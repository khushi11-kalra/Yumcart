package com.yumcart.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yumcart.Exception.UserException;
import com.yumcart.config.JwtProvider;
import com.yumcart.domain.USER_ROLE;
import com.yumcart.model.Cart;
import com.yumcart.model.User;
import com.yumcart.repository.CartRepository;
import com.yumcart.repository.UserRepository;
import com.yumcart.request.LoginRequest;
import com.yumcart.response.AuthResponse;
import com.yumcart.service.CustomeUserServiceImplementation;
import com.yumcart.service.UserService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private JwtProvider jwtProvider;
	private CustomeUserServiceImplementation customUserDetails;
	private CartRepository cartRepository;


    private UserService userService;

	public AuthController(UserRepository userRepository, 
			PasswordEncoder passwordEncoder, 
			JwtProvider jwtProvider,
			CustomeUserServiceImplementation customUserDetails,
			CartRepository cartRepository,
			UserService userService
			) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtProvider = jwtProvider;
		this.customUserDetails = customUserDetails;
		this.cartRepository=cartRepository;
		this.userService=userService;

	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@Valid @RequestBody User user) throws UserException {

		String email = user.getEmail();
		String password = user.getPassword();
		String fullName = user.getFullName();
		USER_ROLE role=user.getRole();

		User isEmailExist = userRepository.findByEmail(email);

		if (isEmailExist!=null) {

			throw new UserException("Email Is Already Used With Another Account");
		}

		// Create new user
		User createdUser = new User();
		createdUser.setEmail(email);
		createdUser.setFullName(fullName);
		createdUser.setPassword(passwordEncoder.encode(password));
		createdUser.setRole(role);

		User savedUser = userRepository.save(createdUser);
		
		Cart cart = new Cart();
		cart.setCustomer(savedUser);
		Cart savedCart = cartRepository.save(cart);
//		savedUser.setCart(savedCart);

		List<GrantedAuthority> authorities=new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority(role.toString()));


		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password,authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtProvider.generateToken(authentication);

		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("Register Success");
		authResponse.setRole(savedUser.getRole());

		return new ResponseEntity<>(authResponse, HttpStatus.OK);

	}

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {

		String username = loginRequest.getEmail();
		String password = loginRequest.getPassword();

		System.out.println(username + " ----- " + password);

		Authentication authentication = authenticate(username, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtProvider.generateToken(authentication);
		AuthResponse authResponse = new AuthResponse();

		authResponse.setMessage("Login Success");
		authResponse.setJwt(token);
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();


		String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();


		authResponse.setRole(USER_ROLE.valueOf(roleName));

		return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
	}

	private Authentication authenticate(String username, String password) {
		UserDetails userDetails = customUserDetails.loadUserByUsername(username);

		System.out.println("sign in userDetails - " + userDetails);

		if (userDetails == null) {
			System.out.println("sign in userDetails - null " + userDetails);
			throw new BadCredentialsException("Invalid username or password");
		}
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			System.out.println("sign in userDetails - password not match " + userDetails);
			throw new BadCredentialsException("Invalid username or password");
		}
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	
}
