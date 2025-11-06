package XML;

import java.util.List;
import CRUD.Categoria;
import CRUD.Producto;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "inventario")
public class InventoryWrapper {

    private List<Categoria> categorias;
    private List<Producto> productos;

    @XmlElement(name = "categoria")
    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @XmlElement(name = "producto")
    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
