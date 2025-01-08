package org.cswteams.ms3.multitenancyapp.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.multitenancyapp.entity.*;
import org.cswteams.ms3.multitenancyapp.dao.*;
import org.cswteams.ms3.multitenancyapp.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Optional;

@Component()
@Profile("!test")
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */

    @Autowired
    private SystemUserDAO systemUserDAO;

    @Autowired
    private TenantUserDAO tenantUserDAO;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        /**
         * FIXME: sostiutire count con controllo su entità Config
         */
         // if (userDAO.count() == 0) {

            populateDB();

            // Ripristina lo schema di default ("public" per PostgreSQL)
            // TenantContext.setCurrentTenant("public");
         // }

    }

    private void addUserToTenantsSelective(SystemUser user) {
        String originalTenant = TenantContext.getCurrentTenant(); // Salva il contesto corrente

        try {
            TenantContext.setCurrentTenant(user.getTenant().toLowerCase()); // Cambia il contesto al tenant specifico

            // Verifica se l'utente esiste già
            Optional<TenantUser> existingUser = tenantUserDAO.findByTaxCode(user.getTaxCode());
            if (existingUser.isPresent()) {
                System.out.println("Utente con codice fiscale " + user.getTaxCode() + " già presente nello schema " + user.getTenant());
                return;
            }

            // Inserisci il nuovo utente
            TenantUser tenantSpecificUser = new TenantUser(
                    user.getName(),
                    user.getLastname(),
                    user.getTaxCode(),
                    user.getBirthday(),
                    user.getEmail(),
                    user.getPassword()
            );
            tenantUserDAO.saveAndFlush(tenantSpecificUser); // Salva nel database del tenant
            System.out.println("Utente " + user.getTaxCode() + " aggiunto nello schema " + user.getTenant());
        } catch (Exception e) {
            System.err.println("Errore nell'inserimento utente nello schema " + user.getTenant() + ": " + e.getMessage());
        } finally {
            TenantContext.setCurrentTenant(originalTenant); // Ripristina il contesto originale
        }
    }



    private void populateDB() {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        SystemUser u3 = new SystemUser("Federica", "Villani", "VLLFRC98P43H926Y", LocalDate.of(1998, 9, 3), "federicavillani_tenant_a@gmail.com", encoder.encode("passw"), "A");
        SystemUser u4 = new SystemUser("Daniele", "Colavecchi", "CLVDNL82C21H501E", LocalDate.of(1982, 7, 6), "danielecolavecchi_tenant_b@gmail.com", encoder.encode("passw"), "B");
        SystemUser u5 = new SystemUser("Daniele", "La Prova", "LPRDNL98H13H501F", LocalDate.of(1998, 2, 12), "danielelaprova_tenant_a@gmail.com", encoder.encode("passw"), "A");
        SystemUser u7 = new SystemUser("Luca", "Fiscariello", "FSCLCU99D15A783Z", LocalDate.of(1998, 8, 12), "lucafiscariello_tenant_b@gmail.com", encoder.encode("passw"), "B");
        SystemUser u8_1 = new SystemUser("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini_tenant_a@gmail.com", encoder.encode("passw"), "A");
        SystemUser u8_2 = new SystemUser("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini_tenant_b@gmail.com", encoder.encode("passw2"), "B");
        SystemUser u10_1 = new SystemUser("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi_tenant_a@gmail.com", encoder.encode("passw"), "A");
        SystemUser u10_2 = new SystemUser("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi_tenant_b@gmail.com", encoder.encode("passw2"), "B");
        SystemUser u9 = new SystemUser("Giulia", "Cantone II", "CTNGLI78E44H501Z", LocalDate.of(1991, 2, 12), "giuliacantone_tenant_a@gmail.com", encoder.encode("passw"), "A");
        SystemUser u1_1 = new SystemUser("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97_tenant_a@gmail.com", encoder.encode("passw"), "A");
        SystemUser u1_2 = new SystemUser("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97_tenant_b@gmail.com", encoder.encode("passw2"), "B");
        SystemUser u2 = new SystemUser("Domenico", "Verde", "VRDDMC96H16H501H", LocalDate.of(1997, 5, 23), "domenicoverde_tenant_b@gmail.com", encoder.encode("passw"), "B");
        SystemUser u6_1 = new SystemUser("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone_tenant_a@gmail.com", encoder.encode("passw"), "A");
        SystemUser u6_2 = new SystemUser("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone_tenant_b@gmail.com", encoder.encode("passw2"), "B");
        SystemUser u44_1 = new SystemUser("Giulio","Farnasini","GLIFNS94M07G224O",LocalDate.of(1994,8,7),"giuliofarnasini_tenant_a@gmail.com",encoder.encode("passw"), "A");
        SystemUser u44_2 = new SystemUser("Giulio","Farnasini","GLIFNS94M07G224O",LocalDate.of(1994,8,7),"giuliofarnasini_tenant_b@gmail.com",encoder.encode("passw2"), "B");
        SystemUser u45_1 = new SystemUser("Full","Permessi","FLLPRM98M24G224O",LocalDate.of(1998,8,24),"fullpermessi_tenant_a@gmail.com",encoder.encode("passw"), "A");
        SystemUser u45_2 = new SystemUser("Full","Permessi","FLLPRM98M24G224O",LocalDate.of(1998,8,24),"fullpermessi_tenant_b@gmail.com",encoder.encode("passw2"), "B");


        // Inserire utenti negli schemi tenant
        addUserToTenantsSelective(u1_1);
        addUserToTenantsSelective(u1_2);
        addUserToTenantsSelective(u2);
        addUserToTenantsSelective(u3);
        addUserToTenantsSelective(u4);
        addUserToTenantsSelective(u5);
        addUserToTenantsSelective(u6_1);
        addUserToTenantsSelective(u6_2);
        addUserToTenantsSelective(u7);
        addUserToTenantsSelective(u8_1);
        addUserToTenantsSelective(u8_2);
        addUserToTenantsSelective(u9);
        addUserToTenantsSelective(u10_1);
        addUserToTenantsSelective(u10_2);
        addUserToTenantsSelective(u44_1);
        addUserToTenantsSelective(u44_2);
        addUserToTenantsSelective(u45_1);
        addUserToTenantsSelective(u45_2);

    }

}
