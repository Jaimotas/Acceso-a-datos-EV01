	package CRUD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import Logs.FileLogger;
import SQL.DBconexion;

public class ProductoCRUD {
    private FileLogger logs = new FileLogger();
	/**
	 * Lista los productos que recoge desde la base de datos
	 * @return lista con todas los productos 
	 */
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY id ASC";
        try (Connection conn = DBconexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("categoria_id"),
                        rs.getInt("stock"),
                        rs.getDouble("precio")
                ));
            }
        } catch (SQLException e) {
            logs.error("Error al cargar la lista de productos: " + e.getMessage());
        }
        logs.info("Lista de productos cargada correctamente");
        return lista;
    }
    /**
	 * Inserta el producto nueva en la base de datos
	 * @param catg datos de el producto a insertar
	 */
    public void agregarProducto(Producto prod) {
        String sql = "INSERT INTO productos(nombre, categoria_id, stock, precio) VALUES(?,?,?,?)";
        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prod.getNombre());
            ps.setInt(2, prod.getCategoriaId());
            ps.setInt(3, prod.getStock());
            ps.setDouble(4, prod.getPrecio());
            ps.executeUpdate();
            logs.info("Producto agregado correctamente");
        } catch (Exception e) {
            logs.error("Error al agregar producto: " + e.getMessage());
        }
    }
    /**
     * Edita el producto en base al id
     * @param id
     * @param nuevoNombre
     */
    public void editarProducto(int id, String nuevoNombre, int nuevaCategoriaId, int nuevoStock, double nuevoPrecio) {
        String sql = "UPDATE productos SET nombre = ?, categoria_id = ?, stock = ?, precio = ? WHERE id = ?";
        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, nuevaCategoriaId);
            ps.setInt(3, nuevoStock);
            ps.setDouble(4, nuevoPrecio);
            ps.setInt(5, id);
            ps.executeUpdate();
            logs.info("Producto editado correctamente");
        } catch (Exception e) {
            logs.error("Error al editar producto con id= " + id + ": " + e.getMessage());
        }
    }
    /**
     * Borra el producto de un id seleccionado
     * @param id
     */
    public void borrarProducto(int id) {
        String sql = "DELETE FROM productos WHERE id = ?";
        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            logs.info("Producto borrado correctamente");
        } catch (Exception e) {
            logs.error("Error al borrar producto con id= " + id + ": " + e.getMessage());
        }
    }
    /**
     * 
     * @param id
     */
    public void moverStock(int id_Producto, int cantidad, boolean entrada) {
        String sqlActualizar = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        String sqlInsertarMovimiento = "INSERT INTO movimientos (id_producto, tipo, cantidad, fecha) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DBconexion.getConnection()) {
            conn.setAutoCommit(false); // Inicia la transacción

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlActualizar);
                 PreparedStatement psInsert = conn.prepareStatement(sqlInsertarMovimiento)) {

                int delta = entrada ? cantidad : -cantidad;
                String tipo = entrada ? "ENTRADA" : "SALIDA";

                psUpdate.setInt(1, delta);
                psUpdate.setInt(2, id_Producto);
                psUpdate.executeUpdate();

                psInsert.setInt(1, id_Producto);
                psInsert.setString(2, tipo);
                psInsert.setInt(3, cantidad);
                psInsert.executeUpdate();

                conn.commit();
                logs.info("Movimiento de stock registrado correctamente: " + tipo + " de " + cantidad+ " producto/s para producto de id= "+ id_Producto);

            } catch (SQLException e) {
                conn.rollback();
                logs.error("Error en movimiento de stock: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logs.error("Error de conexión al mover stock: " + e.getMessage());
        }
    }
    public void exportarProductosStockBajo(int limiteStock) {
        String sql = "SELECT * FROM productos WHERE stock < ?";
        List<Producto> productosBajos = new ArrayList<>();

        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limiteStock);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                productosBajos.add(new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getInt("categoria_id"),
                    rs.getInt("stock"),
                    rs.getDouble("precio")
                ));
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter("productos_stock_bajo.json")) {
                gson.toJson(productosBajos, writer);
            }

            logs.info("Archivo JSON generado correctamente: productos_stock_bajo.json");

        } catch (Exception e) {
            logs.error("Error al exportar productos con stock bajo: " + e.getMessage());
        }
    }
}
