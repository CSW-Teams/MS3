package org.cswteams.ms3.enums;

public enum TipoCategoriaEnum {
    /**
     * Rifelette lo stato dell' utente (es. incinta, in malattia, ...)
     */
    STATO,
    /**
     * Per gli strutturati: la specializzazione del medico (es. Cardiologia, ...)
     */
    SPECIALIZZAZIONE,
    /**
     * Per gli specializzandi: il luogo presso il quale deve svolgere il
     * proprio lavoro (es. Ambulatorio di Cardiologia, ...)
     */
    TURNAZIONE,

    /**
     * ATTENZIONE: nuove tipologie di categorie vanno inserite da qui in poi,
     * non modificando l'ordine delle precedenti, al fine di evitare
     * inconsistenze nella base di dati.
     */

}