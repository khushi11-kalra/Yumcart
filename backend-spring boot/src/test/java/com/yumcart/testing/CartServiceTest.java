package com.yumcart.testing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yumcart.Exception.CartException;
import com.yumcart.Exception.CartItemException;
import com.yumcart.Exception.FoodException;
import com.yumcart.Exception.UserException;
import com.yumcart.model.Cart;
import com.yumcart.model.CartItem;
import com.yumcart.model.Food;
import com.yumcart.model.User;
import com.yumcart.repository.CartItemRepository;
import com.yumcart.repository.CartRepository;
import com.yumcart.repository.foodRepository;
import com.yumcart.request.AddCartItemRequest;
import com.yumcart.service.CartServiceImplementation;
import com.yumcart.service.UserService;

@ExtendWith(MockitoExtension.class) 
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private foodRepository menuItemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartServiceImplementation cartService;

    private User mockUser;
    private Cart mockCart;
    private CartItem mockCartItem;
    private Food mockFood;

    @BeforeEach
    void setUp() {
        // Mock User
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("Khushi Kalra");

        // Mock Food
        mockFood = new Food();
        mockFood.setId(1L);
        mockFood.setName("Pizza");
        mockFood.setPrice((long) 500);

        // Mock CartItem
        mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setQuantity(2);
        mockCartItem.setFood(mockFood);
        mockCartItem.setTotalPrice(mockFood.getPrice() * 2);

        // Mock Cart
        mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setCustomer(mockUser);
        mockCart.setItems(new ArrayList<>());  // Ensure the list is initialized
        mockCart.setTotal(0L);
    }

    @Test
    void testFindCartByUserId_Success() throws CartException, UserException {
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(mockCart));

        Cart result = cartService.findCartByUserId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindCartByUserId_NotFound() {
        when(cartRepository.findByCustomer_Id(2L)).thenReturn(Optional.empty());

        assertThrows(CartException.class, () -> cartService.findCartByUserId(2L));
    }

    @Test
    void testAddItemToCart_Success() throws UserException, FoodException, CartException, CartItemException {
        AddCartItemRequest request = new AddCartItemRequest();
        request.setMenuItemId(1L);
        request.setQuantity(2);

        when(userService.findUserProfileByJwt("mock-jwt")).thenReturn(mockUser);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(mockFood));
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(mockCart));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(mockCartItem);

        CartItem addedItem = cartService.addItemToCart(request, "mock-jwt");

        assertNotNull(addedItem);
        assertEquals(2, addedItem.getQuantity());
        assertEquals(mockFood, addedItem.getFood());

        verify(cartRepository, times(1)).save(mockCart);
    }

    @Test
    void testRemoveItemFromCart_Success() throws UserException, CartException, CartItemException {
        mockCart.getItems().add(mockCartItem);
        when(userService.findUserProfileByJwt("mock-jwt")).thenReturn(mockUser);
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(mockCart));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(mockCartItem));

        cartService.removeItemFromCart(1L, "mock-jwt");

        assertTrue(mockCart.getItems().isEmpty());
    }

    @Test
    void testCalculateCartTotal() throws UserException {
        mockCart.getItems().add(mockCartItem);
        
        // Use lenient() to avoid unnecessary stubbing errors
        lenient().when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(mockCart));

        Long total = cartService.calculateCartTotals(mockCart);
        assertEquals(1000, total); // 500 * 2
    }

    @Test
    void testClearCart() throws CartException, UserException {
        mockCart.getItems().add(mockCartItem);  // Ensure cart has items before clearing
        when(cartRepository.findByCustomer_Id(1L)).thenReturn(Optional.of(mockCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart); // Mock save behavior

        Cart clearedCart = cartService.clearCart(1L);

        assertNotNull(clearedCart, "Cleared cart should not be null");
        assertNotNull(clearedCart.getItems(), "Cleared cart items list should not be null");
        assertTrue(clearedCart.getItems().isEmpty(), "Cart items should be empty after clearing");
        assertEquals(0L, clearedCart.getTotal(), "Total should be 0 after clearing");
    }

}
