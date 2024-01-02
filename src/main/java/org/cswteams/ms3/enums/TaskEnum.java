package org.cswteams.ms3.enums;

/**
 * Questa enum descrive le possibili mansioni di cui un servizio può essere composto
 */
public enum TaskEnum {

    CLINIC,
    /**
     * supervisiona e risponde a esigenze del momento. Molto importante che non rimanga scoperto.
     */
    EMERGENCY, // Dottore di guardia
    /**
     * gestisce ricoverati dal pronto soccorso
     */
    WARD,
    /**
     * gestisce, allestisce o partecipa in sala operatoria
     */
    OPERATING_ROOM
}
