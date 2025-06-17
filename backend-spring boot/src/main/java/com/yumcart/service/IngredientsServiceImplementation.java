package com.yumcart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yumcart.model.IngredientsItem;
import com.yumcart.model.Restaurant;
import com.yumcart.repository.IngredientsItemRepository;

@Service
public class IngredientsServiceImplementation implements IngredientsService {

    @Autowired
    private IngredientsItemRepository ingredientsItemRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Override
    public List<IngredientsItem> findRestaurantsIngredients(Long restaurantId) {
        return ingredientsItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public IngredientsItem createIngredientsItem(Long restaurantId, String ingredientName) throws Exception {
        // Check if the ingredient already exists for the restaurant (ignoring case)
        IngredientsItem isExist = ingredientsItemRepository.findByRestaurantIdAndNameIgnoreCase(restaurantId, ingredientName);
        if (isExist != null) {
            System.out.println("Ingredient already exists: " + ingredientName);
            return isExist;
        }

        // Fetch restaurant details
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        // Create a new ingredient item
        IngredientsItem item = new IngredientsItem();
        item.setName(ingredientName);
        item.setRestaurant(restaurant);

        return ingredientsItemRepository.save(item);
    }

    @Override
    public IngredientsItem updateStock(Long id) throws Exception {
        Optional<IngredientsItem> item = ingredientsItemRepository.findById(id);
        if (item.isEmpty()) {
            throw new Exception("Ingredient not found with id: " + id);
        }

        IngredientsItem ingredient = item.get();
        ingredient.setInStoke(!ingredient.isInStoke()); // Toggle stock status
        return ingredientsItemRepository.save(ingredient);
    }
}
