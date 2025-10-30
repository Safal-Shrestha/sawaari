package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
