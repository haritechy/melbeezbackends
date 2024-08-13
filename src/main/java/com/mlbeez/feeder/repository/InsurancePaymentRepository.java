package com.mlbeez.feeder.repository;

import com.mlbeez.feeder.model.InsurancePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePaymentRepository extends JpaRepository<InsurancePayment,Long> {
}