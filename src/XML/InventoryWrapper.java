package XML;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import CRUD.Categoria;
import CRUD.Producto;

@XmlRootElement(name = "inventario")
public class InventoryWrapper {
    private List<Categoria> categorias;
    private List<Producto> productos;
    private List<MovimientoXml> movimientos;

    public InventoryWrapper() {
    }

    // ===== CATEGORÍAS =====
    @XmlElement(name = "categoria")
    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    // ===== PRODUCTOS =====
    @XmlElement(name = "producto")
    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    // ===== MOVIMIENTOS =====
    @XmlElement(name = "movimiento")
    public List<MovimientoXml> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoXml> movimientos) {
        this.movimientos = movimientos;
    }
}
