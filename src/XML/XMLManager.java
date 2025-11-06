package XML;

import java.io.File;
import java.util.List;

import CRUD.Categoria;
import CRUD.CategoriaCRUD;
import CRUD.Producto;
import CRUD.ProductoCRUD;
import Logs.FileLogger;
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

            File file = new File(XML_PATH);
            if (!file.exists()) {
                logs.warning("No se encontró el archivo XML en " + XML_PATH);
                System.err.println("Archivo XML no encontrado.");
                return;
            }

            InventoryWrapper wrapper = (InventoryWrapper) unmarshaller.unmarshal(file);

            CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
            ProductoCRUD productoCRUD = new ProductoCRUD();

            if (wrapper.getCategorias() != null) {
                for (Categoria c : wrapper.getCategorias()) {
                    categoriaCRUD.agregarCategoria(c);
                }
            }

            if (wrapper.getProductos() != null) {
                for (Producto p : wrapper.getProductos()) {
                    productoCRUD.agregarProducto(p);
                }
            }

            logs.info("Inventario importado correctamente desde " + XML_PATH);
            System.out.println("Inventario importado correctamente desde " + XML_PATH);

        } catch (Exception e) {
            logs.error("Error al importar inventario desde XML: " + e.getMessage());
            System.err.println("Error al importar inventario: " + e.getMessage());
        }
    }
}
