package org.cswteams.ms3.entity.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A <i>condition</i> that can be related to a <code>Doctor</code>.
 *
 * @see PermanentCondition
 * @see TemporaryCondition
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "kind"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PermanentCondition.class, name = "permanent"),
        @JsonSubTypes.Type(value = TemporaryCondition.class, name = "temporary")
})
@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A string descripting the type of the <i>condition</i>.
     */
    @NotNull
    private String type;

    /**
     * Create a <i>condition</i> as descripted by <code>type</code>.
     *
     * @param type condition description
     */
    public Condition(String type) {
        this.type = type;
    }

    /**
     * Default constructor needed by Lombok
     */
    protected Condition() {

    }

}

