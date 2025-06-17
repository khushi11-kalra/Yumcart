package com.yumcart.testing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.yumcart.Exception.CartException;
import com.yumcart.Exception.CartItemException;
import com.yumcart.Exception.FoodException;
import com.yumcart.Exception.UserException;
import com.yumcart.controller.CartController;
import com.yumcart.model.Cart;
import com.yumcart.model.CartItem;
import com.yumcart.model.User;
import com.yumcart.request.AddCartItemRequest;
import com.yumcart.request.UpdateCartItemRequest;
import com.yumcart.service.CartSerive;  // Corrected from CartSerive
import com.yumcart.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CartSerive cartService;  // Corrected from CartSerive

    @Mock
    private UserService userService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void testAddItemToCart() throws Exception {
        AddCartItemRequest request = new AddCartItemRequest();
        CartItem cartItem = new CartItem();
        when(cartService.addItemToCart(any(AddCartItemRequest.class), anyString())).thenReturn(cartItem);

        mockMvc.perform(put("/api/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{\"foodId\": 1, \"quantity\": 2}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCartItemQuantity() throws Exception {
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setCartItemId(1L);
        request.setQuantity(3);
        CartItem cartItem = new CartItem();
        when(cartService.updateCartItemQuantity(1L, 3)).thenReturn(cartItem);

        mockMvc.perform(put("/api/cart-item/update")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content("{\"cartItemId\": 1, \"quantity\": 3}"))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        Cart cart = new Cart();
        when(cartService.removeItemFromCart(anyLong(), anyString())).thenReturn(cart);

        mockMvc.perform(delete("/api/cart-item/1/remove")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void testCalculateCartTotals() throws Exception {
        User user = new User();
        user.setId(1L);
        Cart cart = new Cart();
        when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
        when(cartService.findCartByUserId(1L)).thenReturn(cart);
        when(cartService.calculateCartTotals(cart)).thenReturn((long) 100.0);

        mockMvc.perform(get("/api/cart/total")
                .header("Authorization", "Bearer token")
                .param("cartId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindUserCart() throws Exception {
        User user = new User();
        user.setId(1L);
        Cart cart = new Cart();
        when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
        when(cartService.findCartByUserId(1L)).thenReturn(cart);

        mockMvc.perform(get("/api/cart/")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void testClearCart() throws Exception {
        User user = new User();
        user.setId(1L);
        Cart cart = new Cart();
        when(userService.findUserProfileByJwt(anyString())).thenReturn(user);
        when(cartService.clearCart(1L)).thenReturn(cart);

        mockMvc.perform(put("/api/cart/clear")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }
}
