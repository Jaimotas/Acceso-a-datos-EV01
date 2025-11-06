package XML;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movimiento")
public class MovimientoXml {
    private int id;
    private int productoId;
    private String tipo; // ENTRADA / SALIDA
    private int cantidad;
    private String fecha; // ISO string

    public MovimientoXml() {}
    public MovimientoXml(int id, int productoId, String tipo, int cantidad, String fecha){
        this.id = id; this.productoId = productoId; this.tipo = tipo; this.cantidad = cantidad; this.fecha = fecha;
    }

    @XmlAttribute public int getId(){ return id; }
    public void setId(int id){ this.id = id; }

    @jakarta.xml.bind.annotation.XmlElement(name="producto_id") public int getProductoId(){ return productoId; }
    public void setProductoId(int productoId){ this.productoId = productoId; }

    @jakarta.xml.bind.annotation.XmlElement public String getTipo(){ return tipo; }
    public void setTipo(String tipo){ this.tipo = tipo; }

    @jakarta.xml.bind.annotation.XmlElement public int getCantidad(){ return cantidad; }
    public void setCantidad(int cantidad){ this.cantidad = cantidad; }

    @jakarta.xml.bind.annotation.XmlElement public String getFecha(){ return fecha; }
    public void setFecha(String fecha){ this.fecha = fecha; }
}
