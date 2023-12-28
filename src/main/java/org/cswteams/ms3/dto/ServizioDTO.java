package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cswteams.ms3.entity.Task;

import java.util.List;

@Data
@AllArgsConstructor
public class ServizioDTO {
    private String nome;
    private List<Task> tasks;
}
