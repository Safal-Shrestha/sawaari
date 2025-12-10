package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sawari.dev.model.Analysis;


public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    @Query(
    value = """
        SELECT COALESCE(SUM(total_amount), 0) 
        FROM booking
        WHERE user_id = :userId
          AND booking_date_time >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
          AND booking_date_time <  DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
        """,
    nativeQuery = true
    )
    Double findMonthlyTotalByUserId(@Param("userId") Long userId);
    }
