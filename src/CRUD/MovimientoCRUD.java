package CRUD;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

import Logs.FileLogger;
import SQL.DBconexion;

public class MovimientoCRUD {
	private FileLogger logs = new FileLogger();

    public void importarMovimientosDesdeCSV(String rutaCSV) {
        String sql = "INSERT INTO movimientos (id_producto, tipo, cantidad, fecha) VALUES (?, ?, ?, ?)";
        int batchSize = 1000;

        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(rutaCSV))) {

            conn.setAutoCommit(false);
            String linea;
            int contador = 0;
            br.readLine(); // saltar encabezado

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                ps.setInt(1, Integer.parseInt(datos[0]));
                ps.setString(2, datos[1]);
                ps.setInt(3, Integer.parseInt(datos[2]));
                ps.setString(4, datos[3]);
                ps.addBatch();

                if (++contador % batchSize == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
            conn.commit();
            logs.info("Importación masiva completada con éxito. Total filas: " + contador);

        } catch (SQLException e) {
            logs.fatal("Error SQL durante la importación: " + e.getMessage());
            try (Connection conn = DBconexion.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                logs.fatal("Error al hacer rollback: " + ex.getMessage());
            }
        } catch (Exception e) {
            logs.fatal("Error general durante la importación: " + e.getMessage());
        }
       System.out.println("Carga de CSV completa");
    }
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
}
