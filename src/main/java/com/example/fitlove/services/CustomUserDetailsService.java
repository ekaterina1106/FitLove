package com.example.fitlove.services;

import com.example.fitlove.models.Clients;
import com.example.fitlove.repositories.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientsRepository clientsRepository;

    public CustomUserDetailsService(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Loading user with email: " + email);  // Для дебага

        Clients client = clientsRepository.findByEmail(email);

        if (client == null) {
            System.out.println("Client not found with email: " + email); // Для дебага
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        if (client.isBlocked()) {
            System.out.println("Client is blocked: " + email);
            throw new LockedException("Аккаунт заблокирован");
        }

        // Получаем роли клиента
        Collection<? extends GrantedAuthority> authorities = getAuthorities(client);

        return new org.springframework.security.core.userdetails.User(
                client.getEmail(),
                client.getPassword(),
                !client.isBlocked(),
                true, true, true,
                authorities
        );
    }

    // Метод для получения ролей пользователя
    private Collection<? extends GrantedAuthority> getAuthorities(Clients client) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Добавляем роль пользователя
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // Добавляем роль администратора, если клиент является администратором
        if (client.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return authorities;
    }
}

