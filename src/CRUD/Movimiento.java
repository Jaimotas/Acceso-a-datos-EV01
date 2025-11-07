package CRUD;

import java.sql.Timestamp;

public class Movimiento {
	private int id_producto;
	private boolean tipo ;
	private int cantidad; 
	private Timestamp fechaTimestamp;
	public Movimiento(int id_producto, boolean tipo, int cantidad, Timestamp fechaTimestamp) {
			super();
			this.id_producto = id_producto;
			this.tipo = tipo;
			this.cantidad = cantidad;
			this.fechaTimestamp = fechaTimestamp;
	}
	public int getId_producto() {
		return id_producto;
	}
	public boolean isTipo() {
		return tipo;
	}
	public void setTipo(boolean tipo) {
		this.tipo = tipo;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public Timestamp getFechaTimestamp() {
		return fechaTimestamp;
	}
}
