import java.util.List;
import java.util.Scanner;
import CRUD.Categoria;
import CRUD.CategoriaCRUD;
import CRUD.Producto;
import CRUD.ProductoCRUD;
import CRUD.MovimientoCRUD;
import Logs.FileLogger;
import SQL.CargaCSVs;
import XML.XMLManager;
import Files.ArchivoInventario;

public class App {
    private static Scanner sc = new Scanner(System.in);
    private static CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
    private static ProductoCRUD productoCRUD = new ProductoCRUD();
    private static MovimientoCRUD movimientoCRUD = new MovimientoCRUD();
    private static FileLogger logs = new FileLogger();

    public static void main(String[] args) {
        logs.CrearLogs();
        logs.info("Aplicación iniciada correctamente.");
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            switch (opcion) {
                case 1 -> CargaCSVs.cargarCSVEnBD("src/resources/inventario.csv");
                case 2 -> gestionarCategorias();
                case 3 -> gestionarProductos();
                case 4 -> gestionarMovimientos();
                case 5 -> exportarA_XML();
                case 6 -> importarDesde_XML();
                case 7 -> consultasAvanzadas();
                case 8 -> gestionInventarioArchivos();
                case 0 -> {
                    System.out.println("Saliendo de la aplicación...");
                    logs.info("Aplicación cerrada correctamente.");
                }
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Cargar CSV en base de datos");
        System.out.println("2. Gestionar categorías");
        System.out.println("3. Gestionar productos");
        System.out.println("4. Gestionar Movimientos de Stock");
        System.out.println("5. Exportar inventario a XML");
        System.out.println("6. Importar inventario desde XML");
        System.out.println("7. Consultas avanzadas SQL");
        System.out.println("8. Gestión de inventario en archivos");
        System.out.println("0. Salir");
    }

    // ================== CATEGORÍAS ==================
    private static void gestionarCategorias() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Categorías ---");
            System.out.println("1. Listar categorías");
            System.out.println("2. Agregar categoría");
            System.out.println("3. Editar categoría");
            System.out.println("4. Borrar categoría");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige una opción: ");

            switch (opcion) {
                case 1 -> listarCategorias();
                case 2 -> agregarCategoria();
                case 3 -> editarCategoria();
                case 4 -> borrarCategoria();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);
    }

    // ================== PRODUCTOS ==================
    private static void gestionarProductos() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Productos ---");
            System.out.println("1. Listar productos");
            System.out.println("2. Agregar producto");
            System.out.println("3. Editar producto");
            System.out.println("4. Borrar producto");
            System.out.println("5. Movimiento de stock (entrada/salida)");
            System.out.println("6. Exportar productos con stock bajo a JSON");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige una opción: ");

