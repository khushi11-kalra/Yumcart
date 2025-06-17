package com.yumcart.service;

import com.yumcart.Exception.CartException;
import com.yumcart.Exception.CartItemException;
import com.yumcart.Exception.FoodException;
import com.yumcart.Exception.UserException;
import com.yumcart.model.Cart;
import com.yumcart.model.CartItem;
import com.yumcart.model.Food;
import com.yumcart.model.User;
import com.yumcart.request.AddCartItemRequest;
import com.yumcart.request.UpdateCartItemRequest;

public interface CartSerive {

	public CartItem addItemToCart(AddCartItemRequest req, String jwt) throws UserException, FoodException, CartException, CartItemException;

	public CartItem updateCartItemQuantity(Long cartItemId,int quantity) throws CartItemException;

	public Cart removeItemFromCart(Long cartItemId, String jwt) throws UserException, CartException, CartItemException;

	public Long calculateCartTotals(Cart cart) throws UserException;
	
	public Cart findCartById(Long id) throws CartException;
	
	public Cart findCartByUserId(Long userId) throws CartException, UserException;
	
	public Cart clearCart(Long userId) throws CartException, UserException;
	

	

}
