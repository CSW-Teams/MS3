package org.cswteams.ms3.entity.policy;

import lombok.Getter;
import lombok.NonNull;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.category.Structure;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
public class RotationPolicy extends Policy{
    @ManyToOne
    @NonNull
    private Structure structure;

    protected RotationPolicy(){
        super(new Shift(), UserCategoryPolicyValue.EXCLUDE);
    }

    public RotationPolicy(Structure structure, Shift shift, UserCategoryPolicyValue value){
        super(shift,value);
        this.structure = structure;
    }
}
