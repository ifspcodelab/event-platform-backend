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
    private final Collection<String> organizer;
    private final Collection<String> organizerSubevent;

    public JwtUserDetails(UUID id, String username, List<GrantedAuthority> grantedAuthorities, List<String> organizer, List<String> organizerSubevent) {
        this.id = id;
        this.username = username;
        this.authorities = grantedAuthorities;
        this.organizer = organizer;
        this.organizerSubevent = organizerSubevent;
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

    public Collection<String> getOrganizer() {
        return organizer;
    }

    public Collection<String> getOrganizerSubevent() {
        return organizerSubevent;
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