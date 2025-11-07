package XML;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movimiento")
public class MovimientoXml {
    private int id;
    private int idProducto;
    private String tipo;
    private int cantidad;
    private String fecha; // formato ISO 8601 (String para JAXB)

    public MovimientoXml() {
    }

    public MovimientoXml(int id, int idProducto, String tipo, int cantidad, String fecha) {
        this.id = id;
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    @XmlElement
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @XmlElement(name = "id_producto")
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    @XmlElement
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @XmlElement
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @XmlElement
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return "MovimientoXml{" +
                "id=" + id +
                ", idProducto=" + idProducto +
                ", tipo='" + tipo + '\'' +
                ", cantidad=" + cantidad +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
