package com.library.service;


import com.library.dto.DepositResponse;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String apikey;

    private final UserService userService;

    public PaymentService(UserService userService,
                          @Value("${stripe.api.key}") String apikey) {
        this.userService = userService;
        Stripe.apiKey = apikey;
    }
    public DepositResponse createTestDeposit(Long userId, String amountInCents, String currency, String description, String token) {
        System.out.println("token: " + token);
        try {
           ChargeCreateParams params = ChargeCreateParams.builder()
                   .setAmount(Long.parseLong(amountInCents))
                   .setCurrency(currency)
                   .setDescription(description)
                   .setSource(token)
                   .build();
           Charge charge = Charge.create(params);

           if("succeeded".equals(charge.getStatus())){
               userService.addCredits(userId, Integer.parseInt(amountInCents) / 100);
           }
           return new DepositResponse(charge.getId(), charge.getStatus(), (int)(Long.parseLong(amountInCents)/100));
       } catch (Exception e) {
           throw new RuntimeException("NONON" + e);
       }
    }

}
