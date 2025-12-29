

package com.rianlucas.carona_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.rianlucas.carona_api.domain.user.User;


public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByEmail(String email);
    UserDetails findByUsername(String username);
    UserDetails findByPhone(String phone);
    UserDetails findByCpf(String cpf);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    boolean existsByCpf(String cpf);
    
}
