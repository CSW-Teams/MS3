package org.cswteams.ms3.enums;

public enum CategoriaUtentiEnum {
    DONNA_INCINTA("donna_incinta"),
    OVER_62("over_62"),
    IN_FERIE("in_ferie"),
    IN_MALATTIA("in_malattia"),
    CATEGORIA_PROTETTA("categoria_protetta"),
    UROLOGIA("urologia"),
    CARDIOLOGIA("cardiologia"),
    PEDIARIA("pediatria"),
    ONCOLOGIA("oncologia");



    private String db_code;

    CategoriaUtentiEnum(String db_code){
        this.db_code = db_code;
    }

    public String getDbCode() {
        return this.db_code;
    }

    public void setDbCode(String db_code) {
        this.db_code = db_code;
    }

}
