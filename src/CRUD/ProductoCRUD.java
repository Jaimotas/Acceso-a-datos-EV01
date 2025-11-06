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

    // ================== CRUD BÁSICO ==================
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

    // ================== STOCK Y MOVIMIENTOS ==================
    public void moverStock(int id_Producto, int cantidad, boolean entrada) {
        String sqlActualizar = "UPDATE productos SET stock = stock + ? WHERE id = ?";
        String sqlInsertarMovimiento = "INSERT INTO movimientos (id_producto, tipo, cantidad, fecha) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DBconexion.getConnection()) {
            conn.setAutoCommit(false);

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
                logs.info("Movimiento de stock registrado correctamente: " + tipo + " de " + cantidad +
                          " producto/s para producto de id= " + id_Producto);

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

    // ================== EXPORTAR A JSON ==================
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

    // ================== CONSULTAS AVANZADAS ==================

    /** 🔹 Top N productos más vendidos */
    public List<String> topNProductosMasVendidos(int n) {
        List<String> resultado = new ArrayList<>();
        String sql = """
            SELECT p.nombre, SUM(m.cantidad) AS total_vendido
            FROM productos p
            JOIN movimientos m ON p.id = m.id_producto
            WHERE m.tipo = 'SALIDA'
            GROUP BY p.nombre
            ORDER BY total_vendido DESC
            LIMIT ?;
        """;

        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, n);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.add(rs.getString("nombre") + " - Total vendido: " + rs.getInt("total_vendido"));
            }
            logs.info("Consulta Top " + n + " productos más vendidos ejecutada correctamente.");
        } catch (SQLException e) {
            logs.error("Error al obtener Top N productos: " + e.getMessage());
        }
        return resultado;
    }

    /** 🔹 Histórico de movimientos entre fechas */
    public List<String> historicoMovimientosPorFechas(String fechaInicio, String fechaFin) {
        List<String> resultado = new ArrayList<>();
        String sql = """
            SELECT m.id, p.nombre AS producto, m.tipo, m.cantidad, m.fecha
            FROM movimientos m
            JOIN productos p ON m.id_producto = p.id
            WHERE m.fecha BETWEEN ? AND ?
            ORDER BY m.fecha ASC;
        """;

        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultado.add("[" + rs.getString("fecha") + "] " +
                              rs.getString("producto") + " - " + rs.getString("tipo") +
                              " (" + rs.getInt("cantidad") + ")");
            }
            logs.info("Histórico de movimientos ejecutado correctamente.");
        } catch (SQLException e) {
            logs.error("Error al obtener histórico de movimientos: " + e.getMessage());
        }
        return resultado;
    }
}
