package com.rianlucas.carona_api.domain.user;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rianlucas.carona_api.domain.google.AuthProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String username;
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider = AuthProvider.LOCAL;
    
    private String phone;
    private Date birthDate;
    private String gender;
    private String photoUrl;
    private boolean isDriver = false;
    private String cpf;
    private String state;
    private String city;
    private Boolean emailVerified = false;
    private Boolean phoneVerified = false;
    private LocalDateTime usernameLastEditedAt;
    
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role ==UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    // Getter customizado para o campo username (não confundir com getUsername() do UserDetails)
    public String getUsernameField() {
        return username;
    }

    public void setUsernameField(String username) {
        this.username = username;
    }

    // UserDetails retorna email como username para autenticação
    @Override
    public String getUsername() {
        return email;
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
        return true; // Verificação de email é feita manualmente no UserService
    }

    
}
