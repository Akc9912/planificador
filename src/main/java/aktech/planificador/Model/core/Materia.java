package aktech.planificador.Model.core;

import aktech.planificador.Model.enums.EstadoMateria;
import jakarta.persistence.*;

@Entity
@Table(name = "materias")
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoMateria estado;

    @Column(length = 7, nullable = false)
    private String color;

    private Boolean promocionable;
    private Double notaPromocion;
    private Double calificacion;

    public Materia() {
    }

    public Materia(Integer id, Usuario usuario, String titulo, EstadoMateria estado, String color,
            Boolean promocionable, Double notaPromocion, Double calificacion) {
        this.id = id;
        this.usuario = usuario;
        this.titulo = titulo;
        this.estado = estado;
        this.color = color;
        this.promocionable = promocionable;
        this.notaPromocion = notaPromocion;
        this.calificacion = calificacion;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public EstadoMateria getEstado() {
        return estado;
    }

    public void setEstado(EstadoMateria estado) {
        this.estado = estado;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getPromocionable() {
        return promocionable;
    }

    public void setPromocionable(Boolean promocionable) {
        this.promocionable = promocionable;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public Double getNotaPromocion() {
        return notaPromocion;
    }

    public void setNotaPromocion(Double notaPromocion) {
        this.notaPromocion = notaPromocion;
    }
}
