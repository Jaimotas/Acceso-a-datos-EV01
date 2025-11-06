package XML;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "producto")
public class ProductoXml {
    private int id;
    private String nombre;
    private int categoriaId;
    private int stock;
    private double precio;

    public ProductoXml() {}
    public ProductoXml(int id, String nombre, int categoriaId, int stock, double precio){
        this.id = id; this.nombre = nombre; this.categoriaId = categoriaId; this.stock = stock; this.precio = precio;
    }

    @XmlAttribute public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @jakarta.xml.bind.annotation.XmlElement public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @jakarta.xml.bind.annotation.XmlElement(name="categoria_id") public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    @jakarta.xml.bind.annotation.XmlElement public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @jakarta.xml.bind.annotation.XmlElement public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
