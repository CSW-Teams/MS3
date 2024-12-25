package org.cswteams.ms3.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.cswteams.ms3.enums.Permission.*;

/**
 * This enumeration provides all the <i>roles</i> that an <code>User</code> can have.
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#attori">Glossary</a>.
 * @see org.cswteams.ms3.entity.User
 * @see org.cswteams.ms3.control.actors.ISystemActorController
 */
@RequiredArgsConstructor
public enum SystemActor {
    DOCTOR(
            Set.of(
                    DOCTOR_GET,
                    DOCTOR_PUT,
                    DOCTOR_POST,
                    DOCTOR_DELETE
            )
    ),

    PLANNER(
            Set.of(
                    PLANNER_GET,
                    PLANNER_PUT,
                    PLANNER_POST,
                    PLANNER_DELETE
            )
    ),

    CONFIGURATOR(
            Set.of(
                    CONFIGURATOR_GET,
                    CONFIGURATOR_PUT,
                    CONFIGURATOR_POST,
                    CONFIGURATOR_DELETE
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }
}
