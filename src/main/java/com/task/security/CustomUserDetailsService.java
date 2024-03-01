package com.task.security;

import com.task.model.User;
import com.task.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    @Cacheable(value = "loadUserByUsernameCache", key = "#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findUserByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not exists by Username or Email"));

        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }
}
