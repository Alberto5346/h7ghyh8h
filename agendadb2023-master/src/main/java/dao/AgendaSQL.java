package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.Contacto;

public class AgendaSQL {

	private Connection conn = null;

	/**
	 * Constructor
	 */
	public AgendaSQL() {
		conn = BD.getConnection();
		createTables();
	}

	/**
	 * Cierra la agenda
	 */
	public void close() {
		BD.close();
	}

	/**
	 * Mostrar la agenda
	 * 
	 * @return cadena con la agenda
	 */
	public String show() {
		String sql = """
				SELECT uuid, nombre, telefono, edad
				FROM agenda
				""";
		try {
			StringBuffer sb = new StringBuffer();
			ResultSet rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				Contacto c = read(rs);
				sb.append(c.toString());
				sb.append("\n");
			}
			return sb.toString();
		} catch (SQLException e) {
		}
		return "";
	}

	/**
	 * Buscar un contacto conociendo su identificador
	 * 
	 * @param codigo
	 * @return null
	 */
	public Contacto buscarPorCodigo(String id) {
		String sql = """
				SELECT uuid, nombre, telefono, edad
				FROM agenda
				WHERE uuid = ?
				""";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return read(rs);
			}
		} catch (SQLException e) {
		}
		return null;
	}

	/**
	 * Buscar contactos conociendo los primeros caracteres de su nombre
	 * 
	 * @param inicio del nombre
	 * @return lista de contactos que cumplen con la condición o null
	 */
	public List<Contacto> buscarPorNombre(String inicio) {
		String sql = """
				SELECT uuid, nombre, telefono, edad
				FROM agenda
				WHERE nombre LIKE ?
				""";
		List<Contacto> contactos = new ArrayList<Contacto>();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, inicio + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				contactos.add(read(rs));
			}
		} catch (SQLException e) {
		}
		return contactos;
	}

	/**
	 * Añade un nuevo contacto a la agenda
	 * 
	 * @param c
	 * @return true si ha sido añadido, false en caso contrario
	 */
	public boolean add(Contacto c) {
		String sql = """
				INSERT INTO agenda (uuid, nombre, telefono, edad)
				VALUES (?, ?, ?, ?)
				""";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, c.getUsuario().toString());
			ps.setString(2, c.getNombre());
			ps.setString(3, c.getTelefono());
			ps.setInt(4, c.getEdad());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
		}
		return false;
	}

	/**
	 * Modificar un contacto de la agenda
	 * 
	 * @param c
	 * @return true si ha sido modificado, false en caso contrario
	 */
	public boolean update(Contacto c) {
		String sql = """
				UPDATE agenda
				SET nombre = ?, telefono = ?, edad = ?
				WHERE uuid = ?
				""";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, c.getNombre());
			ps.setString(2, c.getTelefono());
			ps.setInt(3, c.getEdad());
			ps.setString(4, c.getUsuario().toString());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
		}
		return false;
	}

	/**
	 * Borra un contacto conociendo su identificador
	 * 
	 * @param identificador
	 * @return true si es borrado, false en caso contrario
	 */
	public boolean delete(String id) {
		String sql = """
				DELETE FROM agenda
				WHERE uuid = ?
				""";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
		}
		return false;
	}

	/**
	 * Vacía la agenda
	 */
	public void drop() {
		String sql = """
				DELETE FROM agenda
				""";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
		}
	}

	/**
	 * Leer un contacto <b>Ver en clase</b>
	 * 
	 * @param descriptor de un random access file
	 * @return contacto
	 * 
	 */
	private Contacto read(ResultSet rs) {
		try {
			String sUuid = rs.getString("uuid");
			UUID uuid = UUID.fromString(sUuid);
			String nombre = rs.getString("nombre");
			String telefono = rs.getString("telefono");
			int edad = rs.getInt("edad");
			return new Contacto(uuid, nombre, telefono, edad);
		} catch (SQLException e) {
		}
		return null;
	}

	/**
	 * Crea el esquema de la base de datos
	 * 
	 * @throws SQLException
	 */
	private void createTables() {
		String sql = null;
		if (BD.typeDB.equals("sqlite")) {
			sql = """
						CREATE TABLE IF NOT EXISTS agenda (
							uuid STRING PRIMARY KEY,
							nombre STRING NOT NULL,
							telefono STRING,
							edad INTEGER
						)
					""";
		}
		if (BD.typeDB.equals("mariadb")) {
			sql = """
						CREATE TABLE IF NOT EXISTS agenda (
						  uuid CHAR(36) PRIMARY KEY,
						  nombre VARCHAR(255) NOT NULL,
						  telefono VARCHAR(255) DEFAULT NULL,
						  edad INT DEFAULT NULL,
						  PRIMARY KEY (uuid)
						)
					""";
		}
		try {
			conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
		}
	}

}
