package com.yumcart.testing;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;


import com.yumcart.dto.RestaurantDto;
import com.yumcart.model.Restaurant;
import com.yumcart.model.User;
import com.yumcart.service.RestaurantService;
import com.yumcart.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private UserService userService;

    @InjectMocks
    private com.yumcart.controller.RestaurantController restaurantController;

    private MockMvc mockMvc;
    private Restaurant restaurant;
    private RestaurantDto restaurantDto;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantController).build();

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Italian Bistro");
        restaurant.setCuisineType("Italian");

        restaurantDto = new RestaurantDto();
        restaurantDto.setTitle("Italian Bistro");

        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
    }

    @Test
    void testFindRestaurantByName() throws Exception {
        List<Restaurant> restaurants = Arrays.asList(restaurant);

        when(restaurantService.searchRestaurant("Italian")).thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants/search")
                .param("keyword", "Italian")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Italian Bistro"));
    }

    @Test
    void testGetAllRestaurants() throws Exception {
        List<Restaurant> restaurants = Arrays.asList(restaurant);

        when(restaurantService.getAllRestaurant()).thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Italian Bistro"));
    }

    @Test
    void testFindRestaurantById_Success() throws Exception {
        when(restaurantService.findRestaurantById(1L)).thenReturn(restaurant);

        mockMvc.perform(get("/api/restaurants/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Italian Bistro"));
    }

    
    @Test
    void testAddToFavorite_Success() throws Exception {
        when(userService.findUserProfileByJwt("valid-jwt")).thenReturn(user);
        when(restaurantService.addToFavorites(1L, user)).thenReturn(restaurantDto);

        mockMvc.perform(put("/api/restaurants/1/add-favorites")
                .header("Authorization", "valid-jwt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Italian Bistro"));
    }

   
}
