package com.yumcart.service;

import java.util.List;

import com.yumcart.model.IngredientsItem;

public interface IngredientsService {

    public List<IngredientsItem> findRestaurantsIngredients(Long restaurantId);

    public IngredientsItem createIngredientsItem(Long restaurantId, String ingredientName) throws Exception;

    public IngredientsItem updateStock(Long id) throws Exception;
}
