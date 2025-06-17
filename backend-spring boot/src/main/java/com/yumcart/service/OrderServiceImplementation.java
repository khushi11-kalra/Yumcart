package com.yumcart.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.yumcart.Exception.CartException;
import com.yumcart.Exception.OrderException;
import com.yumcart.Exception.RestaurantException;
import com.yumcart.Exception.UserException;
import com.yumcart.model.Address;
import com.yumcart.model.Cart;
import com.yumcart.model.CartItem;
import com.yumcart.model.Order;
import com.yumcart.model.OrderItem;
import com.yumcart.model.PaymentResponse;
import com.yumcart.model.Restaurant;
import com.yumcart.model.User;
import com.yumcart.repository.AddressRepository;
import com.yumcart.repository.OrderItemRepository;
import com.yumcart.repository.OrderRepository;
import com.yumcart.repository.RestaurantRepository;
import com.yumcart.repository.UserRepository;
import com.yumcart.request.CreateOrderRequest;

@Service
public class OrderServiceImplementation implements OrderService {

    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private CartSerive cartService;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentSerive;

    @Override
    public PaymentResponse createOrder(CreateOrderRequest order, User user)
            throws UserException, RestaurantException, CartException, StripeException {

        Address shippAddress = order.getDeliveryAddress();
        Address savedAddress = addressRepository.save(shippAddress);

        if (!user.getAddresses().contains(savedAddress)) {
            user.getAddresses().add(savedAddress);
        }

        userRepository.save(user);

        Optional<Restaurant> restaurant = restaurantRepository.findById(order.getRestaurantId());
        if (restaurant.isEmpty()) {
            throw new RestaurantException("Restaurant not found with id " + order.getRestaurantId());
        }

        Order createdOrder = new Order();
        createdOrder.setCustomer(user);
        createdOrder.setDeliveryAddress(savedAddress);
        createdOrder.setCreatedAt(new Date());
        createdOrder.setOrderStatus("PENDING");
        createdOrder.setRestaurant(restaurant.get());

        Cart cart = cartService.findCartByUserId(user.getId());
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFood(cartItem.getFood());
            orderItem.setIngredients(cartItem.getIngredients());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getFood().getPrice() * cartItem.getQuantity());

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(savedOrderItem);
        }

        Long totalPrice = cartService.calculateCartTotals(cart);
        createdOrder.setTotalAmount(totalPrice);
        createdOrder.setItems(orderItems);

        Order savedOrder = orderRepository.save(createdOrder);
        restaurant.get().getOrders().add(savedOrder);
        restaurantRepository.save(restaurant.get());

        PaymentResponse res = paymentSerive.generatePaymentLink(savedOrder);
        return res;
    }

    @Override
    public void cancelOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        if (order == null) {
            throw new OrderException("Order not found with the id " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    public Order findOrderById(Long orderId) throws OrderException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) return order.get();
        throw new OrderException("Order not found with the id " + orderId);
    }

    @Override
    public List<Order> getUserOrders(Long userId) throws OrderException {
        return orderRepository.findAllUserOrders(userId);
    }

    @Override
    public List<Order> getOrdersOfRestaurant(Long restaurantId, String orderStatus)
            throws OrderException, RestaurantException {

        List<Order> orders = orderRepository.findOrdersByRestaurantId(restaurantId);

        if (orderStatus != null) {
            orders = orders.stream()
                    .filter(order -> order.getOrderStatus().equals(orderStatus))
                    .collect(Collectors.toList());
        }

        return orders;
    }

    @Override
    public Order updateOrder(Long orderId, String orderStatus) throws OrderException {
        Order order = findOrderById(orderId);

        if (orderStatus.equals("OUT_FOR_DELIVERY") || orderStatus.equals("DELIVERED") ||
                orderStatus.equals("COMPLETED") || orderStatus.equals("PENDING")) {
            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        } else {
            throw new OrderException("Please Select A Valid Order Status");
        }
    }
}
