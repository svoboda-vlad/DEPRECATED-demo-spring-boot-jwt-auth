package com.example.demo.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

	private static final String ADMIN_USERS_URL = "/admin/users";
	private final UserRepository userRepository;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(ADMIN_USERS_URL)
	public ResponseEntity<List<UserInfo>> getAllUsers() {
		List<User> users = userRepository.findAll();
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();
		users.forEach(user -> {
			userInfoList.add(user.toUserInfo());
		});
		return ResponseEntity.ok(userInfoList);
	}

}
