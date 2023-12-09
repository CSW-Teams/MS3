package org.cswteams.ms3.entity.policy;

import lombok.Getter;
import lombok.NonNull;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.category.Rotation;
import org.cswteams.ms3.entity.category.Specialization;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
public class RotationPolicy extends Policy{
    @ManyToOne
    @NonNull
    private Rotation rotation;

    protected RotationPolicy(){
        super(new Turno(), UserCategoryPolicyValue.EXCLUDE);
    }

    public RotationPolicy(Rotation rotation, Turno turno, UserCategoryPolicyValue value){
        super(turno,value);
        this.rotation = rotation;
    }
}
