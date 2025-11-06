package aktech.planificador.Model.core;

import java.util.Optional;

import jakarta.persistence.*;

@Entity
@Table(name = "evento")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 7, nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Optional<Usuario> usuario;

    public Evento(String titulo, String color, Optional<Usuario> u) {
        this.titulo = titulo;
        this.color = color;
        this.usuario = u;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Optional<Usuario> getUsuario() {
        return usuario;
    }

    public void setUsuario(Optional<Usuario> usuario) {
        this.usuario = usuario;
    }
}
