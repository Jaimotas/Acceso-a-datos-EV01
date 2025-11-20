# Proyecto de Gestión de Inventario (Acceso a Datos)

Aplicación desarrollada en **Java** que permite gestionar un inventario,
controlar movimientos de stock y realizar operaciones avanzadas sobre
una base de datos **MySQL**.\
Incluye exportación/importación en **XML**, validación con **XSD**,
carga masiva desde **CSV**, consultas optimizadas, así como un sistema
de gestión local mediante archivo de texto, logs y copias de seguridad.

## Requisitos previos

-   **Java 16** o superior\
-   **MySQL** o **XAMPP**\
-   **IDE Java** (IntelliJ, Eclipse, VS Code...)\
-   Conector MySQL y librerías incluidas en la carpeta `libs/`

## Base de Datos

La base de datos usada es **`ad`** e incluye las tablas:

-   `categorias`
-   `productos`
-   `movimientos`

Se proporciona el script **`ad.sql`** con toda la estructura.

## Funcionalidades del Proyecto

### 1. Gestión de Categorías

-   Crear
-   Listar
-   Editar
-   Borrar

### 2. Gestión de Productos

-   Crear productos\
-   Editar productos\
-   Borrar productos\
-   Listar todos los productos\
-   Búsqueda por nombre\
-   Control de stock (entradas/salidas mediante movimientos)

### 3. Movimientos de Stock

-   Registro de entradas y salidas\
-   Histórico\
-   Consultas por rango de fechas

### 4. Importación desde CSV

-   Carga inicial de categorías, productos y movimientos\
-   Evita duplicados\
-   Importación masiva con rollback en caso de error

### 5. Exportación / Importación XML

-   Exportación a `inventario.xml`\
-   Importación con validación mediante `inventario.xsd`

### 6. Consultas SQL Avanzadas

-   Productos más vendidos\
-   Valor del stock agrupado por categoría\
-   Listado de movimientos filtrado

### 7. Sistema de Logs

`logs.txt` y `errorlog.txt` registran:

-   Info\
-   Advertencias\
-   Errores\
-   Acciones del usuario\
-   Fallos en lectura/escritura de archivos

## Funcionalidades Nuevas (Gestión Local con TXT)

### Archivo `inventario.txt`

Formato por línea:

    id;nombre;categoria;precio;stock

### Funciones incluidas:

✔ Cargar inventario desde el archivo\
✔ Guardar inventario al archivo\
✔ Listar productos\
✔ Agregar producto (ID auto-incremental)\
✔ Modificar producto\
✔ Borrar producto\
✔ Buscar por nombre\
✔ Copia de seguridad automática\
✔ Logs integrados con cada operación

### Sincronización con Base de Datos

El usuario puede elegir pasar **toda la información de la BD al archivo
TXT** mediante una opción del menú.

## Archivos generados automáticamente

  -----------------------------------------------------------------------------------
  Archivo                                       Función
  --------------------------------------------- -------------------------------------
  `inventario.txt`                              Inventario local editable

  `inventario_backup_YYYY-MM-DD_HH-MM-SS.txt`   Copias de seguridad automáticas

  `logs.txt`                                    Registro general

  `errorlog.txt`                                Registro de errores

  `inventario.xml`                              Exportación XML

  `ad.sql`                                      Script de la base de datos
  -----------------------------------------------------------------------------------

## ▶ Ejecución del Proyecto

1.  Importa el proyecto en tu IDE.\
2.  Configura las credenciales en `SQL/DBconexion.java`.\
3.  Asegúrate de tener MySQL/XAMPP funcionando.\
4.  Ejecuta `App.java`.\
5.  Usa el menú para gestionar categorías, productos, movimientos,
    exportación/importación o inventario TXT.

## Autor

Proyecto desarrollado por **Jaime Robles**\
**Noviembre 2025**\
Tecnologías: Java, MySQL, JDBC, XML (JAXB), CSV, File I/O
