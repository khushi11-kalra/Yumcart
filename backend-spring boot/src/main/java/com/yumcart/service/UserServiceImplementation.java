package com.yumcart.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.yumcart.Exception.UserException;
import com.yumcart.config.JwtProvider;
import com.yumcart.model.User;
import com.yumcart.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;
    private JwtProvider jwtProvider;

    public UserServiceImplementation(
            UserRepository userRepository,
            JwtProvider jwtProvider) {
        
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new UserException("User does not exist with email " + email);
        }
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByEmail(String username) throws UserException {
        User user = userRepository.findByEmail(username);
        if (user != null) {
            return user;
        }
        throw new UserException("User does not exist with username " + username);
    }
}
