package org.cswteams.ms3.control.utils;

import java.time.DayOfWeek;

/**
 * Classe di utilità che modella giorni della settimana come una bitmask i cui
 * 7 bit meno significativi rappresentano i giorni della settimana inclusi.
 */
public class GiorniDellaSettimanaBitMask {
    /*
    public static final byte Lunedi = 1;
    public static final byte Martedi = 2;
    public static final byte Mercoledi = 4;
    public static final byte Giovedi = 8;
    public static final byte Venerdi = 16;
    public static final byte Sabato = 32;
    public static final byte Domenica = 64;
    */

    /** The actual mask value associated with this bitmask */
    public byte giorni;
    
    /** 
     * Ritorna la bitmask associata al giorno della settimana indicato.
     */
    public static byte getBitMaskGiorno(DayOfWeek giorno){
        
        return (byte) (2<<giorno.getValue()-1);
    }

     /** Controlla se il giorno specificato è compreso tra quelli indicati
      * in questa maschera
      */
    public boolean isDayOfWeekIncluded(DayOfWeek day){
        return (giorni & getBitMaskGiorno(day)) != 0;
    }

    /** aggiunge un giorno della settimana a questa maschera */
    public void addDayOfWeek(DayOfWeek day){
        giorni |= getBitMaskGiorno(day);
    }

    /** rimuove un giorno della settimana da questa maschera */
    public void removeDayOfWeek(DayOfWeek day){
        giorni &= ~getBitMaskGiorno(day);
    }
}