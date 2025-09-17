package aktech.planificador.Model.core;

import jakarta.persistence.*;

import aktech.planificador.Model.enums.DiaSemana;
import aktech.planificador.Model.enums.Theme;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Theme theme;

    private Boolean notificaciones;
    private Boolean formatoHora;
    private Integer inicioPlanner;
    private Integer finPlanner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DiaSemana primerDia;

    public UserSettings() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
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
