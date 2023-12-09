package org.cswteams.ms3.entity.policy;

import lombok.Getter;
import lombok.NonNull;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.category.Specialization;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
public class SpecializationPolicy extends Policy{
    @ManyToOne
    @NonNull
    private Specialization specialization;

    protected SpecializationPolicy(){
        super(new Shift(), UserCategoryPolicyValue.EXCLUDE);
    }

    public SpecializationPolicy(Specialization specialization, Shift shift, UserCategoryPolicyValue value){
        super(shift,value);
        this.specialization = specialization;
    }
}
