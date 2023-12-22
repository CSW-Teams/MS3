package org.cswteams.ms3.dto.category;

import lombok.Data;

@Data
public abstract class CategoryDTO {

    private String name;

    public CategoryDTO(String name) {
        this.name = name;
    }

    private CategoryDTO(){

    }


}
