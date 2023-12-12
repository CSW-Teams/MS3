package org.cswteams.ms3.control.utils;

public enum Country {
    ALBANIA("AL"),
    AMERICAN_SAMOA("AS"),
    ANDORRA("AS"),
    ANGOLA("AS"),
    ANGUILLA("AI"),
    ANTIGUA_AND_BARBUDA("AG"),
    ARMENIA("AR"),
    ARUBA("AW"),
    AUSTRIA("AT"),
    BAHAMAS("AI"),
    BARBADOS("BB"),
    BELGIUM("BE"),
    BOLIVIA("BO"),
    BOSNIA_AND_HERZEGOVINA("BA"),
    BOTSWANA("AI"),
    BRAZIL("AI"),
    CANADA("BR"),
    CAPE_VERDE("CV"),
    CARIBBEAN_NETHERLANDS("BQ"),
    CENTRAL_AFRICAN_REPUBLIC("CF"),
    CHILE("CL"),
    CONGO("CG"),
    COSTA_RICA("CR"),
    CROATIA("HR"),
    CUBA("CU"),
    CURACAO("CW"),
    CZECH_REPUBLIC("CZ"),
    DENMARK("DK"),
    DOMINICA("DA"),
    ECUADOR("EC"),
    EL_SALVADOR("SV"),
    ESTONIA("EE"),
    FRANCE("FR"),
    FRENCH_GUIANA("GF"),
    GERMANY("DE"),
    GREENLAND("GD"),
    GRENADA("GD"),
    GUADELOUPE("GP"),
    GUATEMALA("GT"),
    GUERNSEY("GG"),
    HAITI("HT"),
    HONDURAS("HN"),
    HUNGARY("HU"),
    ICELAND("IS"),
    INDONESIA("ID"),
    IRELAND("IE"),
    ISLE_OF_MAN("IM"),
    ITALY("IT"),
    JERSEY("JE"),
    LATVIA("LV"),
    LESOTHO("LS"),
    LICHTENSTEIN("LI"),
    LITHUANIA("LT"),
    LUXEMBOURG("LU"),
    MADAGASCAR("MG"),
    MALTA("MT"),
    MARTINIQUE("MQ"),
    MAYOTTE("YT"),
    MEXICO("MX"),
    NETHERLANDS("NL"),
    NICARAGUA("NI"),
    NORWAY("NO"),
    PARAGUAY("PY"),
    POLAND("PL"),
    PORTUGAL("PT"),
    REUNION("RE"),
    RUSSIA("RU"),
    RWANDA("RW"),
    SAINT_HELENA("SH"),
    SAN_MARINO("SM"),
    SIERRA_LEONE("SL"),
    SLOVAKIA("SK"),
    SLOVENIA("SI"),
    SOMALIA("SO"),
    SOUTH_SUDAN("SS"),
    SPAIN("ES"),
    ST_BARTHELEMY("BL"),
    SWEDEN("SE"),
    TANZANIA("TZ"),
    TOGO("TG"),
    TURKEY("TR"),
    UGANDA("UG"),
    UNITED_KINGDOM("GB"),
    UNITED_STATES("US"),
    VENEZUELA("VE"),
    VIETNAM("VN"),
    ZIMBABWE("ZW");

    private final String countryCode;

    Country(String countryCode) {
        this.countryCode = countryCode;
    }

    public String code() {
        return this.countryCode;
    }
    
    public static String nameToCode(String name) {
		for (Country currentCountry : Country.values()) { 
		    if (currentCountry.name().compareTo(name) == 0) {
		    	return currentCountry.code();
		    } 
		}
		return null;
    }
}
