package com.example.demo.servicesDTO;

import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entities.Roles;
import com.example.demo.entities.Users;
import com.example.demo.repositories.UserRepository;

@Service
public class AccountDTOServiceImpl implements AccountDTOService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Users save(UsersDTO userDTO) {
        try {
            Random random = new Random();
            Users account = modelMapper.map(userDTO, Users.class);
            account.setStatus(false);
            account.setSecurityCode(String.valueOf(random.nextInt(1000, 10000)));
            account.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Dùng BCryptPasswordEncoder
            Roles role = new Roles();
            role.setRoleId(1);
            account.setRoles(role);
            return userRepository.save(account);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean login(String email, String password) {
        try {
            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                throw new IllegalArgumentException("Email và mật khẩu không được để trống.");
            }

            Users account = userRepository.findByEmail(email);
            if (account == null) {
                return false; // Không tồn tại tài khoản
            }

            boolean isPasswordMatch = passwordEncoder.matches(password, account.getPassword());
            return isPasswordMatch && account.isStatus();
        } catch (IllegalArgumentException e) {
            System.err.println("Lỗi đầu vào: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UsersDTO find(int id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UsersDTO.class))
                .orElse(null);
    }

    @Override
    public UsersDTO findByEmail(String email) {
        Users user = userRepository.findByEmail(email);
        return (user != null) ? modelMapper.map(user, UsersDTO.class) : null;
    }

    @Override
    public Users findByEmailAccount(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean update(Users account) {
        try {
            if (account == null || account.getUserId() == null) {
                throw new IllegalArgumentException("Tài khoản không hợp lệ");
            }
            userRepository.save(account);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(UsersDTO accountDTO) {
        try {
            Users account = userRepository.findByEmail(accountDTO.getEmail());
            if (account == null) {
                return false;
            }

            if (accountDTO.getFullName() != null && !accountDTO.getFullName().isEmpty()) {
                account.setFullName(accountDTO.getFullName());
            }

            if (accountDTO.getPassword() != null && !accountDTO.getPassword().isEmpty()) {
                account.setPassword(passwordEncoder.encode(accountDTO.getPassword()));
            }

            if (accountDTO.getDob() != null) {
                account.setDob(accountDTO.getDob());
            }

            userRepository.save(account);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
