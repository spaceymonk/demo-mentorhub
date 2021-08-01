package com.spaceymonk.mentorhub.config;

import com.spaceymonk.mentorhub.domain.Role;
import com.spaceymonk.mentorhub.domain.User;
import com.spaceymonk.mentorhub.repository.RoleRepository;
import com.spaceymonk.mentorhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
@AllArgsConstructor
public class MyUserDetailsContextMapper implements UserDetailsContextMapper {

    final private UserRepository userRepository;
    final private RoleRepository roleRepository;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            Role role_user = roleRepository.findByName("ROLE_USER");
            user = new User();
            user.setEnabled(true);
            user.setRoles(Set.of(role_user));
            user.setActualName(dirContextOperations.getStringAttribute("cn"));
            user.setUsername(username);
            user.setPassword(new String((byte[]) dirContextOperations.getObjectAttribute("userPassword")));
            userRepository.save(user);
        }

        return new MyUserDetails(user);
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        // not implemented, because no sign-up
    }
}
