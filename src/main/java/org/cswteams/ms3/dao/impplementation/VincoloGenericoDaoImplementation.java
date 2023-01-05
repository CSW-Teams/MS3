package org.cswteams.ms3.dao.impplementation;

import java.util.ArrayList;
import java.util.List;

import org.cswteams.ms3.control.vincoli.Vincolo;
import org.cswteams.ms3.control.vincoli.VincoloPersonaTurno;
import org.cswteams.ms3.dao.VincoloGenericoDao;

/*
 * Questa classe permette di raggruppare tutte le tipologie di vincolo esistenti. L'idea è che usi i dao
 * delle implementazioni delle altre tipologie di vincolo ( Esempio VincoloTurnoPersona o VincoloTurnoAssegnazioneTurno) e 
 * raccolga tutti i vincoli in un unica lista. Questo approccio è necessario perchè non è possibile andare a pescare dal
 * database delle interfacce (Esempio interfaccia Vincolo).
 */
public class VincoloGenericoDaoImplementation implements VincoloGenericoDao{

    @Override
    public List<Vincolo> findAll() {
        List<Vincolo> vincoli = new ArrayList<>();
        vincoli.add(new VincoloPersonaTurno());

        //Quando saranno implementati i vincoli TuroAssegnazioneTurno : vincoli.add(vincoliTurnoAssegnazioneDao.findALL())
        //Quando saranno implementati altri vincoli : vincoli.add(altroVincoloDao.findALL())

        return vincoli;
    }
    
}
