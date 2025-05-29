package com.example.demo.servicesDTO;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entities.Users;

public interface AccountDTOService {
	public Users save(UsersDTO userDTO);
	public UsersDTO find(int id);
	public UsersDTO findByEmail(String email);
	public Users findByEmailAccount(String email);
	public boolean login(String email, String password);
	public boolean update(Users user);
	public boolean update(UsersDTO accountDTO);
}
