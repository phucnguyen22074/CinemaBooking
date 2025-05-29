package com.example.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.demo.entities.Users;

public interface AccountService extends UserDetailsService{

	public boolean save(Users users);
	public boolean sendActivationEmail(Users account);
	public Users findByEmail(String email);
}
