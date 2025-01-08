package org.cswteams.ms3.control.registrazione;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.SystemUserDAO;
import org.cswteams.ms3.dto.registration.RegisteredUserDTO;
import org.cswteams.ms3.dto.registration.RegistrationDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.SystemUser;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.registration.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Service
public class ControllerRegistrazione implements IControllerRegistrazione {


    @Autowired
    private SystemUserDAO userDAO;
    @Autowired
    private DoctorDAO doctorDAO;


    /*
     * The last character of the tax code is calculated like this: the characters in odd positions among the first 15 have certain weights (the count of
     * position starts from 1), while characters in even positions have other weights. The sum of the weights for all 15 characters is calculated and derived
     * form 26. The value obtained at the end corresponds to the last letter of the tax code.
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
        SystemUser user = userDAO.findByEmail(email);
        return user == null;
    }



    @Override
    public RegisteredUserDTO registerUser(@NotNull RegistrationDTO registrationDTO) throws RegistrationException {

        if (Objects.equals(registrationDTO.getName(), ""))
            throw new RegistrationException("Name not specified.");
        if (Objects.equals(registrationDTO.getLastname(), ""))
            throw new RegistrationException("Last name not specified.");
        if (!validaCodiceFiscale(registrationDTO.getTaxCode()))
            throw new RegistrationException("Invalid tax code.");
        if (Objects.equals(registrationDTO.getPassword(), ""))
            throw new RegistrationException("Password cannot be empty.");
        if (!checkEmail(registrationDTO.getEmail()))
            throw new RegistrationException("Email address already registered.");
        if (registrationDTO.getSystemActors().contains(SystemActor.DOCTOR) &&
                (!registrationDTO.getSeniority().toString().equals("STRUCTURED") &&
                        !registrationDTO.getSeniority().toString().equals("SPECIALIST_JUNIOR") &&
                        !registrationDTO.getSeniority().toString().equals("SPECIALIST_SENIOR")
                )
        ) {
            throw new RegistrationException("Seniority not specified.");
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

        SystemUser u = new SystemUser(registrationDTO.getName(),
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