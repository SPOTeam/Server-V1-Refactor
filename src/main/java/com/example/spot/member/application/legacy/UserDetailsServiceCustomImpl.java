package com.example.spot.member.application.legacy;

import com.example.spot.auth.domain.TempUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Deprecated
public class UserDetailsServiceCustomImpl implements UserDetailsServiceCustom {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        return TempUserDetails.builder()
                .email(username)
                .authorities(authorities)
                .build();
    }
}
