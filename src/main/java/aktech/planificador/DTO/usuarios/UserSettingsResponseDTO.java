package aktech.planificador.DTO.usuarios;

import aktech.planificador.Model.enums.DiaSemana;

public class UserSettingsResponseDto {
    private Integer id;
    private String theme;
    private Boolean notificaciones;
    private Boolean formatoHora;
    private Integer inicioPlanner;
    private Integer finPlanner;
    private DiaSemana primerDia;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(Boolean notificaciones) {
        this.notificaciones = notificaciones;
    }

    public Boolean getFormatoHora() {
        return formatoHora;
    }

    public void setFormatoHora(Boolean formatoHora) {
        this.formatoHora = formatoHora;
    }

    public Integer getInicioPlanner() {
        return inicioPlanner;
    }

    public void setInicioPlanner(Integer inicioPlanner) {
        this.inicioPlanner = inicioPlanner;
    }

    public Integer getFinPlanner() {
        return finPlanner;
    }

    public void setFinPlanner(Integer finPlanner) {
        this.finPlanner = finPlanner;
    }

    public DiaSemana getPrimerDia() {
        return primerDia;
    }

    public void setPrimerDia(DiaSemana primerDia) {
        this.primerDia = primerDia;
    }
}
