package com.yumcart.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.yumcart.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	
	public User findByEmail(String username);

}
