package org.cswteams.ms3.entity.policy;

import lombok.Getter;
import lombok.NonNull;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.category.Rotation;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
public class RotationPolicy extends Policy{
    @ManyToOne
    @NonNull
    private Rotation rotation;

    protected RotationPolicy(){
        super(new Shift(), UserCategoryPolicyValue.EXCLUDE);
    }

    public RotationPolicy(Rotation rotation, Shift shift, UserCategoryPolicyValue value){
        super(shift,value);
        this.rotation = rotation;
    }
}
