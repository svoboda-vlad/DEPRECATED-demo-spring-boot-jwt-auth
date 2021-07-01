package com.example.demo.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// IdClass for UserRoles due to composite primary key
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRolesId implements Serializable {
	
	private static final long serialVersionUID = 1L;	
	
	private long user;
    private long role;

}