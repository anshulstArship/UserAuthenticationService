package com.main.userauthenticationservice.security;

import com.main.userauthenticationservice.models.Role;
import org.springframework.security.core.GrantedAuthority;

public class CustomSpringGrantedAuthorities implements GrantedAuthority {
    private Role role;
    public CustomSpringGrantedAuthorities(Role role){
        this.role=role;
    }
    @Override
    public String getAuthority() {
        return role.getRole();
    }
}
