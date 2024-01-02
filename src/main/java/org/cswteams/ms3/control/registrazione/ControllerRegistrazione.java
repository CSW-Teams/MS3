package org.cswteams.ms3.control.registrazione;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.registration.RegisteredUserDTO;
import org.cswteams.ms3.dto.registration.RegistrationDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.registration.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
public class ControllerRegistrazione implements IControllerRegistrazione {


    @Autowired
    private UserDAO userDAO;
    @Autowired
    private DoctorDAO doctorDAO;


    /*
     * L'ultimo carattere del codice fiscale viene calcolato così: i caratteri in posizione dispari tra i primi 15 hanno determinati pesi (il conteggio della
     * posizione parte da 1), mentre i caratteri in posizione pari hanno altri pesi. Si calcola la somma dei pesi per tutti e 15 i caratteri e se ne deriva
     * il modulo 26. Il valore ottenuto alla fine corrisponde all'ultima lettera del codice fiscale.
     */
    private static char calcolaCarattereControllo(String caratteriBase) {
        // Array dei pesi associati ai caratteri del codice fiscale
        int[] pesiDispari = {1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 2, 4, 18, 20, 11, 3, 6, 8, 12, 14, 16, 10, 22, 25, 24, 23};
        int[] pesiPari = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};

        int sommaPonderata = 0;

        // Calcola la somma ponderata dei caratteri moltiplicati per i pesi
        for (int i = 0; i < caratteriBase.length(); i++) {
            char c = caratteriBase.charAt(i);

            if ((i+1)%2 == 1) { //CASO DISPARI
                if (Character.isDigit(c)) {
                    sommaPonderata += pesiDispari[Character.getNumericValue(c)];
                } else {
                    sommaPonderata += pesiDispari[(int)(c)-55]; //il carattere 'A' corrisponde al codice ASCII 65, mentre è in posizione 10 negli array dei pesi (e così via).
                }

            } else {    //CASO PARI
                if (Character.isDigit(c)) {
                    sommaPonderata += pesiPari[Character.getNumericValue(c)];
                } else {
                    sommaPonderata += pesiPari[(int)(c)-55];
                }
            }

        }

        // Calcola il resto della divisione per 26 e ottiene il carattere corrispondente
        int resto = sommaPonderata % 26;
        System.out.println(resto);
        System.out.println((char) ('A' + resto));
        return (char) ('A' + resto);

    }


    /*
     * La validazione del codice fiscale avviene controllando la lunghezza della stringa e verificando se l'ultimo carattere
     * è conforme ai 15 caratteri precedenti (secondo l'apposito algoritmo).
     */
    private static boolean validaCodiceFiscale(String codiceFiscale) {
        // Verifica se la lunghezza del codice fiscale è corretta (assumiamo che chi ha il codice fiscale di lunghezza diversa non sia nelle condizioni di esercitare).
        if (codiceFiscale.length() != 16) {
            return false;
        }

        // Estrai i primi 15 caratteri del codice fiscale
        String caratteriBase = codiceFiscale.substring(0, 15);
        // Estrai l'ultimo carattere del codice fiscale
        char carattereControllo = codiceFiscale.charAt(15);
        // Calcola il carattere di controllo previsto
        char carattereControlloCalcolato = calcolaCarattereControllo(caratteriBase);
        // Confronta il carattere di controllo calcolato con quello effettivo
        return carattereControllo == carattereControlloCalcolato;

    }

    private boolean checkEmail(String email) {
        User user = userDAO.findByEmail(email);
        return user == null;
    }



    @Override
    public RegisteredUserDTO registraUtente(@NotNull RegistrationDTO registrationDTO) throws RegistrationException {

        if (Objects.equals(registrationDTO.getName(), ""))
            throw new RegistrationException("Non è stato specificato il nome");
        if (Objects.equals(registrationDTO.getLastname(), ""))
            throw new RegistrationException("Non è stato specificato il cognome");
        if (!validaCodiceFiscale(registrationDTO.getTaxCode()))
            throw new RegistrationException("Il codice fiscale non è valido");
        if (Objects.equals(registrationDTO.getPassword(), ""))
            throw new RegistrationException("La password non può essere vuota");
        if (!checkEmail(registrationDTO.getEmail()))
            throw new RegistrationException("Indirizzo email già registrato");
        if (registrationDTO.getSystemActors().contains(SystemActor.DOCTOR) &&
                (!registrationDTO.getSeniority().toString().equals("STRUCTURED") &&
                 !registrationDTO.getSeniority().toString().equals("SPECIALIZED")
                )
        ) {
            throw new RegistrationException("Non è stata specificata la seniority");
        }

        if (registrationDTO.getSystemActors().contains(SystemActor.DOCTOR)) {
            Doctor d = new Doctor(
                    registrationDTO.getName(),
                    registrationDTO.getLastname(),
                    registrationDTO.getTaxCode(),
                    registrationDTO.getBirthday(),
                    registrationDTO.getEmail(),
                    registrationDTO.getPassword(),
                    registrationDTO.getSeniority(),
                    registrationDTO.getSystemActors()
                    );
            doctorDAO.saveAndFlush(d);

            return new RegisteredUserDTO(
                    d.getId(),
                    d.getName(),
                    d.getLastname(),
                    d.getBirthday(),
                    d.getTaxCode(),
                    d.getEmail(),
                    d.getPassword(),
                    d.getSystemActors(),
                    d.getSeniority()
            );
        }

        User u = new User(registrationDTO.getName(),
                registrationDTO.getLastname(),
                registrationDTO.getTaxCode(),
                registrationDTO.getBirthday(),
                registrationDTO.getEmail(),
                registrationDTO.getPassword(),
                registrationDTO.getSystemActors()
        );

        userDAO.saveAndFlush(u);

        return new RegisteredUserDTO(
                u.getId(),
                u.getName(),
                u.getLastname(),
                u.getBirthday(),
                u.getTaxCode(),
                u.getEmail(),
                u.getPassword(),
                u.getSystemActors()
        );

    }

}
