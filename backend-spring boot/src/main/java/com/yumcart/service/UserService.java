package com.yumcart.service;

import java.util.List;

import com.yumcart.Exception.UserException;
import com.yumcart.model.User;

public interface UserService {

	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public User findUserByEmail(String email) throws UserException;

	public List<User> findAllUsers();

}
