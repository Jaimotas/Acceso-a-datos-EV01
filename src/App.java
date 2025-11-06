


import java.util.List;
import java.util.Scanner;
import CRUD.Categoria;
import CRUD.CategoriaCRUD;
import CRUD.Producto;
import CRUD.ProductoCRUD;
import Logs.FileLogger;
import SQL.CargaCSVs;

public class App {
    private static Scanner sc = new Scanner(System.in);
    private static CategoriaCRUD categoriaCRUD = new CategoriaCRUD();
    private static ProductoCRUD productoCRUD = new ProductoCRUD();
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
        System.out.println("0. Salir");
    }

    // ================== CATEGORÍAS ==================
    /**
     * Muestra el menu con las opciones y gestiona las categorias
     */
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
    /**
     * Muestra el menu con las opciones y gestiona los productos 
     */
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

    // ================== NUEVAS FUNCIONES ==================
    /**
     * Pide al usuario los datos del producto a cambiar el stock
     */
    private static void gestionarMovimientoStock() {
        int id = leerEntero("ID del producto: ");
        int cantidad = leerEntero("Cantidad: ");
        System.out.print("Tipo de movimiento (1 = Entrada, 2 = Salida): ");
        int tipo = leerEntero("");

        boolean entrada = tipo == 1;
        productoCRUD.moverStock(id, cantidad, entrada);
        System.out.println("Movimiento de stock realizado.");
    }
    /**
     * Pide al usuario el limite de stock para exportarlo al json
     */
    private static void exportarStockBajo() {
        int limite = leerEntero("Introduce el límite de stock: ");
        productoCRUD.exportarProductosStockBajo(limite);
        System.out.println("Exportación completada. Revisa el archivo JSON.");
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
