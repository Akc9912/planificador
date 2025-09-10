package aktech.planificador.DTO;

public class MateriaRequestDto {
    private String titulo;
    private String color;
    private String estado;

    public MateriaRequestDto(String titulo, String color, String estado) {
        this.titulo = titulo;
        this.color = color;
        this.estado = estado;
    }

    // Getters y Setters
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
