package org.cswteams.ms3.dto.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.utils.admissible_values.AdmissibleValues;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
public class PreferenceDTOIn {

    private Long id ;

    @NotNull
    @Range(min = 1, max = 31)
    private final Integer day;

    @NotNull
    @Range(min = 1, max = 12)
    private final Integer month;

    @NotNull
    private final Integer year;

    @NotEmpty
    private final Set<@AdmissibleValues(values = {"MORNING", "AFTERNOON", "NIGHT"}) String> turnKinds;

    /**
     *
     * @param day The day of the month of the preference
     * @param month The month of the preference
     * @param year The year of the preference
     * @param turnKinds A list of shift time slots relative to the preference
     */
    public PreferenceDTOIn(int day, int month, int year, Set<String> turnKinds) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.turnKinds = turnKinds;
    }

    /**
     *
     * @param id The id of the preference, if present
     * @param day The day of the month of the preference
     * @param month The month of the preference
     * @param year The year of the preference
     * @param turnKinds A list of shift time slots relative to the preference
     */
    public PreferenceDTOIn(@JsonProperty("id") Long id, @JsonProperty("day") int day,
                           @JsonProperty("month") int month, @JsonProperty("year") int year,
                           @JsonProperty Set<String> turnKinds) {
        this(day, month, year, turnKinds);
        this.id = id ;
    }
}
