package org.cswteams.ms3.dto;

import lombok.Data;
import lombok.Getter;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.RuoloNumero;
import org.cswteams.ms3.entity.UserCategoryPolicyValue;
import org.cswteams.ms3.entity.category.Condition;
import org.cswteams.ms3.entity.category.PermanentCondition;
import org.cswteams.ms3.entity.category.Rotation;
import org.cswteams.ms3.entity.category.Specialization;
import org.cswteams.ms3.entity.policy.ConditionPolicy;
import org.cswteams.ms3.entity.policy.RotationPolicy;
import org.cswteams.ms3.entity.policy.SpecializationPolicy;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RotationDTO {

    @Getter
    private TipologiaTurno tipologiaTurno;

    @Getter
    private LocalTime oraInizio;

    private Duration durata;

    @Getter
    private ServizioDTO servizio;

    private MansioneEnum mansione;

    @Getter
    private Set<ConditionPolicy> conditionPolicies;

    @Getter
    private Set<RotationPolicy> rotationPolicies;

    @Getter
    private Set<SpecializationPolicy> specializationPolicies;

    private long id;

    private boolean reperibilitaAttiva;

    private List<RuoloNumero> ruoliNumero;

    public RotationDTO(){}

    public RotationDTO(long id, TipologiaTurno tipologiaTurno, LocalTime inizio, Duration durata, ServizioDTO servizio, MansioneEnum mansione, boolean reperibilitaAttiva, List<RuoloNumero> ruoliNumero){
        this.durata = durata;
        this.oraInizio = inizio;
        this.servizio = servizio;
        this.mansione = mansione;
        this.tipologiaTurno = tipologiaTurno;
        this.id = id;
        this.reperibilitaAttiva = reperibilitaAttiva;
        this.ruoliNumero = ruoliNumero;
    }

    public void setBannedConditions(Set<Condition> categorieVietate){
        Set<ConditionPolicy> policies = new HashSet<>();
        for (Condition cu : categorieVietate) {
            policies.add(new ConditionPolicy((PermanentCondition) cu, new Shift(), UserCategoryPolicyValue.EXCLUDE));
        }
        this.setConditionPolicies(policies);
    }

    public void setBannedRotations(Set<org.cswteams.ms3.entity.category.Rotation> categorieVietate){
        Set<RotationPolicy> policies = new HashSet<>();
        for (org.cswteams.ms3.entity.category.Rotation cu : categorieVietate) {
            policies.add(new RotationPolicy(cu, new Shift(), UserCategoryPolicyValue.EXCLUDE));
        }
        this.setRotationPolicies(policies);
    }

    public void setBannedSpecialization(Set<Specialization> categorieVietate){
        Set<SpecializationPolicy> policies = new HashSet<>();
        for (Specialization cu : categorieVietate) {
            policies.add(new SpecializationPolicy(cu, new Shift(), UserCategoryPolicyValue.EXCLUDE));
        }
        this.setSpecializationPolicies(policies);
    }


    public Set<Condition> getBannedConditions(){
        Set<Condition> bannedCategories = new HashSet<>();
        for (ConditionPolicy p : this.getConditionPolicies()) {
            if (p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE)) {
                bannedCategories.add(p.getPermanentCondition());
            }
        }
        return bannedCategories;
    }

    public Set<Rotation> getBannedRotations(){
        Set<org.cswteams.ms3.entity.category.Rotation> bannedRotation = new HashSet<>();
        for (RotationPolicy p : this.getRotationPolicies()) {
            if (p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE)) {
                bannedRotation.add(p.getRotation());
            }
        }
        return bannedRotation;
    }

    public Set<Specialization> getBannedSpecializations(){
        Set<Specialization> bannedSpecializations = new HashSet<>();
        for (SpecializationPolicy p : this.getSpecializationPolicies()) {
            if (p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE)) {
                bannedSpecializations.add(p.getSpecialization());
            }
        }
        return bannedSpecializations;
    }
}
