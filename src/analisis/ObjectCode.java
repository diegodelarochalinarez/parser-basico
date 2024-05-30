package analisis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ObjectCode {
    public static void main(String[] args) {
        String inputFileName = "ensambla.txt";
        String outputFileName = "ensambla.obj";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String objectCode = assembleLine(line);
                if (objectCode != null) {
                    writer.write(objectCode);
                    writer.newLine();
                }
            }

            System.out.println("Assembly complete. Output written to " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String assembleLine(String line) {
        // Aquí se realiza el procesamiento de cada línea de ensamblador
        // para generar el código objeto correspondiente. Este es un ejemplo
        // muy simplificado que solo maneja algunas instrucciones básicas.

        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty() || trimmedLine.startsWith(";")) {
            return null; // Línea vacía o comentario
        }

        String[] parts = trimmedLine.split("\\s+");
        String instruction = parts[0].toUpperCase();

        switch (instruction) {
            case "MOV":
                return processMov(parts);
            case "ADD":
                return processAdd(parts);
            // Agregar más instrucciones según sea necesario
            default:
                System.err.println("Instrucción desconocida: " + instruction);
                return null;
        }
    }

    private static String processMov(String[] parts) {
        if (parts.length != 3) {
            System.err.println("Formato incorrecto para MOV: " + String.join(" ", parts));
            return null;
        }
        // Procesar instrucción MOV (ejemplo simplificado)
        String dest = parts[1];
        String src = parts[2];
        return "B8" + src + "89" + dest; // Este es solo un ejemplo, ajustar según sea necesario
    }

    private static String processAdd(String[] parts) {
        if (parts.length != 3) {
            System.err.println("Formato incorrecto para ADD: " + String.join(" ", parts));
            return null;
        }
        // Procesar instrucción ADD (ejemplo simplificado)
        String dest = parts[1];
        String src = parts[2];
        return "03" + dest + src; // Este es solo un ejemplo, ajustar según sea necesario
    }
}
