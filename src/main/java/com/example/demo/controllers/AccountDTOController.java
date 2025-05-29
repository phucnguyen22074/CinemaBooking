package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UsersDTO;
import com.example.demo.entities.Users;
import com.example.demo.services.JWTService;
import com.example.demo.services.MailService;
import com.example.demo.servicesDTO.AccountDTOService;

@RestController
@RequestMapping({ "api/accountDTO" })
public class AccountDTOController {

	@Autowired
	private AccountDTOService accountDTOService;

	@Autowired
	private MailService mailService;

	@Autowired
	private Environment environment;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	static class JwtResponse {
		private String jwt;

		public JwtResponse(String jwt) {
			this.jwt = jwt;
		}

		public String getJwt() {
			return jwt;
		}
	}

	/* GET */
	@GetMapping(value = "find/{id}", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsersDTO> find(@PathVariable("id") int id) {
		try {
			return new ResponseEntity<UsersDTO>(accountDTOService.find(id), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<UsersDTO>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "findByEmail", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<UsersDTO> findByEmail(@RequestParam("email") String email, @RequestHeader("Authorization") String token) {
	    try {
	        if (!jwtService.validToken(token.replace("Bearer ", ""))) {
	            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	        }

	        return new ResponseEntity<>(accountDTOService.findByEmail(email), HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}


	/* POST */
	@PostMapping(value = "create", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> create(@RequestBody UsersDTO accountDTO) {
		try {
			Users account = accountDTOService.save(accountDTO);
			if (account == null) {
				throw new Exception();
			} else {
				String securityCode = account.getSecurityCode();
				String from = environment.getProperty("spring.mail.username");
				String to = accountDTO.getEmail();
				String body = "Security Code: <b>" + securityCode + "</b>";
				if (mailService.send(from, to, "Verify Account", body)) {
					return new ResponseEntity<Object>(new Object() {
						public boolean result = true;
					}, HttpStatus.OK);
				} else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(value = "active", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> active(@RequestBody UsersDTO accountDTO) {
		try {
			Users account = accountDTOService.findByEmailAccount(accountDTO.getEmail());
			account.setStatus(true);
			return new ResponseEntity<Object>(new Object() {
				public boolean result = accountDTOService.update(account);
			}, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "login", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> login(@RequestBody UsersDTO accountDTO) {
		Map<String, Object> response = new HashMap<>();
		try {
			if (accountDTO.getEmail() == null || accountDTO.getPassword() == null) {
				response.put("error", "Email and password cannot be null.");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			boolean result = accountDTOService.login(accountDTO.getEmail(), accountDTO.getPassword());
			response.put("result", result);

			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", "Internal Server Error: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "loginJWT", produces = "application/json")
	public ResponseEntity<Object> login(@RequestBody Users account) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword()));
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
	        String token = jwtService.generateToken(account.getEmail());

	        response.put("success", true);
	        response.put("token", token);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "Invalid username or password");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	    }
	}


	/* PUT */
	@PutMapping(value = "update", produces = MimeTypeUtils.APPLICATION_JSON_VALUE, consumes = MimeTypeUtils.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> update(@RequestBody UsersDTO accountDTO, @RequestHeader("Authorization") String token) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        if (!jwtService.validToken(token.replace("Bearer ", ""))) {
	            response.put("error", "Unauthorized");
	            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	        }

	        if (accountDTO.getEmail() == null || accountDTO.getFullName() == null) {
	            response.put("error", "Email and full name cannot be null.");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }

	        boolean result = accountDTOService.update(accountDTO);
	        if (result) {
	            response.put("result", true);
	            response.put("message", "Update successful!");
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } else {
	            response.put("result", false);
	            response.put("message", "Update failed!");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("error", "Internal Server Error: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	/* Delete */

}
