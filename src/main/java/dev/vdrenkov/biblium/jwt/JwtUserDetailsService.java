package dev.vdrenkov.biblium.jwt;

import dev.vdrenkov.biblium.entity.User;
import dev.vdrenkov.biblium.entity.UserRole;
import dev.vdrenkov.biblium.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public JwtUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(final @NonNull String username) throws UsernameNotFoundException {
        final User user = userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("No such user found in the database"));

        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (final UserRole role : user.getUserRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
            authorities);
    }
}
