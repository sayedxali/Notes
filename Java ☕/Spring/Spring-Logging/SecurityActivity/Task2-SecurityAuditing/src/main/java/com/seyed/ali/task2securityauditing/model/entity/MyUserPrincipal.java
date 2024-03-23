package com.seyed.ali.task2securityauditing.model.entity;

import com.seyed.ali.task2securityauditing.model.entity.HogwartsUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class represents an Adapter pattern!
 */
@Getter
@AllArgsConstructor
public class MyUserPrincipal implements UserDetails {

    private HogwartsUser hogwartsUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert a user's roles from space-delimited string to a list of SimpleGrantedAuthority objects.
        // E.g., John's roles are stored in a string like "admin user moderator", we need to convert it to a list of GrantedAuthority.
        // Before conversion, we need to add this "ROLE_" prefix to each role name.
        return Arrays
                // this method, makes the string into an array separated by " "
                // E.g., John's roles are `admin user moderator` |-> ["admin", "user", "moderator"]
                .stream(StringUtils.tokenizeToStringArray(
                        this.hogwartsUser.getRoles(),
                        " "
                )).map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Override
    public String getPassword() {
        return this.hogwartsUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.hogwartsUser.getUsername();
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
        return this.hogwartsUser.isEnabled();
    }

}
