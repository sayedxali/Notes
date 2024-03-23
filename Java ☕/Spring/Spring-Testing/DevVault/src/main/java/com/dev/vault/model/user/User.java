package com.dev.vault.model.user;

import com.dev.vault.model.task.Task;
import com.dev.vault.model.user.jwt.JwtToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "\"USER\"")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String email;
    private boolean active = false;
    private int age;
    private String education;
    private String major;

    /* relationships */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Task> task = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<JwtToken> jwtTokens;
    /* end of relationships */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Roles eachRole : roles) {
            authorities.add(new SimpleGrantedAuthority(eachRole.getRole().name()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
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
    // todo: implement the security to bypass even if the user is not active, but he/she won't have
    //  any access to any resources accept the login and search groups
    public boolean isEnabled() {
        return this.isActive();
    }
}
