package com.yumcart.testing;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yumcart.controller.UserController;
import com.yumcart.Exception.GlobalExceptionHandler;
import com.yumcart.Exception.UserException;
import com.yumcart.model.User;
import com.yumcart.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Attach exception handler
                .build();
    }

    @Test
    void testGetUserProfileHandler_Success() throws Exception {
        User mockUser = new User();
        mockUser.setFullName("John Doe");
        mockUser.setEmail("john.doe@example.com");

        when(userService.findUserProfileByJwt(anyString())).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testGetUserProfileHandler_UserNotFound() throws Exception {
        when(userService.findUserProfileByJwt(anyString())).thenThrow(new UserException("User not found"));

        MvcResult result = mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        System.out.println("Response: " + result.getResponse().getContentAsString());
    }
}
