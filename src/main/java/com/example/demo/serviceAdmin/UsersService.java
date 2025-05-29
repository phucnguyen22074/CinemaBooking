package com.example.demo.serviceAdmin;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.demo.entities.Roles;
import com.example.demo.entities.Users;

public interface UsersService extends UserDetailsService {
	
	public Users findByEmail(String email);
	
	public Iterable<Users> findAll();
	
	public Page<Users> findAllUsersExceptAdmin(int page, int size);
	
	public List<Users> findOwner(Integer userId);
	
	public boolean save(Users users);
	
	public Users findById(int id);
	
	public boolean detele(int id);
	
	public Page<Users> findByKeyword(String keyword,int page, int size);
	
	//filter Users By Role And Status
	public List<Users> getUsersByRoles(Integer roleId);
	
	public List<Users> getUsersByStatus(Boolean status);
	
	public List<Users> getUsersByCreatedAt(Date createdAt);
	
	public List<Roles> getAllRoles();
	
	public Page<Users> filterUsers(Integer roleId, Boolean status, Date createdAt,int page, int size,String keyword);
	
	public boolean isSameDay(Date date1, Date date2);
	//Paginated
	public Page<Users> getUsers(int page, int size);
	
}
