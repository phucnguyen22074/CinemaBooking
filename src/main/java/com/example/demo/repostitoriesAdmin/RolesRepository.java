package com.example.demo.repostitoriesAdmin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

	@Query("SELECT r FROM Roles r WHERE r.id != 2")
	public List<Roles> findAllRolesExceptAdmin();

	@Query("SELECT r FROM Roles r")
	List<Roles> findAllRoles();

	@Query("SELECT r FROM Roles r WHERE r.roleId = :roleId")
	List<Roles> findByRoleId(@Param("roleId") Integer roleId);
}
