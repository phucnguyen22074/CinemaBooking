package com.example.demo.serviceAdmin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Roles;
import com.example.demo.repostitoriesAdmin.RolesRepository;

@Service("rolesService")
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RolesRepository roleRepository;

	@Override
	public Iterable<Roles> findAll() {

		return roleRepository.findAll();
	}

	@Override
	public Roles findById(int id) {

		return roleRepository.findById(id).get();
	}

	@Override
	public boolean save(Roles roles) {
		try {
			if (!roles.getName().startsWith("ROLE_")) {
				roles.setName("ROLE_" + roles.getName());
			}
			roleRepository.save(roles);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Roles> findAllRolesExceptAdmin() {
		return roleRepository.findAllRolesExceptAdmin();
	}

	@Override
	public boolean detele(int id) {
		try {

			roleRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// Lọc roles theo roleId
    public List<Roles> filterRoles(Integer roleId) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("Invalid roleId");
        }
        return roleRepository.findByRoleId(roleId);
    }

	@Override
	public List<Roles> findAllRoles() {
		// TODO Auto-generated method stub
		return (List<Roles>) roleRepository.findAll();
	}


}
