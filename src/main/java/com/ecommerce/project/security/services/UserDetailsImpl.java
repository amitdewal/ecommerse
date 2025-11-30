package com.ecommerce.project.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String username;
	private String email;

	@JsonIgnore
	private String password;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public UserDetailsImpl(Long id, String username, String email, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}
	
	 public static UserDetailsImpl build(User user) {	 
		 
		 List<SimpleGrantedAuthority> authorities = user.getRoles()
				 .stream()
				 .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
				 .collect(Collectors.toList());
		 
		return new UserDetailsImpl(user.getUserId(),user.getUserName(),user.getEmail(),user.getPassword(),authorities ) 
				;
		 
	 }

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		UserDetailsImpl other = (UserDetailsImpl) obj;
//		return Objects.equals(authorities, other.authorities) && Objects.equals(email, other.email)
//				&& Objects.equals(id, other.id) && Objects.equals(password, other.password)
//				&& Objects.equals(username, other.username);
//	}

//	@Override
//	public int hashCode() {
//		return Objects.hash(authorities, email, id, password, username);
//	}

	 @Override
	    public boolean equals(Object o) {
	        if (this == o)
	            return true;
	        if (o == null || getClass() != o.getClass())
	            return false;
	        UserDetailsImpl user = (UserDetailsImpl) o;
	        return Objects.equals(id, user.id);
	    }

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return UserDetails.super.isEnabled();
	}
	 
	 
}
