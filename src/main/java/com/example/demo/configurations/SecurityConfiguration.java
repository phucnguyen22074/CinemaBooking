package com.example.demo.configurations;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.demo.services.AccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private AccountService accountService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.cors(cor -> cor.disable()).csrf(cs -> cs.disable()).authorizeHttpRequests(auth -> {
			auth.requestMatchers(
					"/", 
					"/home/**", 
					"/images/**",
					"/assets/**",
					"/schedule/**",
					"/account/verify/**",
					"/account/login", 
					"/account/register",
					"/account/save",
					"/movie/**",
					"/account/access-denied",
					"/admin/login",
					"/api/**"
					)
					.permitAll()
					.requestMatchers(
							"/admin/**"
							).hasAnyRole("ADMIN")
					.requestMatchers(
							"/account/welcome",
							"/account/profile",
							"/account/history",
							"/account/findBydates/**",
							"/account/invoiceDetails/**",
							"https://your-server.com/**"
							).hasAnyRole("CUSTOMER_SUPER","ADMIN","CUSTOMER");
		}).formLogin(form -> {
			form.loginPage("/account/login")
				.loginProcessingUrl("/account/process-login")
				.usernameParameter("email")
					.passwordParameter("password").successHandler(new AuthenticationSuccessHandler() {
						@Override
						public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
								Authentication authentication) throws IOException, ServletException {
							Map<String, String> urls = new HashMap<String, String>();
							urls.put("ROLE_ADMIN", "/admin/dashboard/index");
							urls.put("ROLE_CUSTOMER", "/home/index");
							urls.put("ROLE_CUSTOMER_SUPER", "/home/index");
							List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication
									.getAuthorities();
							String role = authorities.get(0).getAuthority();
							String url = urls.get(role);
							response.sendRedirect(url);
						}
					})
					// .defaultSuccessUrl("/account/welcome")
					.failureUrl("/account/login?error");
		}).logout(logout -> {
			logout.logoutUrl("/account/logout").logoutSuccessUrl("/home/index");
		}).exceptionHandling(ex -> {
			ex.accessDeniedPage("/account/access-denied");
		}).build();
	}

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
//		builder.userDetailsService(accountService);
//	}
	
	@Autowired
	public void globalConfig(AuthenticationManagerBuilder builder) throws Exception {
		builder.userDetailsService(accountService);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
		return authenticationConfiguration.getAuthenticationManager();
	}

}