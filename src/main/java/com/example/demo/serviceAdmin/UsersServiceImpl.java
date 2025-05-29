package com.example.demo.serviceAdmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Roles;
import com.example.demo.entities.Users;
import com.example.demo.repostitoriesAdmin.RolesRepository;
import com.example.demo.repostitoriesAdmin.UsersRepository;

@Service("usersService")
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Override
	public Users findByEmail(String email) {
		return usersRepository.findByEmail(email);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Users users = usersRepository.findByEmail(email);
		if (users == null) {
			throw new UsernameNotFoundException("Email not found");
		} else {
			List<GrantedAuthority> authorities = new ArrayList<>();
			Roles role = users.getRoles();
			if (role != null) {
				authorities.add(new SimpleGrantedAuthority(role.getName()));
			}
			return new User(email, users.getPassword(), authorities);
		}
	}

	@Override
	public Iterable<Users> findAll() {
		return usersRepository.findAll();
	}

	@Override
	public Page<Users> findAllUsersExceptAdmin(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        return usersRepository.findByRolesNameNot("ROLE_ADMIN", pageable);
	}

	@Override
	public boolean save(Users users) {
		try {
			usersRepository.save(users);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Users findById(int id) {
		return usersRepository.findById(id).get();
	}

	@Override
	public boolean detele(int id) {
		try {
			usersRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Page<Users> findByKeyword(String keyword, int size , int page) {
		Pageable pageable = PageRequest.of(page, size);
        return usersRepository.findByFullNameContainingOrEmailContaining(keyword, keyword, pageable);
	}

	@Override
	public List<Users> getUsersByRoles(Integer roleId) {
		return usersRepository.findUserByRoleId(roleId);
	}

	@Override
	public List<Roles> getAllRoles() {
		// TODO Auto-generated method stub
		return rolesRepository.findAllRolesExceptAdmin();
	}

	@Override
	public List<Users> getUsersByStatus(Boolean status) {
		// TODO Auto-generated method stub
		return usersRepository.findUserByStatus(status);
	}

	@Override
    public Page<Users> filterUsers(Integer roleId, Boolean status, Date createdAt, int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return usersRepository.filterUsers(roleId, status, createdAt, keyword, pageable);
    }

	@Override
	public List<Users> getUsersByCreatedAt(Date createdAt) {
		// TODO Auto-generated method stub
		return usersRepository.findUserByCreatedAt(createdAt);
	}

	@Override
	public boolean isSameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		cal1.set(Calendar.HOUR_OF_DAY, 0);
		cal1.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.SECOND, 0);
		cal1.set(Calendar.MILLISECOND, 0);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		cal2.set(Calendar.HOUR_OF_DAY, 0);
		cal2.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.SECOND, 0);
		cal2.set(Calendar.MILLISECOND, 0);

		return cal1.getTime().equals(cal2.getTime());
	}

	@Override
	public List<Users> findOwner(Integer userId) {

		return usersRepository.findUserByRoleId(userId);
	}

	@Override
	public Page<Users> getUsers(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return usersRepository.findAll(pageable);
	}
	
}
