package com.yumcart.testing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.yumcart.Exception.RestaurantException;
import com.yumcart.dto.RestaurantDto;
import com.yumcart.model.Address;
import com.yumcart.model.ContactInformation;
import com.yumcart.model.Restaurant;
import com.yumcart.model.User;
import com.yumcart.repository.AddressRepository;
import com.yumcart.repository.RestaurantRepository;
import com.yumcart.repository.UserRepository;
import com.yumcart.request.CreateRestaurantRequest;
import com.yumcart.service.RestaurantServiceImplementation;
import com.yumcart.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RestaurantServiceImplementation restaurantService;

    private Restaurant restaurant;
    private User user;
    private CreateRestaurantRequest createRequest;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Italian Bistro");
        restaurant.setCuisineType("Italian");
        restaurant.setDescription("Best Italian food in town");

        Address address = new Address();
        address.setCity("New York");
        address.setCountry("USA");
        address.setFullName("Street 1");
        address.setPostalCode("10001");
        address.setState("NY");
        address.setStreetAddress("123 Main St");
        restaurant.setAddress(address);

        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setFavorites(new ArrayList<>());

        createRequest = new CreateRestaurantRequest();
        createRequest.setAddress(address);
        createRequest.setCuisineType("Italian");
        createRequest.setDescription("Best Italian food in town");
        createRequest.setImages(new ArrayList<>());
        createRequest.setName("Italian Bistro");
        createRequest.setOpeningHours("10 AM - 10 PM");

        ContactInformation contactInfo = new ContactInformation();
        contactInfo.setEmail("contact@example.com");
        contactInfo.setMobile("9876543210");
        restaurant.setContactInformation(contactInfo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        createRequest.setRegistrationDate(LocalDateTime.parse("2024-03-26 00:00:00", formatter));

        restaurant.setOwner(user);
    }

    @Test
    void testCreateRestaurant() {
        when(addressRepository.save(any(Address.class))).thenReturn(restaurant.getAddress());
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        Restaurant createdRestaurant = restaurantService.createRestaurant(createRequest, user);

        assertNotNull(createdRestaurant);
        assertEquals("Italian Bistro", createdRestaurant.getName());
    }

    @Test
    void testFindRestaurantById_Success() throws RestaurantException {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant foundRestaurant = restaurantService.findRestaurantById(1L);

        assertNotNull(foundRestaurant);
        assertEquals("Italian Bistro", foundRestaurant.getName());

        verify(restaurantRepository, times(1)).findById(1L);
    }



    @Test
    void testDeleteRestaurant_Success() throws RestaurantException {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantRepository).delete(any(Restaurant.class));

        assertDoesNotThrow(() -> restaurantService.deleteRestaurant(1L));

        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).delete(restaurant);
    }

   

    @Test
    void testGetAllRestaurants() {
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(restaurant);

        when(restaurantRepository.findAll()).thenReturn(restaurantList);

        List<Restaurant> result = restaurantService.getAllRestaurant();

        assertEquals(1, result.size());
        assertEquals("Italian Bistro", result.get(0).getName());

        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    void testSearchRestaurant() {
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(restaurant);

        when(restaurantRepository.findBySearchQuery("Italian")).thenReturn(restaurantList);

        List<Restaurant> result = restaurantService.searchRestaurant("Italian");

        assertEquals(1, result.size());
        assertEquals("Italian Bistro", result.get(0).getName());

        verify(restaurantRepository, times(1)).findBySearchQuery("Italian");
    }

    @Test
    void testAddToFavorites() throws RestaurantException {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(userRepository.save(user)).thenReturn(user);

        RestaurantDto favoriteDto = restaurantService.addToFavorites(1L, user);

        assertNotNull(favoriteDto);
        assertEquals("Italian Bistro", favoriteDto.getTitle());
        assertEquals(1, user.getFavorites().size());

        verify(restaurantRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateRestaurantStatus() throws RestaurantException {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        Restaurant updatedRestaurant = restaurantService.updateRestaurantStatus(1L);

        assertNotNull(updatedRestaurant);
        assertTrue(updatedRestaurant.isOpen()); // Assuming initial value was false

        verify(restaurantRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }
}
