package org.cswteams.ms3.control.utils;

import java.util.HashMap;

@Deprecated
/**
 * Questa classe prevede metodi per convertire date in un formato più semplice da leggere per un utente
 */
public class ConvertitoreData {


    /**
     * Converte una data in formato dd-mm-yyyy in una stringa avente il seguente formato "dd nomeDelMese yyyy"
     * Ad esempio 22-01-2023 verrà convertito in 22 Gennaio 2023.
     * @param data
     * @return
     */
    public static String daStandardVersoTestuale(String data){

        HashMap<String,String> mesi = new HashMap<String, String>() {{
            put("01", "Gennaio");
            put("02", "Febbraio");
            put("03", "Marzo");
            put("04", "Aprile");
            put("05", "Maggio");
            put("06", "Giugno");
            put("07", "Luglio");
            put("08", "Agosto");
            put("09", "Settembre");
            put("10", "Ottobre");
            put("11", "Novembre");
            put("12", "Dicembre");

        }};

        //Isolo il giorno, il mese e l'anno dalla data
        String[] dataInfo = data.split("-");

        //Verifico se il formato della data è quello atteso. Se ad esempio è passata una data come formato dd/mm/yyyy
        //verrà restituita la data senza apportare nessuna conversione.
        if(dataInfo.length >2)
            return dataInfo[2]+ " " + mesi.get(dataInfo[1])+ " "+ dataInfo[0];

        return data;
    }
}
