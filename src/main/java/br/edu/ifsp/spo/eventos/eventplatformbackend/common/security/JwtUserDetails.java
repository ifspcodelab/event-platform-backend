package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class JwtUserDetails implements UserDetails {
    private final UUID id;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(UUID id, String username, List<GrantedAuthority> grantedAuthorities) {
        this.id = id;
        this.username = username;
        this.authorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "JwtUserDetails{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", authorities=" + authorities +
            '}';
    }
}