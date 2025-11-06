package CRUD;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import Logs.FileLogger;
import SQL.DBconexion;

public class CategoriaCRUD {
	private FileLogger logs = new FileLogger();
	/**
	 * Lista las categorias que recoge desde la base de datos
	 * @return lista con todas las categorias
	 */
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
	/**
	 * Inserta la categoria nueva en la base de datos
	 * @param catg datos de la categoria a insertar
	 */
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
    /**
     * Edita la categoria en base al id
     * @param id
     * @param nuevoNombre
     */
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
    /**
     * Borra la categoria de un id seleccionado
     * @param id
     */
    public void borrarCategoria(int id) {
        String comprobarSql = "SELECT COUNT(*) FROM categorias WHERE id = ?";
        String borrarSql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = DBconexion.getConnection();
             PreparedStatement comprobarPs = conn.prepareStatement(comprobarSql);
             PreparedStatement borrarPs = conn.prepareStatement(borrarSql)) {

            // Verificamos si existe la categoría
            comprobarPs.setInt(1, id);
            try (ResultSet rs = comprobarPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Existe → procedemos a borrar
                    borrarPs.setInt(1, id);
                    borrarPs.executeUpdate();
                    logs.info("Categoría con id = " + id + " borrada correctamente.");
                    System.out.println("Categoría eliminada.");
                } else {
                    logs.warning("No existe ninguna categoría con id = " + id + ". No se realizó el borrado.");
                }
            }

        } catch (SQLException e) {
            logs.error("Error al intentar borrar la categoría con id = " + id + ": " + e.getMessage());
        }
    }

    
}
