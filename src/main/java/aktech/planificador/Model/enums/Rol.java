package aktech.planificador.Model.enums;

public enum Rol {

    ADMIN("ADMIN"),
    USUARIO("USUARIO");

    private String value;

    Rol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
    