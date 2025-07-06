package com.paymybuddy.config;

import com.paymybuddy.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Ici, on retourne une liste vide car pas des roles
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Pas de gestion d'expiration
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Pas de gestion de verrouillage
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Pas de gestion d'expiration des credentials
    }

    @Override
    public boolean isEnabled() {
       // return true; // Tous les comptes sont activés
        return user.isEnabled();
    }


    public User getUser() {
        return user;
    }
}
