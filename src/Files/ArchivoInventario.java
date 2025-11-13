package Files;

import java.io.*;
import java.util.*;
import SQL.DBconexion;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Logs.FileLogger;
import CRUD.CategoriaCRUD;

public class ArchivoInventario {
	private static CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
    private static final String INVENTARIO_FILE = "src/resources/inventario.txt";
    private final static String LOG_FILE= "logs.txt";
    private static FileLogger logs = new FileLogger();

    // Exporta la base de datos al inventario.txt
    public static void cargarInventarioDeBD() {
        List<String> lineas = new ArrayList<>();
        String sql = """
            SELECT p.id, p.nombre, c.nombre AS categoria, p.precio, p.stock
            FROM productos p
            JOIN categorias c ON p.categoria_id = c.id
            ORDER BY p.id ASC;
        """;

        try (Connection conn = DBconexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");
                lineas.add(id + ";" + nombre + ";" + categoria + ";" + precio + ";" + stock);
            }

            guardarArchivo(lineas);
            logs.info("Base de datos exportada correctamente a inventario.txt");

        } catch (SQLException e) {
            logs.error("Error SQL al exportar la base de datos a TXT: " + e.getMessage());
        } catch (Exception e) {
            logs.error("Error al exportar la base de datos a TXT: " + e.getMessage());
        }
    }

    // Guarda la lista de líneas en el archivo
    public static void guardarArchivo(List<String> lineas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(INVENTARIO_FILE))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
            logs.registro("Inventario guardado correctamente en " + INVENTARIO_FILE);
        } catch (IOException e) {
            logs.error("Error al guardar inventario.txt: " + e.getMessage());
        }
    }

    // Carga inventario.txt en memoria
    public static List<String> cargarInventario() {
        List<String> inventario = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INVENTARIO_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                inventario.add(linea);
            }
            logs.registro("Inventario cargado desde TXT correctamente");
        } catch (FileNotFoundException e) {
            logs.warning("No se encontró inventario.txt: " + e.getMessage());
        } catch (IOException e) {
            logs.error("Error al leer inventario.txt: " + e.getMessage());
        }
        return inventario;
    }

    // Copia de seguridad del inventario
    public static void backupInventarioTXT() {
    	String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String backupFile = "src/resources/inventario_backup"+fecha+".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(INVENTARIO_FILE));
             BufferedWriter bw = new BufferedWriter(new FileWriter(backupFile))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                bw.write(linea);
                bw.newLine();
            }
            logs.registro("Copia de seguridad creada correctamente: " + backupFile);
        } catch (FileNotFoundException e) {
            logs.warning("No se encontró inventario.txt para backup: " + e.getMessage());
        } catch (IOException e) {
            logs.error("Error al crear copia de seguridad: " + e.getMessage());
        }
    }

    // Lista todo el inventario en consola
    public static void listarInventarioTXT(List<String> inventario) {
        System.out.println("\n===== INVENTARIO =====");
        for (String linea : inventario) {
            System.out.println(linea);
        }
        logs.registro("Inventario listado correctamente.");
    }

    // Agregar producto al inventario
    public static void agregarProductoTXT(List<String> inventario) {
        Scanner sc = new Scanner(System.in);
        try {
            // Calcular ID auto incremental
            int id = 1; // valor por defecto si la lista está vacía
            for (String linea : inventario) {
                String[] partes = linea.split(";");
                int idActual = Integer.parseInt(partes[0]);
                if (idActual >= id) {
                    id = idActual + 1;
                }
            }
            System.out.println("ID Producto asignado automáticamente: " + id);

            System.out.print("Nombre: ");
            String nombre = sc.nextLine();

            System.out.print("ID Categoría: ");
            int catId = Integer.parseInt(sc.nextLine());
            String categoria = categoriaCRUD.getNombreCategoriaPorId(catId);

            System.out.print("Precio: ");
            double precio = Double.parseDouble(sc.nextLine());

            System.out.print("Stock: ");
            int stock = Integer.parseInt(sc.nextLine());

            String linea = id + ";" + nombre + ";" + categoria + ";" + precio + ";" + stock;
            inventario.add(linea);
            logs.registro("Producto agregado: " + linea);

        } catch (Exception e) {
            logs.error("Error al agregar producto: " + e.getMessage());
        }
    }


    // Modificar producto existente
    public static void modificarProductoTXT(List<String> inventario) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("ID del producto a modificar: ");
            int id = Integer.parseInt(sc.nextLine());
            boolean encontrado = false;

            for (int i = 0; i < inventario.size(); i++) {
                String[] partes = inventario.get(i).split(";");
                if (Integer.parseInt(partes[0]) == id) {
                    System.out.print("Nuevo nombre: ");
                    partes[1] = sc.nextLine();
                    System.out.print("ID nueva categoría: ");
                    int catId = Integer.parseInt(sc.nextLine());
                    partes[2] = categoriaCRUD.getNombreCategoriaPorId(catId);
                    System.out.print("Nuevo precio: ");
                    partes[3] = sc.nextLine();
                    System.out.print("Nuevo stock: ");
                    partes[4] = sc.nextLine();

                    inventario.set(i, String.join(";", partes));
                    logs.registro("Producto modificado: " + inventario.get(i));
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                logs.warning("No se encontró el producto con ID " + id);
            }
        } catch (Exception e) {
            logs.error("Error al modificar producto: " + e.getMessage());
        }
    }

    // Borrar producto
    public static void borrarProductoTXT(List<String> inventario) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("ID del producto a borrar: ");
            int id = Integer.parseInt(sc.nextLine());
            boolean eliminado = inventario.removeIf(linea -> {
                String[] partes = linea.split(";");
                return Integer.parseInt(partes[0]) == id;
            });
            if (eliminado) {
                logs.registro("Producto con ID " + id + " eliminado del inventario.");
            } else {
                logs.warning("No se encontró el producto con ID " + id + " para eliminar.");
            }
        } catch (Exception e) {
            logs.error("Error al borrar producto: " + e.getMessage());
        }
    }

    // Buscar producto
    public static void buscarProductoTXT(List<String> inventario) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese nombre a buscar: ");
        String nombre = sc.nextLine().toLowerCase();
        boolean encontrado = false;

        for (String linea : inventario) {
            // Compara solo con el nombre del producto en la línea
            String[] partes = linea.split(";");
            if (partes.length > 1 && partes[1].toLowerCase().equals(nombre)) {
                System.out.println(linea);
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No se han encontrado productos con ese nombre.");
        }

        logs.registro("Búsqueda realizada: " + nombre + " | Resultado: " + (encontrado ? "Encontrado" : "No encontrado"));
    }


    // Mostrar historial de cambios
    public static void mostrarHistorialTXT() {
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.contains("[REGISTRO]")) {
                    System.out.println(linea);
                }
            }
            logs.registro("Historial de inventario mostrado correctamente.");
        } catch (IOException e) {
            logs.error("Error al mostrar historial: " + e.getMessage());
        }
    }

}
