
package Logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileLogger {

    private final String logFile = "logs.txt";  //ruta para archivo logs
    private final String errorlogFile = "errorlog.txt"; //ruta para archivo logs de errores
    public void CrearLogs() {
        crearArchivo(logFile);
        crearArchivo(errorlogFile);
    }
    /**
     * Función que crea los archivos de los logs, a no ser que ya esten creados
     * @param rutaArchivo ruta del archivo a crear(errorlog/logs)
     */
    private void crearArchivo(String rutaArchivo) {
        File file = new File(rutaArchivo);
        try {
            if (!file.exists()) {
            	file.createNewFile();
                info("Archivo creado: " + rutaArchivo);
            } else {
                String mensaje = "El archivo " + rutaArchivo + " ya existe.";
                warning(mensaje);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo crear el archivo: " + rutaArchivo);
            error("Error al crear archivo " + rutaArchivo + ": " + e.getMessage());
        }
    }
    /**
     * Funcion para escribir mensajes en el log.
     * @param level nivel de mensaje para el log(INFO, WARNING, ERROR, FATAL)
     * @param mensaje 
     * @param file archivo de destino
     */
    private void writeLog(String level, String mensaje, String file) {
        try (FileWriter fw = new FileWriter(file, true)) {
            String logEntry = String.format("[%s] [%s] %s%n",LocalDateTime.now(),level,mensaje);
            fw.write(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Log por defecto para mensajes de informacion
     * @param mensaje
     */
    public void info(String mensaje) {
        writeLog("INFO", mensaje, logFile);
    }
    /**
     * Log por defecto para mensajes de warning
     * @param mensaje
     */
    public void warning(String mensaje) {
        writeLog("WARNING", mensaje, logFile);
    }
    /**
     * Log por defecto para mensajes de error
     * @param mensaje
     */
    public void error(String mensaje) {
        writeLog("ERROR", mensaje, errorlogFile);
    }
    /**
     * Log por defecto para mensajes fatales
     * @param mensaje
     */
    public void fatal(String mensaje) {
        writeLog("FATAL", mensaje, errorlogFile);
    }
    public void registro(String mensaje) {
    	writeLog("REGISTRO", mensaje, logFile);
    }
}
