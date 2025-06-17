package com.yumcart.testing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import com.stripe.exception.StripeException;
import com.yumcart.Exception.CartException;
import com.yumcart.Exception.OrderException;
import com.yumcart.Exception.RestaurantException;
import com.yumcart.Exception.UserException;
import com.yumcart.controller.OrderController;
import com.yumcart.model.*;
import com.yumcart.request.CreateOrderRequest;
import com.yumcart.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderController orderController;

    private User user;
    private CreateOrderRequest createOrderRequest;
    private PaymentResponse paymentResponse;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setRestaurantId(1L);

        paymentResponse = new PaymentResponse();
        paymentResponse.setPayment_url("http://payment-link.com");

        Order order = new Order();
        order.setId(1L);
        order.setCustomer(user);
        order.setOrderStatus("PENDING");
        
        orders = Collections.singletonList(order);
    }

    @Test
    void testCreateOrder_Success() throws UserException, RestaurantException, CartException, StripeException, OrderException {
        String jwtToken = "valid-jwt";

        when(userService.findUserProfileByJwt(jwtToken)).thenReturn(user);
        when(orderService.createOrder(any(CreateOrderRequest.class), any(User.class))).thenReturn(paymentResponse);

        ResponseEntity<PaymentResponse> response = orderController.createOrder(createOrderRequest, jwtToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("http://payment-link.com", response.getBody().getPayment_url());

        System.out.println("Response: " + response.getBody().getPayment_url());
    }

    
    @Test
    void testGetAllUserOrders_Success() throws UserException, OrderException {
        String jwtToken = "valid-jwt";

        when(userService.findUserProfileByJwt(jwtToken)).thenReturn(user);
        when(orderService.getUserOrders(user.getId())).thenReturn(orders);

        ResponseEntity<List<Order>> response = orderController.getAllUserOrders(jwtToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        System.out.println("Orders Retrieved: " + response.getBody().size());
    }

}
