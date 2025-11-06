package SQL;

import java.io.BufferedReader;
import java.util.List;


import CRUD.Categoria;
import CRUD.CategoriaCRUD;
import CRUD.Producto;
import CRUD.ProductoCRUD;
import Logs.FileLogger;

public class CargaCSVs {

    private static CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
    private static ProductoCRUD productoCRUD = new ProductoCRUD();
    private static FileLogger logs = new FileLogger();

    public static void cargarCSVEnBD(String rutaCsv) {
    	System.out.println("Cargando base de datos desde CSV...");
        try (BufferedReader br = new BufferedReader(new java.io.FileReader(rutaCsv))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) { // saltamos cabecera
                    primeraLinea = false;
                    continue;
                }

                String[] partes = linea.split(";");
                if (partes.length != 5) {
                    logs.warning("Línea mal formada: " + linea);
                    continue;
                }

                try {
                    int id_Producto = Integer.parseInt(partes[0].trim());
                    String nombreProducto = partes[1].trim();
                    String nombreCategoria = partes[2].trim();
                    double precio = Double.parseDouble(partes[3].trim());
                    int stock = Integer.parseInt(partes[4].trim());

                    // ----------------------
                    // INSERTAR CATEGORÍA SI NO EXISTE
                    // ----------------------
                    List<Categoria> categorias = categoriaCRUD.listarCategorias();
                    int categoriaId = -1;
                    for (Categoria c : categorias) {
                        if (c.getNombre().equalsIgnoreCase(nombreCategoria)) {
                            categoriaId = c.getId();
                            break;
                        }
                    }

                    if (categoriaId == -1) {
                        Categoria nuevaCat = new Categoria(1, nombreCategoria);
                        categoriaCRUD.agregarCategoria(nuevaCat);
                        logs.info("Categoría añadida: " + nombreCategoria);

                        // Recuperamos el ID recién creado
                        categorias = categoriaCRUD.listarCategorias();
                        for (Categoria c : categorias) {
                            if (c.getNombre().equalsIgnoreCase(nombreCategoria)) {
                                categoriaId = c.getId();
                                break;
                            }
                        }
                    }

                    // ----------------------
                    // VALIDAR DUPLICADOS DE PRODUCTO
                    // ----------------------
                    List<Producto> productos = productoCRUD.listarProductos();
                    boolean productoDuplicado = false;
                    for (Producto p : productos) {
                        if (p.getNombre().equalsIgnoreCase(nombreProducto)) {
                            productoDuplicado = true;
                            logs.warning("Producto duplicado encontrado: " + nombreProducto + " (no se insertará)");
                            break;
                        }
                    }

                    // ----------------------
                    // INSERTAR PRODUCTO (si no es duplicado)
                    // ----------------------
                    if (!productoDuplicado) {
                        Producto p = new Producto(id_Producto, nombreProducto, categoriaId, stock, precio);
                        productoCRUD.agregarProducto(p);
                        logs.info("Producto añadido: " + nombreProducto);
                    }

                } catch (NumberFormatException e) {
                    logs.error("Error de formato en línea: " + linea + " - " + e.getMessage());
                }
            }
            logs.info("Carga de CSV completada");
            System.out.println("Carga de CSV completada.");
        } catch (Exception e) {
            logs.fatal("Error al leer CSV: " + e.getMessage());
        }
    }
}
