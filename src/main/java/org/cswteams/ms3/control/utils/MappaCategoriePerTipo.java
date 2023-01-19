package org.cswteams.ms3.control.utils;

import lombok.Data;
import org.cswteams.ms3.dto.CategoriaDTO;
import org.cswteams.ms3.dto.CategoriaUtenteDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;

import java.util.HashSet;
import java.util.Set;

@Data
public class MappaCategoriePerTipo {

    public static Categoria CategoriaDTOtoEntity(CategoriaDTO categoriaDTO) {
        return new Categoria(categoriaDTO.getNome(),categoriaDTO.getTipo());
    }

    public static CategoriaDTO CategoriatoDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getNome(),categoria.getTipo());
    }

    public static Set<CategoriaDTO> categoriaSetEntityToDTO(Set<Categoria> categoriaSet) {
        Set<CategoriaDTO> categoriaDTOS = new HashSet<>();
        for (Categoria entity : categoriaSet) {
            categoriaDTOS.add(CategoriatoDTO(entity));
        }
        return categoriaDTOS;
    }
}
