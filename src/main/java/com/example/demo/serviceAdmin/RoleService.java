package com.example.demo.serviceAdmin;

import java.util.List;

import com.example.demo.entities.Roles;

public interface RoleService {
	
	public Iterable<Roles> findAll();
	
	List<Roles> findAllRoles();
	
	public Roles findById(int id);
	
	public boolean detele(int id);
	
	public boolean save(Roles roles);
	
	public List<Roles> findAllRolesExceptAdmin();
	
	public List<Roles> filterRoles(Integer id);
}
