package CRUD;
public class Producto {
    private int id;
    private String nombre;
    private int categoriaId;
    private int stock;
    private double precio;

    public Producto(int id, String nombre, int categoriaId, int stock, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.stock = stock;
        this.precio = precio;
    }
    public Producto(String nombre, int categoriaId, int stock, double precio) {
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.stock = stock;
        this.precio = precio;
    }


    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCategoriaId() { return categoriaId; }
    public int getStock() { return stock; }
    public double getPrecio() { return precio; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }
    public void setStock(int stock) { this.stock = stock; }
    public void setPrecio(double precio) { this.precio = precio; }
}
