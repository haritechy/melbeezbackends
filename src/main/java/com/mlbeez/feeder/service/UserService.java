//package com.mlbeez.feeder.service;
//
//import com.mlbeez.feeder.controller.Stripe;
//import com.mlbeez.feeder.model.User;
//import com.mlbeez.feeder.repository.UserRepository;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Customer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//
//
//    public User storeUser(User user)throws StripeException
//    {
//
//        User saveUser=userRepository.save(user);
////        Customer stripeCustomer=stripeService.customerCreateParams(saveUser.getEmail(), saveUser.getName());
//        User userEntity=new User();
//        userEntity.setCustomerId(stripeCustomer.getId());
//        return saveUser;
//
//    }
//}