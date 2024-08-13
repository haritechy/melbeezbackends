package com.mlbeez.feeder.controller;

import com.mlbeez.feeder.model.InsurancePayment;
import com.mlbeez.feeder.model.ProductEntity;
import com.mlbeez.feeder.model.User;
import com.mlbeez.feeder.repository.ProductRepository;
import com.mlbeez.feeder.repository.UserRepository;
import com.mlbeez.feeder.service.InsurancePaymentService;

import com.stripe.Stripe;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = {"http://localhost:8081", "http://10.0.2.2:8081", "http://localhost:3005"})

@RequestMapping("/payment-sheet")
public class PaymentSheetController {


    @Value("${stripe.api.key}")
    private String stripeApiKey;
    @Value("${stripe.publishableKey}")
    private String stripePublishableKey;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InsurancePaymentService insurancePaymentService;

    @PostMapping
    public Map<String, String> createSubscription(@RequestBody Map<String, String> Details, InsurancePayment insurancePayment, User user) {

        // Set the Stripe API key
        Stripe.apiKey = stripeApiKey;

        String warrantyId = Details.get("warrantyId");
        String currency = Details.get("currency");
        String paymentMethodId = Details.get("paymentMethodId");
        String name = Details.get("name");
        String email = Details.get("email");
        String monthlyPriceStr = Details.get("monthlyPrice");
        Long monthlyPrice = Long.parseLong(monthlyPriceStr);

        String userIdStr = Details.get("userId");
        Long userId = Long.parseLong(userIdStr);

        try {
            // Create a new customer
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(name)
                    .setEmail(email).build();
            Customer customer = Customer.create(customerParams);

//            user.setUserId(userId);
//            user.setName(name);
//            user.setEmail(email);
//            user.setCustomerId(customer.getId());
//            userRepository.save(user);

            // Create an ephemeral key for the customer
            EphemeralKeyCreateParams ephemeralKeyParams = EphemeralKeyCreateParams.builder()
                    .setStripeVersion("2024-06-20")
                    .setCustomer(customer.getId())
                    .build();
            EphemeralKey ephemeralKey = EphemeralKey.create(ephemeralKeyParams);

            // Find the ProductEntity by warrantyId
            ProductEntity productEntity = productRepository.findByWarrantyId(warrantyId)
                    .orElseThrow(() -> new RuntimeException("Product not found for warrantyId: " + warrantyId));

            String productId = productEntity.getProductId();

            // Create a new recurring price for the product
            PriceCreateParams priceParams = PriceCreateParams.builder()
                    .setUnitAmount(monthlyPrice * 100) // Amount in cents
                    .setCurrency(currency)
                    .setRecurring(
                            PriceCreateParams.Recurring.builder()
                                    .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                    .build()
                    )
                    .setProduct(productId)
                    .build();
            Price price = Price.create(priceParams);

            // Create a subscription
            SubscriptionCreateParams subscriptionParams = SubscriptionCreateParams.builder()
                    .setCustomer(customer.getId())
                    .addItem(
                            SubscriptionCreateParams.Item.builder()
                                    .setQuantity(1L)
                                    .setPrice(price.getId())
                                    .build()
                    )
                    .build();
            Subscription subscription = Subscription.create(subscriptionParams);

            insurancePayment.setSubscriptionId(subscription.getId());
//
            insurancePayment.setCurrency(subscription.getCurrency());
            insurancePayment.setCustomer(subscription.getCustomer());


            insurancePayment.setStatus(subscription.getStatus());
//
            insurancePaymentService.storePayment(insurancePayment);

            // Prepare response data
            Map<String, String> responseData = new HashMap<>();
            responseData.put("subscriptionId", subscription.getId());
            responseData.put("ephemeralKey", ephemeralKey.getSecret());
            responseData.put("customer", "cus_QcJ5rvy9S1Gs05");
            responseData.put("publishableKey", stripePublishableKey);

            return responseData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create subscription", e);
        }
    }

}
//@RestController
//@CrossOrigin(origins = {"http://localhost:8081", "http://10.0.2.2:8081"})
//
//public class Stripe {
//
//    @Autowired
//    ProductRepository productRepository;
//
//    private static final Logger logger = LoggerFactory.getLogger(Stripe.class);
//
//    @PostMapping("/create-checkout-session")
//    public Map<String, String> createCheckoutSession(@RequestBody Map<String, String> warrantyDetails) {
//        logger.debug("Request to create checkout Stripe");
//        String warrantyId=warrantyDetails.get("warrantyId");
//        String currency=warrantyDetails.get("currency");
//        String monthlyPriceStr = warrantyDetails.get("monthlyPrice");
//        Long monthlyPrice = Long.parseLong(monthlyPriceStr);
//
//
//
//
//        Map<String, String> responseData = new HashMap<>();
//        try {
//
//            // Find the ProductEntity by warrantyId
//            ProductEntity productEntity = productRepository.findByWarrantyId(warrantyId)
//                    .orElseThrow(() -> new RuntimeException("Product not found for warrantyId: " + warrantyId));
//
//            String productId = productEntity.getProductId();
//
////             Create a new recurring price for the product
//            PriceCreateParams priceParams = PriceCreateParams.builder()
//                    .setUnitAmount(monthlyPrice.longValue() * 100) // Amount in cents
//                    .setCurrency(currency)
//                    .setRecurring(
//                            PriceCreateParams.Recurring.builder()
//                                    .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
//                                    .build()
//                    )
//                    .setProduct(productId)
//                    .build();
//            Price price = Price.create(priceParams);
//
//            // Create a checkout session for the subscription
//            SessionCreateParams sessionParams = SessionCreateParams.builder()
//                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
//                    .setSuccessUrl("myapp://payment-success")
//                    .setCancelUrl("myapp://payment-cancel")
//                    .addLineItem(
//                            SessionCreateParams.LineItem.builder()
//                                    .setQuantity(1L)
//                                    .setPrice(price.getId())
//                                    .build()
//                    )
//                    .build();
//
//            Session session = Session.create(sessionParams);
//
//            responseData.put("url", session.getUrl());
//        } catch (Exception e) {
//            responseData.put("error", e.getMessage());
//        }
//        return responseData;
//    }
//}