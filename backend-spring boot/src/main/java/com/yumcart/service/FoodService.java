package com.yumcart.service;

import java.util.List;

import com.yumcart.Exception.FoodException;
import com.yumcart.Exception.RestaurantException;
import com.yumcart.model.Category;
import com.yumcart.model.Food;
import com.yumcart.model.Restaurant;
import com.yumcart.request.CreateFoodRequest;

public interface FoodService {

	public Food createFood(CreateFoodRequest req,Category category,
						   Restaurant restaurant) throws FoodException, RestaurantException;

	void deleteFood(Long foodId) throws FoodException;
	
	public List<Food> getRestaurantsFood(Long restaurantId,
			boolean isVegetarian, boolean isNonveg, boolean isSeasonal,String foodCategory) throws FoodException;
	
	public List<Food> searchFood(String keyword);
	
	public Food findFoodById(Long foodId) throws FoodException;

	public Food updateAvailibilityStatus(Long foodId) throws FoodException;
}
