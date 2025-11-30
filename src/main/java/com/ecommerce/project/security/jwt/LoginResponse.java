package com.ecommerce.project.security.jwt;

import java.util.List;

public class LoginResponse {

	private String jwtToken;

	private String username;
	private List<String> roles;

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public LoginResponse(String jwtToken, String username, List<String> roles) {
		super();
		this.jwtToken = jwtToken;
		this.username = username;
		this.roles = roles;
	}

}
