package org.cswteams.ms3.enums;

/**
 * Una festività rientra in una categoria che la correla
 * ad altre festività. In questo modo è possibile manipolare
 * gruppi di festività affini.
 */
public enum HolidayCategory {
    RELIGIOUS("Religious"), // festività religiose
    SECULAR("Secular"), // Laica
    CIVIL("Civil"),
    NATIONAL("National"),
    CORPORATE("Corporate"); // festività aziendali (ES: ponti, ferie, ...)
    String categoryName;
    HolidayCategory(String categoryName) {
        this.categoryName = categoryName;
    }
    //override the inherited method
    @Override
    public String toString() {
        return categoryName;
    }
    public HolidayCategory toCategory(String nameCategory){
        for(HolidayCategory h: values()){
            if(h.toString().equals(nameCategory)){
                return  h;
            }
        }
        return null;
    }
}

