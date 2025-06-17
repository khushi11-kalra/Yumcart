package com.yumcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.yumcart.model.Order;
import com.yumcart.model.PaymentResponse;
import com.yumcart.service.PaymentService;

@RestController
@RequestMapping("/api")
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/{orderId}/payment")
	public ResponseEntity<PaymentResponse> generatePaymentLink(@RequestBody Order order) 
			throws StripeException{
		
		PaymentResponse res = paymentService.generatePaymentLink(order);
		
		return new ResponseEntity<PaymentResponse>(res,HttpStatus.ACCEPTED);
	}

}
