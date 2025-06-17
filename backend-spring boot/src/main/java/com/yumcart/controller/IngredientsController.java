package com.yumcart.controller;

import java.util.List;

import com.yumcart.model.IngredientsItem;
import com.yumcart.request.CreateIngredientRequest;
import com.yumcart.service.IngredientsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ingredients")
public class IngredientsController {

    @Autowired
    private IngredientsService ingredientService;

    @PostMapping()
    public ResponseEntity<IngredientsItem> createIngredient(
            @RequestBody CreateIngredientRequest req) throws Exception {
        IngredientsItem item = ingredientService.createIngredientsItem(req.getRestaurantId(), req.getName());
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<IngredientsItem> updateStock(@PathVariable Long id) throws Exception {
        IngredientsItem item = ingredientService.updateStock(id);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<IngredientsItem>> restaurantsIngredient(
            @PathVariable Long id) throws Exception {
        List<IngredientsItem> items = ingredientService.findRestaurantsIngredients(id);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
