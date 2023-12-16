package org.cswteams.ms3.control.preferenze;


public class CalendarSettingBuilder{
    private ServiceDataENUM serviceDataENUM;
    private String urlDateNager = "https://date.nager.at/api/v3/PublicHolidays";
    public CalendarSettingBuilder(ServiceDataENUM e){
        this.serviceDataENUM=e;
    }
    public CalendarSetting create(String year,String CountryCode){
        CalendarSetting s = null;
        if(ServiceDataENUM.DATANEAGER==serviceDataENUM){
            s = new CalendarSetting(this.urlDateNager+"/"+year+"/"+ CountryCode);
        }
        return s;
    }
}
