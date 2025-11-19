package com.sawari.dev.model;

import com.sawari.dev.dbtypes.PaymentMethod;
import com.sawari.dev.dbtypes.PaymentStatus;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payment_id;

    @Column(nullable = false)
    private Long booking_id;

    @Column(nullable = false)
    private Long user_id;

    @Column(nullable = false)
    private Double base_price = 0.0;

    @Column(nullable = false)
    private Double fine_amount = 0.0;

    @Column(nullable = false)
    private Double total_amount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod payment_method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus payment_status = PaymentStatus.PENDING;

    private Timestamp payment_date;
    private String transaction_id;

    @Column(nullable = false)
    private boolean fine_payment = false;

    public Payment() {}

    public Payment(Long booking_id, Long user_id, Double base_price, Double fine_amount,
                   Double total_amount, PaymentMethod payment_method, PaymentStatus payment_status,
                   Timestamp payment_date, String transaction_id, boolean fine_payment) {
        this.booking_id = booking_id;
        this.user_id = user_id;
        this.base_price = base_price;
        this.fine_amount = fine_amount;
        this.total_amount = total_amount;
        this.payment_method = payment_method;
        this.payment_status = payment_status;
        this.payment_date = payment_date;
        this.transaction_id = transaction_id;
        this.fine_payment = fine_payment;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return payment_id;
    }

    public void setPaymentId(Long payment_id) {
        this.payment_id = payment_id;
    }

    public Long getBookingId() {
        return booking_id;
    }

    public void setBookingId(Long booking_id) {
        this.booking_id = booking_id;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public Double getBasePrice() {
        return base_price;
    }

    public void setBasePrice(Double base_price) {
        this.base_price = base_price;
    }

    public Double getFineAmount() {
        return fine_amount;
    }

    public void setFineAmount(Double fine_amount) {
        this.fine_amount = fine_amount;
    }

    public Double getTotalAmount() {
        return total_amount;
    }

    public void setTotalAmount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public PaymentMethod getPaymentMethod() {
        return payment_method;
    }

    public void setPaymentMethod(PaymentMethod payment_method) {
        this.payment_method = payment_method;
    }

    public PaymentStatus getPaymentStatus() {
        return payment_status;
    }

    public void setPaymentStatus(PaymentStatus payment_status) {
        this.payment_status = payment_status;
    }

    public Timestamp getPaymentDate() {
        return payment_date;
    }

    public void setPaymentDate(Timestamp payment_date) {
        this.payment_date = payment_date;
    }

    public String getTransactionId() {
        return transaction_id;
    }

    public void setTransactionId(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public boolean isFinePayment() {
        return fine_payment;
    }

    public void setFinePayment(boolean fine_payment) {
        this.fine_payment = fine_payment;
    }
}
