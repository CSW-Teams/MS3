package org.cswteams.ms3.dto.login;

import lombok.Getter;
import org.cswteams.ms3.enums.SystemActor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

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
    private final SystemActor systemActor;


    public CustomUserDetails(Long id, String name, String lastname, String email, String password, SystemActor systemActor) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.systemActor = systemActor;
    }

    /**
     * Returns the collection of authorities granted to the user.
     * Each role from the user's {@link SystemActor} set is mapped to a
     * {@link SimpleGrantedAuthority}.
     *
     * <p>
     * If the system actor is not null, a single authority is returned,
     * representing the user's role in the system.
     * If no system actor is assigned, null is returned, meaning the user has
     * no role granted in Spring Security.
     *
     * @return A collection of {@link GrantedAuthority} objects representing the user's role.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (systemActor != null) {
            return Collections.singleton(new SimpleGrantedAuthority(this.systemActor.toString()));
        }

        return null;
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
