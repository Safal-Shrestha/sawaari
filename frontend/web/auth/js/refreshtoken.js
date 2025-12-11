import axios from "https://cdn.jsdelivr.net/npm/axios@1.6.8/dist/esm/axios.min.js";

const refreshTokenService = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true
});

let isRefreshing = false;
let queue = [];

refreshTokenService.interceptors.request.use(config => {
  const token = sessionStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = "Bearer " + token;
  }
  return config;
});

refreshTokenService.interceptors.response.use(
  res => res,
  async err => {
    const original = err.config;

    if (err.response?.status === 401 && !original._retry) {
      original._retry = true;

      if (isRefreshing) {
        return new Promise(resolve => {
          queue.push(token => {
            original.headers.Authorization = `Bearer ${token}`;
            resolve(refreshTokenService(original));
          });
        });
      }

      isRefreshing = true;

      try {
        const res = await refreshTokenService.post("/api/auth/refresh");
        const newToken = res.data.accessToken;

        localStorage.setItem("accessToken", newToken);
        refreshTokenService.defaults.headers.Authorization = `Bearer ${newToken}`;

        queue.forEach(cb => cb(newToken));
        queue = [];

        return api(original);
      } catch (e) {
        localStorage.removeItem("accessToken");
        window.location.href = "/login";
        return Promise.reject(e);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(err);
  }
);

export default refreshTokenService;
