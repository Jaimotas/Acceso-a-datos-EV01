package CRUD;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "producto")
public class Producto {
    private int id;
    private String nombre;
    private int categoriaId;
    private int stock;
    private double precio;

    // 🔹 Constructor vacío obligatorio para JAXB
    public Producto() {
    }

    public Producto(int id, String nombre, int categoriaId, int stock, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.stock = stock;
        this.precio = precio;
    }

    public Producto(String nombre, int categoriaId, int stock, double precio) {
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.stock = stock;
        this.precio = precio;
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

    @XmlElement(name = "categoria_id")
    public int getCategoriaId() { 
        return categoriaId; 
    }

    public void setCategoriaId(int categoriaId) { 
        this.categoriaId = categoriaId; 
    }

    @XmlElement
    public int getStock() { 
        return stock; 
    }

    public void setStock(int stock) { 
        this.stock = stock; 
    }

    @XmlElement
    public double getPrecio() { 
        return precio; 
    }

    public void setPrecio(double precio) { 
        this.precio = precio; 
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoriaId=" + categoriaId +
                ", stock=" + stock +
                ", precio=" + precio +
                '}';
    }
}
