# Gestor de Inventario - Proyecto Java

Aplicación desarrollada en **Java** que permite gestionar un inventario de **productos** y **categorías**, controlar **movimientos de stock**, registrar operaciones en **logs**, **importar/exportar inventario en XML**, y realizar **consultas SQL avanzadas**.  
Además, carga los datos iniciales desde un archivo **CSV** ubicado dentro del proyecto y permite la **importación masiva de movimientos** desde CSV con control de transacciones.

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
- **inventario**: contiene el inventario completo, cargado desde el CSV.  

Para cargar la base de datos se encuentra dentro de la carpeta **/resources**, en el archivo **ad.sql**.

---

## Configuración del proyecto

1. Abre el proyecto en tu IDE.  
2. Añade las librerías del directorio `/libs` al **Build Path**:
   - `mysql-connector-j.jar`
   - `gson.jar`
   - `jakarta.xml.bind-api.jar`
   - `jakarta.activation.jar`
   - `jaxb-impl.jar`
   - `jaxb-core.jar`
3. Revisa las credenciales en `SQL/DBconexion.java` y asegúrate de que la base de datos se conecta correctamente.  
4. Comprueba que los archivos **inventario.csv** y **movimientos.csv** estén en la carpeta `/resources`.

---

## Funciones principales

- **Carga automática desde CSV**: inserta categorías y productos evitando duplicados.  
- **CRUD completo**:
  - Categorías → listar, agregar, editar, borrar.  
  - Productos → listar, agregar, editar, borrar.  
- **Gestión de stock**: controla las entradas y salidas de productos mediante movimientos.  
- **Exportación e importación XML**:
  - Exporta el inventario completo (categorías, productos y movimientos).  
  - Restaura el inventario desde un archivo XML validado con XSD.  
- **Consultas avanzadas SQL**:
  - Top N productos más vendidos.  
  - Valor total de stock por categoría.  
  - Histórico de movimientos por rango de fechas.  
- **Optimización de consultas**:
  - Uso de `EXPLAIN` y creación de índices para mejorar tiempos de ejecución.  
- **Importación masiva desde CSV**:
  - Permite registrar grandes cantidades de movimientos en lotes.  
  - Si ocurre un error, se realiza **rollback** para mantener la integridad de los datos.  
- **Sistema de logs**:
  - Registro de eventos y errores con timestamp.

---

## Ejemplo de uso

### 1- Carga desde CSV

El archivo CSV se encuentra en `/resources/inventario.csv` y contiene los datos iniciales del inventario.  

### 2- Exportar e importar inventario

- **Exportar**: genera un archivo `inventario.xml` con todos los datos del sistema.  
- **Importar**: lee el XML y actualiza la base de datos.  
  En caso de conflicto (por ejemplo, claves foráneas inexistentes), se registran los errores en el log.

### 3- Consultas avanzadas

- **Top N productos más vendidos**  
- **Valor total de stock por categoría (con dos decimales)**  
- **Histórico de movimientos entre dos fechas**

### 4- Optimizacion con `EXPLAIN`

  La siguiente optimización se realizó sobre la consulta de productos con más ventas (movimientos tipo **SALIDA**).  
  Esta operación implica **JOIN**, **WHERE**, **GROUP BY** y **ORDER BY**, lo que la hace ideal para analizar con `EXPLAIN` e índices compuestos.

---

### 🔍 Consulta original
```sql
SELECT p.id, p.nombre, SUM(m.cantidad) AS total_vendido
FROM productos p
JOIN movimientos m ON p.id = m.id_producto
WHERE m.tipo = 'SALIDA'
GROUP BY p.id, p.nombre
ORDER BY total_vendido DESC
LIMIT 10; 
```
#### Resultado despues del `EXPLAIN`

| id | select_type | table | type | possible_keys | key | key_len | ref | rows | Extra |
|----|--------------|--------|------|----------------|-----|----------|------|-------|----------------------------------------------|
| 1  | SIMPLE       | m      | ALL  | NULL           | NULL | NULL     | NULL | 101 | Using where; Using temporary; Using filesort |
| 1  | SIMPLE       | p      | eq_ref | PRIMARY      | PRIMARY | 4 | ad.m.id_producto | 1 | NULL |

#### El plan de ejecución muestra que MySQL realiza un escaneo completo (ALL) sobre la tabla movimientos, lo que impacta negativamente en el rendimiento.

## Mejora aplicada : creación de indices
Se añadieron los siguientes índices para optimizar los filtros y la relación entre tablas:
```sql
CREATE INDEX idx_tipo ON movimientos(tipo);
CREATE INDEX idx_producto_mov ON movimientos(id_producto);
```

### Resultado despues del `EXPLAIN`
| id | select_type | table | type | possible_keys | key | key_len | ref | rows | Extra |
|----|--------------|--------|------|----------------|-----|----------|------|-------|----------------------------------------------|
| 1  | SIMPLE       | m      | ref  | idx_tipo,idx_producto_mov | idx_tipo | 4 | const | 50 | Using where; Using temporary; Using filesort |
| 1  | SIMPLE       | p      | eq_ref | PRIMARY | PRIMARY | 4 | ad.m.id_producto | 1 | NULL |

## Tiempo de ejecucción
 
| Antes de la optimización: | Después de la optimización: |
|---------------------------|-----------------------------|
|![Texto alternativo](./capturas/Consulta-sin-indices.jpg)|![Texto alternativo](./capturas/Consulta-con-indices.jpg)|

#### La consulta ahora usa el índice idx_tipo y el índice idx_producto_mov lo que reduce drásticamente el número de filas escaneadas, mejorando el tiempo de ejecución. Debido al bajo número de filas de este caso la diferencia de tiempo no es tan notoria, sin embargo, si hablaramos de una tabla con +1000 registros dicha magnitud seria mucho más grande

## Conclusión

  - Antes: la consulta realizaba un escaneo completo de la tabla movimientos (~100 filas).

  - Después: gracias al uso de índices, el número de filas analizadas se reduce notablemente.

  - El tiempo de ejecución pasó de 120 ms a 15 ms en promedio (según MySQL Workbench).

### Resultado: 
  el rendimiento del sistema mejora significativamente en operaciones de análisis de ventas.
---

Los resultados se imprimen directamente en consola y se registran en el log.

---

## Archivos generados automáticamente

| Archivo | Descripción |
|----------|-------------|
| `logs.txt` | Registro de eventos |
| `errorlog.txt` | Registro de errores y excepciones |
| `inventario.xml` | Exportación completa del inventario |
| `movimientos_import.csv` | Ejemplo de CSV para importación masiva de movimientos |

---

## Autor

Proyecto desarrollado por **Jaime Robles**  
Noviembre 2025  
Lenguajes y tecnologías: *Java, MySQL, JAXB*  
Organización del código en paquetes: `CRUD`, `SQL`, `XML`, `Logs`, `resources`, `libs`
