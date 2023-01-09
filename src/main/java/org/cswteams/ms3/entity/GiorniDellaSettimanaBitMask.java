package org.cswteams.ms3.entity;

import java.time.DayOfWeek;

import javax.persistence.Embeddable;

import lombok.Data;

/**
 * Classe di utilità che modella giorni della settimana come una bitmask i cui
 * 7 bit meno significativi rappresentano i giorni della settimana inclusi.
 */
@Data
@Embeddable
public class GiorniDellaSettimanaBitMask {

    /** The actual mask value associated with this bitmask */
    private byte giorni;
        
    /** 
     * Ritorna la bitmask associata al giorno della settimana indicato.
     */
    private byte getBitMaskGiorno(DayOfWeek giorno){
        
        return (byte) (2<<giorno.getValue()-1);
    }

     /** Controlla se il giorno specificato è compreso tra quelli indicati
      * in questa maschera
      */
    public boolean isDayOfWeekIncluded(DayOfWeek day){
        return (this.giorni & getBitMaskGiorno(day)) != 0;
    }

    /** aggiunge un giorno della settimana a questa maschera */
    public GiorniDellaSettimanaBitMask addDayOfWeek(DayOfWeek day){
        this.giorni |= getBitMaskGiorno(day);
        return this;
    }

    /** rimuove un giorno della settimana da questa maschera */
    public GiorniDellaSettimanaBitMask removeDayOfWeek(DayOfWeek day){
        this.giorni &= ~getBitMaskGiorno(day);
        return this;
    }

    /**Abilita tutti i giorni */
    public GiorniDellaSettimanaBitMask enableAllDays(){
        this.giorni = (byte) 255;
        return this;
    }

    /**Disabilita tutti i giorni */
    public GiorniDellaSettimanaBitMask disableAllDays(){
        this.giorni = 0;
        return this;
    }
}