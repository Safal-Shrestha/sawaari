package com.sawari.dev.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Payment;
import com.sawari.dev.repository.PaymentRepository;

@RestController
@RequestMapping("/api")
public class GetPaymentController {

    private final PaymentRepository paymentRepository;

    public GetPaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

   
    @GetMapping("/paymentInfo")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
