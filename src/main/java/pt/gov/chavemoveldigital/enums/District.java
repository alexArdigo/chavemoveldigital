package pt.gov.chavemoveldigital.enums;

public enum District {
    VIANA_DO_CASTELO("Viana do Castelo"),
    BRAGA("Braga"),
    VILA_REAL("Vila Real"),
    BRAGANCA("Bragança"),
    PORTO("Porto"),
    AVEIRO("Aveiro"),
    VISEU("Viseu"),
    GUARDA("Guarda"),

    // Portugal Continental - Centro
    COIMBRA("Coimbra"),
    LEIRIA("Leiria"),
    CASTELO_BRANCO("Castelo Branco"),

    // Portugal Continental - Lisboa e Vale do Tejo
    SANTAREM("Santarém"),
    LISBOA("Lisboa"),
    PORTALEGRE("Portalegre"),

    // Portugal Continental - Alentejo
    EVORA("Évora"),
    BEJA("Beja"),

    // Portugal Continental - Algarve
    FARO("Faro"),

    // Área Metropolitana de Lisboa
    SETUBAL("Setúbal"),

    // Regiões Autónomas
    MADEIRA("Madeira"),
    ACORES("Açores"),

    // Círculos da Emigração
    EUROPA("Europa"),
    RESTO_DO_MUNDO("Resto do Mundo"),

    ELECTORAL_CIRCLE("Electoral Circle");

    private final String value;

    District(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}