package br.com.fiap.dailyreminder.services;

import br.com.fiap.dailyreminder.modules.users.domain.User;
import br.com.fiap.dailyreminder.modules.users.infrastructure.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class AuthenticationService {
    
    @Autowired
    UserRepository userRepository;

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("usuario nao encontrado"));
    }
}
