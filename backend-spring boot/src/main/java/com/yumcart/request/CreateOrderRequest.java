package com.yumcart.request;

import com.yumcart.model.Address;

import lombok.Data;

@Data
public class CreateOrderRequest {
 
	private Long restaurantId;
	
	private Address deliveryAddress;
	
    
}
