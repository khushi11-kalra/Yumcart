package com.yumcart.service;

import com.stripe.exception.StripeException;
import com.yumcart.model.Order;
import com.yumcart.model.PaymentResponse;

public interface PaymentService {
	
	public PaymentResponse generatePaymentLink(Order savedOrder) throws StripeException;

}
