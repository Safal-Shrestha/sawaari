package com.sawari.dev.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sawari.dev.model.Users;
import com.sawari.dev.repository.UsersRepository;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new CustomUserDetails(
            user.getUserId(),
            user.getUserName(),
            user.getPassword(),
            user.getRole()
        );
    }

    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException{
        Users user = usersRepository.findById(userId).orElse(null);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new CustomUserDetails(user);
    }

    public List<Users> findAll() {
        List<Users> list = new ArrayList<>();
        usersRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    public Users findOne(String username) {
        return usersRepository.findByEmail(username);
    }
}
