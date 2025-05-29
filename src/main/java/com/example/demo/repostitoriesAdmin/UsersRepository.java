package com.example.demo.repostitoriesAdmin;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Users;

public interface UsersRepository extends JpaRepository<Users, Integer>, PagingAndSortingRepository<Users, Integer> {

	@Query("from Users where email = :email")
	public Users findByEmail(@Param("email") String email);

	@Query("SELECT a FROM Users a where a.roles.roleId != 2")
	public List<Users> findAllUsersExceptAdmin();

	@Query("SELECT a From Users a where a.fullName like %:keyword%")
	public List<Users> findByKeyword(@Param("keyword") String keyword);

	@Query("SELECT a FROM Users a WHERE a.roles.roleId = :roleId AND :roleId <> 2")
	public List<Users> findUserByRoleId(@Param("roleId") Integer roleId);

	@Query("SELECT a FROM Users a WHERE a.status = :status")
	public List<Users> findUserByStatus(@Param("status") Boolean status);

	@Query("SELECT a FROM Users a WHERE DATE(a.createdAt) = :createdAt")
	public List<Users> findUserByCreatedAt(@Param("createdAt") Date createdAt);

	@Query("SELECT a FROM Users a WHERE " 
	        + "(a.roles.roleId = :roleId OR :roleId IS NULL) "
	        + "AND a.roles.roleId <> 2 "
	        + "AND (a.status = :status OR :status IS NULL) "
	        + "AND (DATE(a.createdAt) = :createdAt OR :createdAt IS NULL) "
	        + "AND (:keyword IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	public Page<Users> filterUsers(
	        @Param("roleId") Integer roleId,
	        @Param("status") Boolean status,
	        @Param("createdAt") Date createdAt,
	        @Param("keyword") String keyword,
	        Pageable pageable);
	
	public Page<Users> findByRolesNameNot(String roleName, Pageable pageable);
	
	public Page<Users> findByFullNameContainingOrEmailContaining(String fullName, String email, Pageable pageable);

}
