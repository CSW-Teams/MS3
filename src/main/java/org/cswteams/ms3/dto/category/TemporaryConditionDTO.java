package org.cswteams.ms3.dto.category;


public class TemporaryConditionDTO extends CategoryDTO{
    private final long startDate;
    private final long endDate;
    public TemporaryConditionDTO(String name, long startDate, long endDate){
        super(name);
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
