package com.mlbeez.feeder.service;

import com.mlbeez.feeder.model.InsurancePayment;
import com.mlbeez.feeder.repository.InsurancePaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsurancePaymentService {

    @Autowired
    InsurancePaymentRepository insurancePaymentRepository;

    public InsurancePayment storePayment(InsurancePayment insurancePayment)
    {
        return insurancePaymentRepository.save(insurancePayment);
    }
}