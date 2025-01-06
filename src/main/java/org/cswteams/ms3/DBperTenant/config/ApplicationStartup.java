package org.cswteams.ms3.DBperTenant.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component()
@Profile("!test")
public class ApplicationStartup {

}
