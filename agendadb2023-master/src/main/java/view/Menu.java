package view;

import java.util.List;

import dao.AgendaSQL;
import io.IO;
import model.Contacto;

public class Menu {
	
	public static void main(String[] args) {
		AgendaSQL agenda = new AgendaSQL();
//		agenda.drop();
		
		List<String> opciones = List.of( 
				"buscar por Código", 
				"buscar por Nombre", 
				"Mostrar la agenda", 
				"Añadir un contacto",
				"moDificar un contacto",
				"Eliminar un contacto",
				"Salir");
		
		while (true) {
			System.out.println(opciones);
			switch (IO.readString().toUpperCase().charAt(0)) {
			case 'C':
				buscarPorCodigo(agenda);
				break;
			case 'N':
				buscarPorInicioDelNombre(agenda);
				break;
			case 'M':
				mostrar(agenda);
				break;
			case 'A':
				anadirContacto(agenda);
				break;
			case 'D':
				modificarContacto(agenda);
				break;
			case 'E':
				borrarContacto(agenda);
				break;
			case 'S':
				cerrarAgenda(agenda);
				return;
			default:
			}
		}		
		
	}

	private static void cerrarAgenda(AgendaSQL agenda) {
		agenda.close();
	}

	private static void borrarContacto(AgendaSQL agenda) {
		IO.print("Código ? ");
		String id = IO.readString();
		boolean borrado = agenda.delete(id);
		IO.println(borrado ? "Borrado" : "No se ha podido borrar");
	}

	private static void anadirContacto(AgendaSQL agenda) {
		IO.print("Nombre ? ");
		String nombre = IO.readString();
		IO.print("Teléfono ? ");
		String telefono = IO.readString();
		IO.print("Edad ? ");
		int edad = IO.readInt();
		boolean anadido = agenda.add(new Contacto(nombre, telefono, edad));
		IO.println(anadido ? "Añadido" : "No se ha podido añadir");
	}

	private static void modificarContacto(AgendaSQL agenda) {
		IO.print("Código del contacto a modificar ? ");
		String id = IO.readString();
		Contacto contacto = agenda.buscarPorCodigo(id);
		IO.print("Nombre [" + contacto.getNombre() + "] ? ");
		String nombre = IO.readString();
		if (!nombre.isBlank()) {
			contacto.setNombre(nombre);
		}
		IO.print("Teléfono [" + contacto.getTelefono() + "] ? ");
		String telefono = IO.readString();
		if (!telefono.isBlank()) {
			contacto.setTelefono(telefono);
		}
		IO.print("Edad [" + contacto.getEdad() + "] ? ");
		int edad = IO.readInt();
		contacto.setEdad(edad);
		boolean anadido = agenda.update(contacto);
		IO.println(anadido ? "Modificado" : "No se ha podido modificar");		
	}

	private static void mostrar(AgendaSQL agenda) {
		System.out.println(agenda.show());
	}

	private static void buscarPorInicioDelNombre(AgendaSQL agenda) {
		IO.print("El nombre empieza por ? ");
		String inicio = IO.readString();
		List<?> contactos = agenda.buscarPorNombre(inicio);
		IO.println(contactos);
	}

	private static void buscarPorCodigo(AgendaSQL agenda) {
		IO.print("Código ? ");
		String id = IO.readString();
		Contacto contacto = agenda.buscarPorCodigo(id);
		IO.println(contacto);
	}

}

