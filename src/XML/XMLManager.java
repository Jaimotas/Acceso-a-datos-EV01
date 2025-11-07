package XML;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import CRUD.Categoria;
import CRUD.CategoriaCRUD;
import CRUD.Producto;
import CRUD.ProductoCRUD;
import Logs.FileLogger;
import SQL.DBconexion;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class XMLManager {

    private static final String XML_PATH = "src/resources/inventario.xml";
    private static FileLogger logs = new FileLogger();

    /**
     * Exporta todas las categorías y productos a un archivo XML.
     */
    public static void exportarInventario() {
        try {
             CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
            ProductoCRUD productoCRUD = new ProductoCRUD();

            List<Categoria> categorias = categoriaCRUD.listarCategorias();
            List<Producto> productos = productoCRUD.listarProductos();

            InventoryWrapper wrapper = new InventoryWrapper();
            wrapper.setCategorias(categorias);
            wrapper.setProductos(productos);

            JAXBContext context = JAXBContext.newInstance(InventoryWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(wrapper, new File(XML_PATH));
            logs.info("Inventario exportado correctamente a " + XML_PATH);
            System.out.println("Inventario exportado correctamente a " + XML_PATH);

        } catch (Exception e) {
            logs.error("Error al exportar inventario a XML: " + e.getMessage());
            System.err.println("Error al exportar inventario: " + e.getMessage());
        }
    }

    /**
     * Importa el inventario desde un archivo XML y lo inserta en la base de datos.
     */
    public static void importarInventario() {
        try {
            JAXBContext context = JAXBContext.newInstance(InventoryWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InventoryWrapper wrapper = (InventoryWrapper) unmarshaller.unmarshal(new File(XML_PATH));

            CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
            ProductoCRUD productoCRUD = new ProductoCRUD();
            FileLogger logs = new FileLogger();

            // 1️⃣ Importar categorías primero
            List<Categoria> categorias = wrapper.getCategorias();
            for (Categoria c : categorias) {
                try {
                    categoriaCRUD.agregarCategoria(c);
                } catch (Exception e) {
                    logs.warning("No se pudo agregar categoría: " + c.getNombre() + " -> " + e.getMessage());
                }
            }

            // 2️⃣ Luego los productos
            List<Producto> productos = wrapper.getProductos();
            for (Producto p : productos) {
                try {
                    productoCRUD.agregarProducto(p);
                } catch (Exception e) {
                    logs.warning("No se pudo agregar producto: " + p.getNombre() + " -> " + e.getMessage());
                }
            }

            // 3️⃣ (Opcional) Si hay movimientos
            if (wrapper.getMovimientos() != null) {
                try (Connection conn = DBconexion.getConnection()) {
                    String sql = "INSERT INTO movimientos (id_producto, tipo, cantidad, fecha) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        for (MovimientoXml m : wrapper.getMovimientos()) {
                            ps.setInt(1, m.getIdProducto());
                            ps.setString(2, m.getTipo());
                            ps.setInt(3, m.getCantidad());
                            ps.setTimestamp(4, Timestamp.valueOf(m.getFecha().replace("T", " ").substring(0, 19)));
                            ps.executeUpdate();
                        }
                    }
                }
            }

            logs.info("Inventario importado correctamente desde XML.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error al importar inventario desde XML: " + e.getMessage());
        }
    }

}
