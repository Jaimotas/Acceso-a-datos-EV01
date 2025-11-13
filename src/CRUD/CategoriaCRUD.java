package CRUD;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import Logs.FileLogger;
import SQL.DBconexion;

public class CategoriaCRUD {
	private FileLogger logs = new FileLogger();

	public List<Categoria> listarCategorias() {
		List<Categoria> lista = new ArrayList<>();
	 	String sql = "SELECT * FROM categorias ORDER BY id ASC";
		try (Connection conn = DBconexion.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				lista.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
			}
		} catch (SQLException e) {
			logs.error("Error al cargar la lista con las categorias: "+e.getMessage());
		}
		logs.info("Listado de categorias cargado");
		return lista;
	}

    public void agregarCategoria(Categoria catg) {
        String sql = "INSERT INTO categorias(nombre) VALUES(?)";
        try (Connection conn = DBconexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            	ps.setString(1, catg.getNombre());
            	ps.executeUpdate(); 
            	logs.info("Nueva categoria agregada correctamente");
        }
        catch(Exception e) {
            logs.error("Error al cargar categoria "); 
        }
    }

    public void editarCategoria(int id, String nuevoNombre) {
        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";
        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, id);
            ps.executeUpdate();
            logs.info("Categoria con id= "+id+" editada correctamente");
        }
        catch(Exception e) {
        	logs.error("Error al editar categoria con id= "+id+ e.getMessage());
        }
    }

    public void borrarCategoria(int id) {
        String comprobarSql = "SELECT COUNT(*) FROM categorias WHERE id = ?";
        String borrarSql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = DBconexion.getConnection();
             PreparedStatement comprobarPs = conn.prepareStatement(comprobarSql);
             PreparedStatement borrarPs = conn.prepareStatement(borrarSql)) {

            comprobarPs.setInt(1, id);
            try (ResultSet rs = comprobarPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    borrarPs.setInt(1, id);
                    borrarPs.executeUpdate();
                    logs.info("Categoría con id = " + id + " borrada correctamente.");
                } else {
                    logs.warning("No existe ninguna categoría con id = " + id + ". No se realizó el borrado.");
                }
            }

        } catch (SQLException e) {
            logs.error("Error al intentar borrar la categoría con id = " + id + ": " + e.getMessage());
        }
    }

    // ✅ NUEVA CONSULTA: Valor total de stock por categoría
    public List<String> valorTotalStockPorCategoria() {
        List<String> resultado = new ArrayList<>();
        String sql = """
            SELECT c.nombre AS categoria, SUM(p.stock * p.precio) AS valor_total
            FROM productos p
            JOIN categorias c ON p.categoria_id = c.id
            GROUP BY c.nombre
            ORDER BY valor_total DESC;
        """;

        try (Connection conn = DBconexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String categoria = rs.getString("categoria");
                double valorTotal = rs.getDouble("valor_total");
                String valorFormateado = String.format("%.2f", valorTotal);
                resultado.add(categoria + " - Valor total: " + valorFormateado + " €");
            }

            logs.info("Consulta de valor total de stock por categoría ejecutada correctamente.");
        } catch (SQLException e) {
            logs.error("Error al calcular el valor total de stock por categoría: " + e.getMessage());
        }

        return resultado;
    }
    public String getNombreCategoriaPorId(int id) {
        String sql = "SELECT nombre FROM categorias WHERE id = ?";
        String nombre = "Desconocida"; // Valor por defecto si no se encuentra
        try (Connection conn = DBconexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombre = rs.getString("nombre");
                } else {
                    logs.warning("No se encontró ninguna categoría con ID = " + id);
                }
            }
        } catch (SQLException e) {
            logs.error("Error al obtener el nombre de la categoría con ID = " + id + ": " + e.getMessage());
        }
        return nombre;
    
    }
}
