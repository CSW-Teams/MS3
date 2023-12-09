package org.cswteams.ms3.entity.policy;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NonNull;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.category.Condition;

/**
 * Modella se una categoria utente è vietata o necessaria per un turno
 */
@Entity
@Getter
public class ConditionPolicy extends Policy{
    @ManyToOne
    @NonNull
    private Condition condition;

   protected ConditionPolicy(){
       super(new Turno(),UserCategoryPolicyValue.EXCLUDE);
   }

   public ConditionPolicy(Condition condition, Turno turno, UserCategoryPolicyValue value){
       super(turno,value);
       this.condition = condition;
   }
}