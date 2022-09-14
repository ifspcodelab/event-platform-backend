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
    private final Collection<String> coordinatorEvent;
    private final Collection<String> coordinatorSubevent;
    private final Collection<String> collaboratorEvent;
    private final Collection<String> collaboratorSubevent;

    public JwtUserDetails(UUID id, String username, List<GrantedAuthority> grantedAuthorities, List<String> coordinatorEvent, List<String> coordinatorSubevent, List<String> collaboratorEvent, List<String> collaboratorSubevent) {
        this.id = id;
        this.username = username;
        this.authorities = grantedAuthorities;
        this.coordinatorEvent = coordinatorEvent;
        this.coordinatorSubevent = coordinatorSubevent;
        this.collaboratorEvent = collaboratorEvent;
        this.collaboratorSubevent = collaboratorSubevent;
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

    public Collection<String> getCoordinatorEvent() {
        return coordinatorEvent;
    }

    public Collection<String> getCoordinatorSubevent() {
        return coordinatorSubevent;
    }

    public Collection<String> getCollaboratorEvent() {
        return collaboratorEvent;
    }

    public Collection<String> getCollaboratorSubevent() {
        return collaboratorSubevent;
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