package org.cswteams.ms3.entity.policy;

import lombok.Getter;
import lombok.NonNull;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.category.PermanentCondition;
import org.cswteams.ms3.entity.category.TemporaryCondition;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Modella se una categoria utente è vietata o necessaria per un turno
 */
@Entity
@Getter
public class ConditionPolicy extends Policy{
    @ManyToOne
    private PermanentCondition permanentCondition;

    @ManyToOne
    private TemporaryCondition temporaryCondition;
   protected ConditionPolicy(){
       super(new Shift(),UserCategoryPolicyValue.EXCLUDE);
   }

   public ConditionPolicy(PermanentCondition condition, Shift shift, UserCategoryPolicyValue value){
       super(shift,value);
       this.permanentCondition = condition;
   }

    public ConditionPolicy(PermanentCondition condition, TemporaryCondition temporaryCondition ,Shift shift, UserCategoryPolicyValue value){
        super(shift,value);
        this.permanentCondition = condition;
        this.temporaryCondition = temporaryCondition;
    }
}