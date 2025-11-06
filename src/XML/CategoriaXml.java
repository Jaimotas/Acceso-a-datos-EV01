package XML;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "categoria")
public class CategoriaXml {
    private int id;
    private String nombre;

    public CategoriaXml() {}
    public CategoriaXml(int id, String nombre) { this.id = id; this.nombre = nombre; }

    @XmlAttribute
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @jakarta.xml.bind.annotation.XmlElement
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
