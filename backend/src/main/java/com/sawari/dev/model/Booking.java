package com.sawari.dev.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//Frontend → API → Java Object → JPA/Hibernate → Database Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long booking_id;

    @Column(nullable = false)
    private Long user_id;

    @Column(nullable = false)
    private Long parking_id;

    @Column(nullable = false)
    private Long slot_id;

    @Column(nullable = false)
    private String vehicle_id;

    @Column(nullable = false)
    private Timestamp booking_date_time;

    @Column(nullable = false)
    private Timestamp expected_starting_time;

    @Column(nullable = false)
    private Timestamp expected_end_time;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal base_price;

    @Column(nullable = true, precision = 10, scale = 2)
    private BigDecimal fine_amount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total_amount;

    @Column(nullable = false, length = 50)
    private String booking_status;
}