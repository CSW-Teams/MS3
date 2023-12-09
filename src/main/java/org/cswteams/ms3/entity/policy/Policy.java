package org.cswteams.ms3.entity.policy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;

import javax.persistence.*;

@MappedSuperclass
@Getter
public abstract class Policy {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonIgnore
    @NonNull
    private Shift shift;

    /**
     * La registrazione di questa policy nel db implica che tale categoria è da escludere
     * o da includere per un shift. Se così non fosse, non sarebbe stata registrata.
     *
     *
     */
    @NonNull
    @Enumerated(value = EnumType.STRING)
    private UserCategoryPolicyValue policy;


    public Policy(Shift shift, UserCategoryPolicyValue value){
        this.shift = shift;
        this.policy = value;
    }

}
