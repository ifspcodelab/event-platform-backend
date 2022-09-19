package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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

    public boolean isAdmin() {
        return authorities.stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean hasPermissionForEvent(UUID eventId) {
        var organizerEvents = Stream.concat(getCoordinatorEvent().stream(), getCollaboratorEvent().stream());
        return organizerEvents.anyMatch(e -> e.equals(eventId.toString()));
    }

    public boolean hasPermissionForSubEvent(UUID subEventId) {
        var organizerSubEvents = Stream.concat(getCoordinatorSubevent().stream(), getCollaboratorSubevent().stream());
        return organizerSubEvents.anyMatch(e -> e.equals(subEventId.toString()));
    }

    public boolean isOrganizer() {
        var permissions = new ArrayList<String>();
        permissions.addAll(getCollaboratorEvent());
        permissions.addAll(getCoordinatorEvent());
        permissions.addAll(getCollaboratorSubevent());
        permissions.addAll(getCoordinatorSubevent());
        return permissions.size() > 0;
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
            ", coordinatorEvent=" + coordinatorEvent +
            ", coordinatorSubevent=" + coordinatorSubevent +
            ", collaboratorEvent=" + collaboratorEvent +
            ", collaboratorSubevent=" + collaboratorSubevent +
            '}';
    }
}