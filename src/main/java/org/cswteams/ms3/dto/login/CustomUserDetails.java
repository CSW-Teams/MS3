package org.cswteams.ms3.dto.login;

import lombok.Getter;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO used in the Login use case (from Service to REST Controller)
 */
@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String name;
    private final String lastname;
    private final String email;
    private final String password;
    private final Set<SystemActor> systemActors;


    public CustomUserDetails(Long id, String name, String lastname, String email, String password, Set<SystemActor> systemActors) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.systemActors = systemActors;
    }

    /**
     * Returns the collection of authorities granted to the user.
     * Each role from the user's {@link SystemActor} set is mapped to a
     * {@link SimpleGrantedAuthority}.
     *
     * <p>
     * If the system actor set is not null or empty, a collection of authorities
     * is returned, each representing a role in the system.
     * If the set is null or empty, an empty collection is returned, meaning the
     * user has no roles granted in Spring Security.
     *
     * @return A collection of {@link GrantedAuthority} objects representing the user's roles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (systemActors != null && !systemActors.isEmpty()) {
            return systemActors.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name().toUpperCase()))
                    .collect(Collectors.toSet());
        }

        // Return an empty collection instead of null for better compatibility
        return Collections.emptySet();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account has expired.
     * This implementation always returns {@code true}, meaning the account is not expired.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the account is not expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // TODO: Customise according to the logic of your system
    }

    /**
     * Indicates whether the user's account is locked.
     * This implementation always returns {@code true}, meaning the account is not locked.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the account is not locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // TODO: Customise according to the logic of your system
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * This implementation always returns {@code true}, meaning the credentials are not expired.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the credentials are not expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // TODO: Customise according to the logic of your system
    }

    /**
     * Indicates whether the user is enabled.
     * This implementation always returns {@code true}, meaning the user is enabled.
     * This method can be customized based on the business logic of your system.
     *
     * @return {@code true} if the user is enabled.
     */
    @Override
    public boolean isEnabled() {
        return true; // TODO: Customise according to the logic of your system
    }
}
