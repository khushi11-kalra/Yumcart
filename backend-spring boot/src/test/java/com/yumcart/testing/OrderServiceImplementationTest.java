package com.yumcart.testing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.stripe.exception.StripeException;
import com.yumcart.Exception.CartException;
import com.yumcart.Exception.OrderException;
import com.yumcart.Exception.RestaurantException;
import com.yumcart.Exception.UserException;
import com.yumcart.model.*;
import com.yumcart.repository.*;
import com.yumcart.request.CreateOrderRequest;
import com.yumcart.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplementationTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartSerive cartService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderServiceImplementation orderService;

    private User user;
    private Restaurant restaurant;
    private Cart cart;
    private Address address;
    private Order order;
    private OrderItem orderItem;
    private CartItem cartItem;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        // Mock User
        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setAddresses(new ArrayList<>());

        // Mock Address
        address = new Address();
        address.setId(1L);
        address.setState("123 Main St");

        // Mock Restaurant
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        // Mock Cart Item
        cartItem = new CartItem();
        cartItem.setQuantity(2);
        Food food = new Food();
        food.setId(1L);
        food.setPrice(200L);
        cartItem.setFood(food);

        // Mock Cart
        cart = new Cart();
        cart.setId(1L);
        cart.setItems(Collections.singletonList(cartItem));

        // Mock Order
        order = new Order();
        order.setId(1L);
        order.setOrderStatus("PENDING");
        order.setCustomer(user);
        order.setRestaurant(restaurant);
        order.setTotalAmount(400L);

        // Mock Order Request
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setRestaurantId(1L);
        createOrderRequest.setDeliveryAddress(address);
    }

    @Test
    void testCreateOrder_Success() throws UserException, RestaurantException, CartException, StripeException {
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(cartService.findCartByUserId(1L)).thenReturn(cart);
        when(cartService.calculateCartTotals(cart)).thenReturn(400L);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Fix: Ensure PaymentResponse contains a valid payment link
        PaymentResponse mockPaymentResponse = new PaymentResponse();
        mockPaymentResponse.setPayment_url("payment-link");

        when(paymentService.generatePaymentLink(any(Order.class))).thenReturn(mockPaymentResponse);

        PaymentResponse response = orderService.createOrder(createOrderRequest, user);

        assertNotNull(response);
        assertEquals("payment-link", response.getPayment_url());
    }

    @Test
    void testCreateOrder_RestaurantNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RestaurantException.class, () ->
                orderService.createOrder(createOrderRequest, user));

        assertEquals("Restaurant not found with id 1", exception.getMessage());
    }

    @Test
    void testCancelOrder_Success() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderException.class, () ->
                orderService.cancelOrder(1L));

        assertEquals("Order not found with the id 1", exception.getMessage());
    }

    @Test
    void testFindOrderById_Success() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.findOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
    }

    @Test
    void testFindOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderException.class, () ->
                orderService.findOrderById(1L));

        assertEquals("Order not found with the id 1", exception.getMessage());
    }

    @Test
    void testGetUserOrders() throws OrderException {
        List<Order> orders = Arrays.asList(order);

        when(orderRepository.findAllUserOrders(1L)).thenReturn(orders);

        List<Order> retrievedOrders = orderService.getUserOrders(1L);

        assertNotNull(retrievedOrders);
        assertEquals(1, retrievedOrders.size());
    }

    @Test
    void testGetOrdersOfRestaurant() throws OrderException, RestaurantException {
        List<Order> orders = Arrays.asList(order);

        when(orderRepository.findOrdersByRestaurantId(1L)).thenReturn(orders);

        List<Order> retrievedOrders = orderService.getOrdersOfRestaurant(1L, "PENDING");

        assertNotNull(retrievedOrders);
        assertEquals(1, retrievedOrders.size());
    }

    @Test
    void testUpdateOrder_Success() throws OrderException {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(1L, "DELIVERED");

        assertNotNull(updatedOrder);
        assertEquals("DELIVERED", updatedOrder.getOrderStatus());
    }

    @Test
    void testUpdateOrder_InvalidStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(OrderException.class, () ->
                orderService.updateOrder(1L, "INVALID_STATUS"));

        assertEquals("Please Select A Valid Order Status", exception.getMessage());
    }
}
