package org.cswteams.ms3.enums;

/**
 * A holiday falls into a category that correlates it to other holidays.
 * In this way it is possible to manipulate groups of similar holidays.
 */
public enum HolidayCategory {

    /**
     * Religious holiday
     */
    RELIGIOUS("Religious"),

    /**
     * Secular holiday
     */
    SECULAR("Secular"),

    CIVIL("Civil"),

    NATIONAL("National"),

    /**
     * Company holidays, e.g. long weekends, holidays, ...
     */
    CORPORATE("Corporate");

    String categoryName;

    HolidayCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    //override the inherited method
    @Override
    public String toString() {
        return categoryName;
    }

    /**
     * Convert the <code>nameCategory</code> string into the corresponding <code>HolidayCategory</code>-
     *
     * @param nameCategory category name string
     * @return <code>HolidayCategory</code> enum object corresponding to <code>nameCategory</code>
     */
    public HolidayCategory toCategory(String nameCategory){
        for(HolidayCategory h: values()){
            if(h.toString().equals(nameCategory)){
                return  h;
            }
        }
        return null;
    }
}

