package XML;

import CRUD.CategoriaCRUD;
import CRUD.ProductoCRUD;
import CRUD.Producto;
import CRUD.Categoria;
import Logs.FileLogger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.JAXBException;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import SQL.DBconexion;

public class XmlService {
    private FileLogger logs = new FileLogger();
    private CategoriaCRUD categoriaDAO = new CategoriaCRUD();
    private ProductoCRUD productoDAO = new ProductoCRUD();

    private static final String XML_PATH = "src/resources/inventario.xml";
    private static final String XSD_PATH = "src/resources/inventario.xsd";

    /**
     * Exporta las categorías, productos y movimientos a un archivo XML.
     */
    public void exportarInventario() {
        try {
            InventoryWrapper wrapper = new InventoryWrapper();

            // ===== CATEGORÍAS =====
            List<Categoria> cats = new ArrayList<>();
            for (Categoria c : categoriaDAO.listarCategorias()) {
                cats.add(new Categoria(c.getId(), c.getNombre()));
            }
            wrapper.setCategorias(cats);

            // ===== PRODUCTOS =====
            List<Producto> prods = new ArrayList<>();
            for (Producto p : productoDAO.listarProductos()) {
                prods.add(new Producto(p.getId(), p.getNombre(), p.getCategoriaId(), p.getStock(), p.getPrecio()));
            }
            wrapper.setProductos(prods);

            // ===== MOVIMIENTOS =====
            List<MovimientoXml> movs = new ArrayList<>();

            String sql = "SELECT id, id_producto, tipo, cantidad, fecha FROM movimientos ORDER BY fecha DESC";

            try (Connection conn = DBconexion.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    MovimientoXml movimiento = new MovimientoXml();
                    movimiento.setId(rs.getInt("id"));
                    movimiento.setIdProducto(rs.getInt("id_producto"));
                    movimiento.setTipo(rs.getString("tipo"));
                    movimiento.setCantidad(rs.getInt("cantidad"));
                    movimiento.setFecha(rs.getTimestamp("fecha").toInstant().toString());
                    movs.add(movimiento);
                }

                wrapper.setMovimientos(movs);
                logs.info("Movimientos exportados correctamente a XML. Total: " + movs.size());

            } catch (SQLException e) {
                logs.error("Error al obtener movimientos desde la base de datos: " + e.getMessage());
            }


            // ===== MARSHALLING (Exportar a XML) =====
            JAXBContext ctx = JAXBContext.newInstance(
                    InventoryWrapper.class, CategoriaXml.class, ProductoXml.class, MovimientoXml.class);
            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // Validar con XSD si existe
            File xsd = new File(XSD_PATH);
            if (xsd.exists()) {
                SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                Schema schema = sf.newSchema(xsd);
                m.setSchema(schema);
                logs.info("Archivo XSD encontrado y aplicado para validación.");
            }

            // Crear directorio si no existe
            File file = new File(XML_PATH);
            file.getParentFile().mkdirs();

            m.marshal(wrapper, file);
            logs.info("Inventario exportado correctamente a XML: " + XML_PATH);
            System.out.println("Inventario exportado correctamente a: " + XML_PATH);

        } catch (Exception e) {
            logs.error("Error exportando inventario a XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Importa el inventario desde el XML (valida con XSD si está disponible).
     */
    public void importarInventario() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(
                    InventoryWrapper.class, CategoriaXml.class, ProductoXml.class, MovimientoXml.class);
            Unmarshaller u = ctx.createUnmarshaller();

            File xmlFile = new File(XML_PATH);
            if (!xmlFile.exists()) {
                logs.warning("Archivo XML no encontrado en: " + XML_PATH);
                System.err.println("⚠️ No se encontró el archivo XML.");
                return;
            }

            // Validar con XSD si existe
            File xsd = new File(XSD_PATH);
            if (xsd.exists()) {
                SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                Schema schema = sf.newSchema(xsd);
                u.setSchema(schema);
                logs.info("Validación XSD habilitada durante la importación.");
            }

            InventoryWrapper wrapper = (InventoryWrapper) u.unmarshal(xmlFile);

            // ===== Insertar en la base de datos =====
            for (Categoria c : wrapper.getCategorias()) {
                categoriaDAO.agregarCategoria(new Categoria(0, c.getNombre()));
            }
            for (Producto p : wrapper.getProductos()) {
                productoDAO.agregarProducto(new Producto(0, p.getNombre(), p.getCategoriaId(), p.getStock(), p.getPrecio()));
            }

            logs.info("Inventario importado correctamente desde XML: " + XML_PATH);
            System.out.println("Inventario importado correctamente desde: " + XML_PATH);

        } catch (SAXException sax) {
            logs.error("Error de validación XSD: " + sax.getMessage());
            System.err.println("Error de validación del XML: " + sax.getMessage());
        } catch (JAXBException jax) {
            logs.error("Error JAXB al importar XML: " + jax.getMessage());
            System.err.println("Error al procesar el XML: " + jax.getMessage());
        } catch (Exception e) {
            logs.error("Error general importando inventario desde XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