            switch (opcion) {
                case 1 -> listarProductos();
                case 2 -> agregarProducto();
                case 3 -> editarProducto();
                case 4 -> borrarProducto();
                case 5 -> gestionarMovimientoStock();
                case 6 -> exportarStockBajo();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);
    }

    // ================== MOVIMIENTOS ==================
    private static void gestionarMovimientos() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Movimientos ---");
            System.out.println("1. Movimiento de stock (entrada/salida)");
            System.out.println("2. Carga de Movimientos mediante CSV");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige una opción: ");

            switch (opcion) {
                case 1 -> gestionarMovimientoStock();
                case 2 -> movimientoCRUD.importarMovimientosDesdeCSV("src/resources/movimientos.csv");
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);
    }

    // ================== FUNCIONES DE CATEGORÍAS ==================
    private static void listarCategorias() {
        List<Categoria> categorias = categoriaCRUD.listarCategorias();
        System.out.println("\nListado de Categorías:");
        for (Categoria c : categorias) {
            System.out.println(c.getId() + " - " + c.getNombre());
        }
    }

    private static void agregarCategoria() {
        System.out.print("Nombre de la nueva categoría: ");
        String nombre = sc.nextLine();
        Categoria nueva = new Categoria(0, nombre);
        categoriaCRUD.agregarCategoria(nueva);
        System.out.println("Categoría agregada.");
    }

    private static void editarCategoria() {
        int id = leerEntero("ID de la categoría a editar: ");
        System.out.print("Nuevo nombre: ");
        String nuevoNombre = sc.nextLine();
        categoriaCRUD.editarCategoria(id, nuevoNombre);
        System.out.println("Categoría editada.");
    }

    private static void borrarCategoria() {
        int id = leerEntero("ID de la categoría a borrar: ");
        categoriaCRUD.borrarCategoria(id);
    }

    // ================== FUNCIONES DE PRODUCTOS ==================
    private static void listarProductos() {
        List<Producto> productos = productoCRUD.listarProductos();
        System.out.println("\nListado de Productos:");
        for (Producto p : productos) {
            System.out.println(p.getId() + " - " + p.getNombre() +
                    " | CatID: " + p.getCategoriaId() +
                    " | Stock: " + p.getStock() +
                    " | Precio: " + p.getPrecio());
        }
    }

    private static void agregarProducto() {
        System.out.print("Nombre del producto: ");
        String nombre = sc.nextLine();
        int categoriaId = leerEntero("ID de la categoría: ");
        int stock = leerEntero("Stock inicial: ");
        double precio = leerDouble("Precio: ");

        Producto nuevo = new Producto(0, nombre, categoriaId, stock, precio);
        productoCRUD.agregarProducto(nuevo);
        System.out.println("Producto agregado.");
    }

    private static void editarProducto() {
        int id = leerEntero("ID del producto a editar: ");
        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();
        int categoriaId = leerEntero("Nueva categoría ID: ");
        int stock = leerEntero("Nuevo stock: ");
        double precio = leerDouble("Nuevo precio: ");

        productoCRUD.editarProducto(id, nombre, categoriaId, stock, precio);
        System.out.println("Producto editado.");
    }

    private static void borrarProducto() {
        int id = leerEntero("ID del producto a borrar: ");
        productoCRUD.borrarProducto(id);
        System.out.println("Producto eliminado.");
    }

    private static void gestionarMovimientoStock() {
        int id = leerEntero("ID del producto: ");
        int cantidad = leerEntero("Cantidad: ");
        System.out.print("Tipo de movimiento (1 = Entrada, 2 = Salida): ");
        int tipo = leerEntero("");
        boolean entrada = tipo == 1;
        movimientoCRUD.moverStock(id, cantidad, entrada);
        System.out.println("Movimiento de stock realizado.");
    }

    private static void exportarStockBajo() {
        int limite = leerEntero("Introduce el límite de stock: ");
        productoCRUD.exportarProductosStockBajo(limite);
        System.out.println("Exportación completada. Revisa el archivo JSON.");
    }

    // ================== EXPORTAR / IMPORTAR XML ==================
    private static void exportarA_XML() {
        try {
            XMLManager.exportarInventario();
            System.out.println("Inventario exportado correctamente a XML.");
            logs.info("Inventario exportado a XML correctamente.");
        } catch (Exception e) {
            System.out.println("Error al exportar inventario a XML.");
            logs.error("Error al exportar inventario a XML: " + e.getMessage());
        }
    }

    private static void importarDesde_XML() {
        try {
            XMLManager.importarInventario();
            System.out.println("Inventario restaurado correctamente desde XML.");
            logs.info("Inventario importado desde XML correctamente.");
        } catch (Exception e) {
            System.out.println("Error al importar inventario desde XML.");
            logs.error("Error al importar inventario desde XML: " + e.getMessage());
        }
    }

    // ================== CONSULTAS AVANZADAS ==================
    private static void consultasAvanzadas() {
        int opcion;
        do {
            System.out.println("\n--- Consultas Avanzadas SQL ---");
            System.out.println("1. Top N productos más vendidos");
            System.out.println("2. Valor total de stock por categoría");
            System.out.println("3. Histórico de movimientos por rango de fechas");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige una opción: ");

            switch (opcion) {
                case 1 -> {
                    int n = leerEntero("Introduce N: ");
                    System.out.println("\n Top " + n + " productos más vendidos:");
                    List<String> topProductos = productoCRUD.topNProductosMasVendidos(n);
                    if (topProductos.isEmpty()) System.out.println("No hay datos disponibles.");
                    else topProductos.forEach(System.out::println);
                }
                case 2 -> {
                    System.out.println("\n Valor total del stock por categoría:");
                    List<String> valores = categoriaCRUD.valorTotalStockPorCategoria();
                    if (valores.isEmpty()) System.out.println("No hay categorías o productos registrados.");
                    else valores.forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("Fecha inicio (YYYY-MM-DD): ");
                    String inicio = sc.nextLine();
                    System.out.print("Fecha fin (YYYY-MM-DD): ");
                    String fin = sc.nextLine();
                    System.out.println("\n Histórico de movimientos entre " + inicio + " y " + fin + ":");
                    List<String> movimientos = productoCRUD.historicoMovimientosPorFechas(inicio, fin);
                    if (movimientos.isEmpty()) System.out.println("No se encontraron movimientos en ese rango de fechas.");
                    else movimientos.forEach(System.out::println);
                }
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);
    }

 // ================== GESTIÓN DE INVENTARIO CON ARCHIVOS ==================
    private static void gestionInventarioArchivos() {
        // Al arrancar, exportamos la base de datos a inventario.txt
        List<String> inventario = ArchivoInventario.cargarInventario();
        int opcion;
        do {
        	 System.out.println("\n--- Gestión de Inventario en Archivos ---");
             System.out.println("1. Cargar base de datos a inventario.txt");
             System.out.println("2. Listar inventario");
             System.out.println("3. Agregar producto");
             System.out.println("4. Modificar producto");
             System.out.println("5. Borrar producto");
             System.out.println("6. Buscar producto");
             System.out.println("7. Consultar historial");
             System.out.println("0. Volver al menú principal");
             opcion = leerEntero("Elige una opción: ");

            switch (opcion) {
            	case 1 -> ArchivoInventario.cargarInventarioDeBD();
                case 2 -> ArchivoInventario.listarInventarioTXT(inventario);
                case 3 -> ArchivoInventario.agregarProductoTXT(inventario);
                case 4 -> ArchivoInventario.modificarProductoTXT(inventario);
                case 5 -> ArchivoInventario.borrarProductoTXT(inventario);
                case 6 -> ArchivoInventario.buscarProductoTXT(inventario);
                case 7 -> ArchivoInventario.mostrarHistorialTXT();
                case 8 -> ArchivoInventario.backupInventarioTXT();
                case 0 -> ArchivoInventario.guardarArchivo(inventario);
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 0);
    }
    // ================== UTILIDADES ==================
    private static int leerEntero(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                logs.warning("Entrada inválida, se esperaba un número.");
                System.out.println("Por favor, ingresa un número válido.");
            }
        }
    }

    private static double leerDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                logs.warning("Entrada inválida, se esperaba un número decimal.");
                System.out.println("Por favor, ingresa un número válido.");
            }
        }
    }
}
