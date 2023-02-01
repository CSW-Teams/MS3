package org.cswteams.ms3.control.categorieUtente;

import org.cswteams.ms3.control.utils.MappaCategoriaUtente;
import org.cswteams.ms3.dao.CategoriaUtenteDao;
import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.CategoriaUtenteDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class ControllerCategorieUtente implements IControllerCategorieUtente {

    @Autowired
    CategoriaUtenteDao categoriaUtenteDao;

    @Autowired
    private CategorieDao categorieDao;

    @Autowired
    private UtenteDao utenteDao;

    @Override
    public Set<CategoriaUtenteDTO> leggiCategorieUtente(Long id) {
        Set<CategoriaUtenteDTO> categorieUtenteDTO = MappaCategoriaUtente.categoriaUtenteToDTO(categoriaUtenteDao.findStatoUtente(id));
        return categorieUtenteDTO;
    }

    @Override
    public Set<CategoriaUtenteDTO> leggiSpecializzazioniUtente(Long id) {
        Set<CategoriaUtenteDTO> specializzazioneUtenteDTO = MappaCategoriaUtente.categoriaUtenteToDTO(categoriaUtenteDao.findSpecializzazioniUtente(id));
        return specializzazioneUtenteDTO;
    }

    @Override
    public Set<CategoriaUtenteDTO> leggiTurnazioniUtente(Long id) throws ParseException {
        Set<CategoriaUtenteDTO> turnazioniUtenteDTO = MappaCategoriaUtente.categoriaUtenteToDTO(categoriaUtenteDao.findTurnazioniUtente(id));
        return turnazioniUtenteDTO;
    }

    @Override
    public CategoriaUtente aggiungiTurnazioneUtente(CategoriaUtenteDTO c, Long utenteID) throws Exception {

        if(categorieDao.findAllByNome(c.getCategoria().getNome()) == null)
            throw new Exception("Non esiste una categoria con questo nome :"+c.getCategoria().getNome());
        Optional<Utente> u = utenteDao.findById(utenteID);
        if (u == null)
            throw new Exception("Nessun utente con ID esistente: " + utenteID);

        CategoriaUtente categoriaUtente = MappaCategoriaUtente.categoriaUtenteDTOToEntity(c);
        categoriaUtente.setCategoria(categorieDao.findAllByNome(c.getCategoria().getNome()));
        categoriaUtenteDao.save(categoriaUtente);
        u.get().getTurnazioni().add(categoriaUtente);
        utenteDao.saveAndFlush(u.get());

        return  categoriaUtente;
    }

    @Override
    public void cancellaRotazione(Long idCategoria, Long idUtente) throws DatabaseException {
        Optional<Utente> utente = utenteDao.findById(idUtente);
        if(utente.isPresent()){
            List<CategoriaUtente> rotazioni = utente.get().getTurnazioni();
            for(int i = 0; i < rotazioni.size(); i++){
                if(rotazioni.get(i).getId().equals(idCategoria)){
                    utente.get().getTurnazioni().remove(i);
                }
            }
            utenteDao.save(utente.get());
        }else{
            throw new DatabaseException("Utente non trovato");
        }


    }

}
