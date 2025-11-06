# Gestor de Inventario - Proyecto Java

Aplicación desarrollada en **Java** que permite gestionar un inventario de **productos** y **categorías**, controlar **movimientos de stock**, registrar operaciones en **logs** y **exportar datos a JSON**.  
Además, cargar los datos iniciales desde un archivo **CSV** ubicado dentro del proyecto.

---

## Requisitos previos

Antes de ejecutar la aplicación asegúrate de tener instalado:

- Java 16 o superior  
- MySQL o XAMPP  
- IDE (Eclipse, IntelliJ, VS Code, etc.)

---


## Base de Datos

La base de datos del proyecto se llama **ad**.  
Contiene las siguientes tablas principales:

- **categorias**: almacena las categorías de productos.  
- **productos**: almacena los productos con su nombre, categoría, stock y precio.  
- **movimientos**: registra las entradas y salidas de stock.
- **inventario**: contiene el inventario completo, sacado del csv

Para cagar la base de datos se encuentra dentro de la carpeta **/resources**, en el archivo **ad.sql**.

---

## Configuración del proyecto

1. Abre el proyecto en tu IDE.  
2. Añade las librerías del directorio `/libs` al **Build Path**:
   - `mysql-connector-j.jar`
   - `gson.jar`
3. Revisa las credenciales en `SQL/DBconexion.java` y asegúrate de que la base de datos se conecta correctamente.  
4. Comprueba que el archivo **inventario.csv** esté en la carpeta `/resources`.

---

## Funciones principales

- **Carga automática desde CSV**: inserta categorías y productos evitando duplicados.  
- **CRUD completo**:
  - Categorías → listar, agregar, editar, borrar.  
  - Productos → listar, agregar, editar, borrar.  
- **Gestión de stock**: controla las entradas y salidas de productos.  
- **Exportación a JSON**: genera un archivo con los productos con stock bajo.  
- **Sistema de logs**: registra todos los eventos y errores en archivos separados.

---

## Ejemplo de uso

### 1- Carga desde CSV

El archivo CSV se encuentra en `/resources/inventario.csv` y contiene los datos iniciales del inventario.  

### 2- Exportación de productos con poco stock

La aplicación permite exportar los productos con stock bajo (limitado por el usuario) a un archivo `productos_stock_bajo.json`, el cual se genera automáticamente en el directorio raíz del proyecto.

---

## Archivos generados automáticamente

| Archivo | Descripción |
|----------|-------------|
| `logs.txt` | Registro de eventos |
| `errorlog.txt` | Registro de errores y excepciones |
| `productos_stock_bajo.json` | Productos con stock bajo |

---

## Autor

Proyecto desarrollado por **Jaime Robles**  
Octubre 2025  
Lenguajes y tecnologías: *Java, MySQL, Gson*  
Organización del código en paquetes: `CRUD`, `SQL`, `Logs`, `resources`, `libs`




