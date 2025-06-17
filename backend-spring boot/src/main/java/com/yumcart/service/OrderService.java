package com.yumcart.service;

import java.util.List;

import com.stripe.exception.StripeException;
import com.yumcart.Exception.CartException;
import com.yumcart.Exception.OrderException;
import com.yumcart.Exception.RestaurantException;
import com.yumcart.Exception.UserException;
import com.yumcart.model.Order;
import com.yumcart.model.PaymentResponse;
import com.yumcart.model.User;
import com.yumcart.request.CreateOrderRequest;

public interface OrderService {
	
	 public PaymentResponse createOrder(CreateOrderRequest order, User user) throws UserException, RestaurantException, CartException, StripeException;
	 
	 public Order updateOrder(Long orderId, String orderStatus) throws OrderException;
	 
	 public void cancelOrder(Long orderId) throws OrderException;
	 
	 public List<Order> getUserOrders(Long userId) throws OrderException;
	 
	 public List<Order> getOrdersOfRestaurant(Long restaurantId,String orderStatus) throws OrderException, RestaurantException;
	 

}
