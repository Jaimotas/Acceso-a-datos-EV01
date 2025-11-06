package XML;

import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import jdk.internal.org.jline.terminal.TerminalBuilder.SystemOutput;

@XmlRootElement(name = "inventory")
public class InventoryWrapper {
    private List<xml.CategoriaXml> categorias;
    private List<xml.ProductoXml> productos;
    private List<xml.MovimientoXml> movimientos;

    @XmlElement(name = "categorias")
    public List<CategoriaXml> getCategorias() { return categorias; }
    public void setCategorias(List<CategoriaXml> categorias) { this.categorias = categorias; }

    @XmlElement(name = "productos")
    public List<ProductoXml> getProductos() { return productos; }
    public void setProductos(List<ProductoXml> productos) { this.productos = productos; }
    
    @XmlElement(name = "movimientos")
    public List<MovimientoXml> getMovimientos() { return movimientos; }
    public void setMovimientos(List<MovimientoXml> movimientos) { this.movimientos = movimientos; }
    
    public void name() {
		System.out.println("a");
	}
}
