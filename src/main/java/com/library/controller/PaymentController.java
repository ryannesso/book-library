package com.library.controller;

import com.library.config.JwtAuthenticationFilter;
import com.library.config.JwtService;
import com.library.dto.DepositResponse;
import com.library.dto.request.DepositRequest;
import com.library.service.PaymentService;
import com.stripe.model.Charge;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtService  jwtService;
    private final JwtAuthenticationFilter jwtFilter;
    public PaymentController(PaymentService paymentService, JwtService jwtService, JwtAuthenticationFilter jwtFilter) {
        this.paymentService = paymentService;
        this.jwtService = jwtService;
        this.jwtFilter = jwtFilter;
    }

    @PostMapping("/deposit")
    public DepositResponse createDeposit(@RequestBody DepositRequest request, HttpServletRequest httpRequest) {
        String jwt = jwtFilter.extractTokenFromRequest(httpRequest);
        Long userId = jwtService.extractUserId(jwt);
        System.out.println("userId = " + userId);
        System.out.println("Received Request: " + request);
        System.out.println("token: " + request.token());
        System.out.println("userId = " + userId);
        return paymentService.createTestDeposit(userId, String.valueOf(request.amount()), request.currency(), "Deposit for user" + userId, request.token());
    }
}
