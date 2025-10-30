package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Pricing;

public interface PricingRepository extends JpaRepository<Pricing, Long>{
}
