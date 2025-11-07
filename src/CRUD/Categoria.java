package CRUD;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "categoria")
public class Categoria {
    private int id;
    private String nombre;

    // 🔹 Constructor vacío obligatorio para JAXB
    public Categoria() {
    }

    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    @XmlElement
    public int getId() { 
        return id; 
    }

    public void setId(int id) { 
        this.id = id; 
    }

    @XmlElement
    public String getNombre() { 
        return nombre; 
    }

    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
