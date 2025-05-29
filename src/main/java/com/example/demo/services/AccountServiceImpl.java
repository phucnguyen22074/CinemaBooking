package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;

import com.example.demo.entities.Users;
import com.example.demo.helper.SecurityCodeHelper;
import com.example.demo.repositories.UserRepository;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private MailService mailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        Users users = userRepository.findByEmail(email);
        if (users == null) {
            throw new UsernameNotFoundException("Email không tồn tại trong hệ thống.");
        }
        if (!users.isStatus()) {
            throw new UsernameNotFoundException("Tài khoản của bạn đã bị khóa.");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(users.getRoles().getName()));

        return new User(email, users.getPassword(), authorities);
    }

    @Override
    public boolean sendActivationEmail(Users account) {
        try {
            if (account == null) {
                throw new IllegalArgumentException("Tài khoản không hợp lệ.");
            }

            String SecurityCode = SecurityCodeHelper.gennerate();
            account.setSecurityCode(SecurityCode);
            userRepository.save(account);

            String baseUrl = environment.getProperty("base_url");
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalStateException("base_url không được cấu hình.");
            }

            String url = baseUrl + "account/verify?email=" + account.getEmail() + "&securitycode=" + SecurityCode;
            String content = generateEmailContent(account.getFullName(), url);

            return mailService.send("phucnguyen220704@gmail.com", account.getEmail(), "Kích hoạt tài khoản", content);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Lỗi khi chuẩn bị dữ liệu email: " + e.getMessage());
            return false;
        } catch (MailException e) {
            System.err.println("Lỗi khi gửi email: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Users findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean save(Users users) {
        try {
            if (users == null) {
                throw new IllegalArgumentException("Người dùng không hợp lệ.");
            }
            userRepository.save(users);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu người dùng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String generateEmailContent(String fullName, String url) {
        return "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head><meta charset='UTF-8'><title>Kích Hoạt Tài Khoản</title></head>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4;'>"
                + "<div style='max-width: 600px; margin: auto; background: #fff; padding: 20px;'>"
                + "<h1 style='color: #333;'>Kích Hoạt Tài Khoản</h1>"
                + "<p>Chào " + fullName + ",</p>"
                + "<p>Vui lòng nhấn vào nút dưới đây để kích hoạt tài khoản của bạn:</p>"
                + "<p style='text-align: center;'>"
                + "<a href='" + url + "' style='background: #28a745; color: #fff; padding: 10px 20px; text-decoration: none;'>Kích Hoạt Tài Khoản</a>"
                + "</p>"
                + "<p>Nếu bạn không tạo tài khoản, hãy bỏ qua email này.</p>"
                + "</div>"
                + "</body></html>";
    }
}
