package com.yumcart.request;

import lombok.Data;

@Data
public class CreateIngredientRequest {

    private Long restaurantId;
    private String name;
}
