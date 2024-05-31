package analisis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObjectCode {

    private static LabelTable labelTable = new LabelTable();

    public static void main(String[] args) {
        String inputFileName = "ensambla.txt";
        String outputFileName = "ensambla.obj";

        // Primera pasada: construir la tabla de símbolos con direcciones
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            int address = 0;
            while ((line = reader.readLine()) != null) {
                System.out.println(String.format("%04X", address) + "\t " + line);
                address = labelLine(line, address);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Segunda pasada: ensamblar el código
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

    private static int labelLine(String line, int address) {
        String trimmedLine = line.trim();
        int newAddress;

        if (trimmedLine.isEmpty() || trimmedLine.startsWith(";")) {
            newAddress = address; // Línea vacía o comentario
        }

        String[] parts = trimmedLine.split("\\s+");

        if (parts[0].endsWith(":")) {
            String label = parts[0].substring(0, parts[0].length() - 1);
            labelTable.addLabel(label, address);
            if (parts.length > 1) newAddress = getAddress(address, trimmedLine);
            else newAddress =  address;
        }

        newAddress = getAddress(address, trimmedLine);
        return newAddress;
    }

    private static String assembleLine(String line) {
        // Aquí se realiza el procesamiento de cada línea de ensamblador
        // para generar el código objeto correspondiente. Este es un ejemplo
        // muy simplificado que solo maneja algunas instrucciones básicas.

        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty() || trimmedLine.startsWith(";")) {
            return null; // Línea vacía o comentario
        }

        String[] parts = getParts(trimmedLine);
        String instruction = parts[0].toUpperCase();

        if (instruction.equals("START") || instruction.equals("END")) {
            return null; // No se genera código objeto para START y END
        }

        switch (instruction) {
            case "ADD":
                return processADD(parts);
            case "ADDF":
                return processADDF(parts);
            case "ADDR":
                return processADDR(parts);
            case "AND":
                return processAND(parts);
            case "CLEAR":
                return processCLEAR(parts);
            case "COMP":
                return processCOMP(parts);
            case "COMPF":
                return processCOMPF(parts);
            case "COMPR":
                return processCOMPR(parts);
            case "DIV":
                return processDIV(parts);
            case "DIVF":
                return processDIVF(parts);
            case "DIVR":
                return processDIVR(parts);
            case "FIX":
                return processFIX(parts);
            case "FLOAT":
                return processFLOAT(parts);
            case "HIO":
                return processHIO(parts);
            case "J":
                return processJ(parts);
            case "JEQ":
                return processJEQ(parts);
            case "JGT":
                return processJGT(parts);
            case "JLT":
                return processJLT(parts);
            case "JSUB":
                return processJSUB(parts);
            case "LDA":
                return processLDA(parts);
            case "LDB":
                return processLDB(parts);
            case "LDCH":
                return processLDCH(parts);
            case "LDF":
                return processLDF(parts);
            case "LDL":
                return processLDL(parts);
            case "LDS":
                return processLDS(parts);
            case "LDT":
                return processLDT(parts);
            case "LDX":
                return processLDX(parts);
            case "LPS":
                return processLPS(parts);
            case "MUL":
                return processMUL(parts);
            case "MULF":
                return processMULF(parts);
            case "MULR":
                return processMULR(parts);
            case "NORM":
                return processNORM(parts);
            case "OR":
                return processOR(parts);
            case "RD":
                return processRD(parts);
            case "RMO":
                return processRMO(parts);
            case "RSUB":
                return processRSUB(parts);
            case "SHIFTL":
                return processSHIFTL(parts);
            case "SHIFTR":
                return processSHIFTR(parts);
            case "SIO":
                return processSIO(parts);
            case "SSK":
                return processSSK(parts);
            case "STA":
                return processSTA(parts);
            case "STB":
                return processSTB(parts);
            case "STCH":
                return processSTCH(parts);
            case "STF":
                return processSTF(parts);
            case "STI":
                return processSTI(parts);
            case "STL":
                return processSTL(parts);
            case "STS":
                return processSTS(parts);
            case "STSW":
                return processSTSW(parts);
            case "STT":
                return processSTT(parts);
            case "STX":
                return processSTX(parts);
            case "SUB":
                return processSUB(parts);
            case "SUBF":
                return processSUBF(parts);
            case "SUBR":
                return processSUBR(parts);
            case "SVC":
                return processSVC(parts);
            case "TD":
                return processTD(parts);
            case "TIO":
                return processTIO(parts);
            case "TIX":
                return processTIX(parts);
            case "TIXR":
                return processTIXR(parts);
            case "WD":
                return processWD(parts);
            case "BYTE":
                return processBYTE(parts);
            case "RESB":
                return processRESB(parts);
            case "RESW":
                return processRESW(parts);
            case "WORD":
                return processWORD(parts);
            default:
                System.err.println("Instrucción desconocida: " + instruction);
                return null;
        }
    }

    private static String[] getParts(String line) {
        if (line.isEmpty()) return new String[0];
        String[] parts = line.split("\\s+");
        String instruction = parts[0].toUpperCase();

        if (labelTable.containsLabel(instruction.substring(0, instruction.length() - 1))) {
            instruction = parts[1].toUpperCase();
            // Eliminar etiqueta de la instrucción
            String[] newParts = new String[parts.length - 1];
            System.arraycopy(parts, 1, newParts, 0, parts.length - 1);
            parts = newParts;
        }

        return parts;
    }

    private static int getAddress(int actualAddress, String line) {
        if (line.isEmpty()) return actualAddress;

        String[] parts = getParts(line);
        String instruction = parts[0].toUpperCase();
        if (instruction.equals("START") || instruction.equals("END")) {
            return actualAddress;
        }
        int aument = 0;

        switch (instruction) {
            case "ADD":
                aument = 3;
                break;
            case "ADDF":
                aument = 3;
                break;
            case "ADDR":
                aument = 2;
                break;
            case "AND":
                aument = 3;
                break;
            case "CLEAR":
                aument = 2;
                break;
            case "COMP":
                aument = 3;
                break;
            case "COMPF":
                aument = 3;
                break;
            case "COMPR":
                aument = 2;
                break;
            case "DIV":
                aument = 3;
                break;
            case "DIVF":
                aument = 3;
                break;
            case "DIVR":
                aument = 2;
                break;
            case "FIX":
                aument = 1;
                break;
            case "FLOAT":
                aument = 1;
                break;
            case "HIO":
                aument = 1;
                break;
            case "J":
                aument = 3;
                break;
            case "JEQ":
                aument = 3;
                break;
            case "JGT":
                aument = 3;
                break;
            case "JLT":
                aument = 3;
                break;
            case "JSUB":
                aument = 3;
                break;
            case "LDA":
                aument = 3;
                break;
            case "LDB":
                aument = 3;
                break;
            case "LDCH":
                aument = 3;
                break;
            case "LDF":
                aument = 3;
                break;
            case "LDL":
                aument = 3;
                break;
            case "LDS":
                aument = 3;
                break;
            case "LDT":
                aument = 3;
                break;
            case "LDX":
                aument = 3;
                break;
            case "LPS":
                aument = 3;
                break;
            case "MUL":
                aument = 3;
                break;
            case "MULF":
                aument = 3;
                break;
            case "MULR":
                aument = 2;
                break;
            case "NORM":
                aument = 1;
                break;
            case "OR":
                aument = 3;
                break;
            case "RD":
                aument = 3;
                break;
            case "RMO":
                aument = 2;
                break;
            case "RSUB":
                aument = 3;
                break;
            case "SHIFTL":
                aument = 2;
                break;
            case "SHIFTR":
                aument = 2;
                break;
            case "SIO":
                aument = 1;
                break;
            case "SSK":
                aument = 3;
                break;
            case "STA":
                aument = 3;
                break;
            case "STB":
                aument = 3;
                break;
            case "STCH":
                aument = 3;
                break;
            case "STF":
                aument = 3;
                break;
            case "STI":
                aument = 3;
                break;
            case "STL":
                aument = 3;
                break;
            case "STS":
                aument = 3;
                break;
            case "STSW":
                aument = 3;
                break;
            case "STT":
                aument = 3;
                break;
            case "STX":
                aument = 3;
                break;
            case "SUB":
                aument = 3;
                break;
            case "SUBF":
                aument = 3;
                break;
            case "SUBR":
                aument = 2;
                break;
            case "SVC":
                aument = 2;
                break;
            case "TD":
                aument = 3;
                break;
            case "TIO":
                aument = 1;
                break;
            case "TIX":
                aument = 3;
                break;
            case "TIXR":
                aument = 2;
                break;
            case "WD":
                aument = 3;
                break;
            case "BYTE":
                aument = 1;
                break;
            case "RESB":
                if (incorrectFormat("RESB", 2, parts)) aument = 0;
                aument = Integer.parseInt(parts[1]);
                break;
            case "RESW":
                if (incorrectFormat("RESW", 2, parts)) aument = 0;
                aument = 3 * Integer.parseInt(parts[1]);
                break;
            case "WORD":
                if (incorrectFormat("WORD", 2, parts)) aument = 0;
                aument = 3;
                break;
            default:
                aument = 0;
                break;
        }

        return actualAddress + aument;
    }

    //
    // Métodos para procesar instrucciones específicas
    //
    protected static String processADD(String[] parts) {
        if (incorrectFormat("ADD", 2, parts)) return null;
        String[] r = parts[1].split(",");
        int rf = 0;
        for (String s : r) {
            if (labelTable.containsLabel(s)) {
                rf = labelTable.getAddress(s);
            } else {
                rf = s.charAt(0);
            }
        }
        return "14" + String.format("%04X", rf);
    }

    protected static String processADDF(String[] parts) {
        if (incorrectFormat("ADDF", 2, parts)) return null;
        String[] r = parts[1].split(",");
        int rf = 0;
        for (String s : r) {
            if (labelTable.containsLabel(s)) {
                rf = labelTable.getAddress(s);
            } else {
                rf = s.charAt(0);
            }
        }
        return "58" + String.format("%04X", rf);
    }

    protected static String processADDR(String[] parts) {
        if (incorrectFormat("ADDR", 2, parts)) return null;
        String[] r = parts[1].split(",");
        return "90" + r[0] + r[1];
    }

    protected static String processAND(String[] parts) {
        if (incorrectFormat("AND", 3, parts)) return null;
        String[] c = parts[1].split(",");
        int cf = 0;
        for (String s : c) {
            if (labelTable.containsLabel(s)) {
                cf = labelTable.getAddress(s);
            } else {
                cf = s.charAt(0);
            }
        }
        return "40" + String.format("%04X", cf);
    }

    protected static String processCLEAR(String[] parts) {
        if (incorrectFormat("CLEAR", 2, parts)) return null;
        String r = parts[1];
        int rf = 0;
        if(labelTable.containsLabel(r)) {
            rf = labelTable.getAddress(r);
        } else {
            rf = r.charAt(0);
        }
        return "B4" + String.format("%04X", rf);
    }

    protected static String processCOMP(String[] parts) {
        if (incorrectFormat("COMP", 2, parts)) return null;
        String[] r = parts[1].split(",");
        int cf = 0;
        for (String s : r) {
            if(labelTable.containsLabel(s)) {
                cf = labelTable.getAddress(s);
            } else {
                cf = s.charAt(0);
            }
        }
        return "28" + String.format("%04X", cf);
    }

    protected static String processCOMPF(String[] parts) {
        if (incorrectFormat("COMPF", 3, parts)) return null;
        String[] r = parts[1].split(",");
        int cf = 0;
        for (String s : r) {
            if (labelTable.containsLabel(s)) {
                cf = labelTable.getAddress(s);
            } else {
                cf = s.charAt(0);
            }
        }
        return "88" + String.format("%04X", cf);
    }

    protected static String processCOMPR(String[] parts) {
        if (incorrectFormat("COMPR", 2, parts)) return null;
        String[] c = parts[1].split(",");
        return "A0" + c[0] + c[1];
    }

    protected static String processDIV(String[] parts) {
        if (incorrectFormat("DIV", 3, parts)) return null;
        String[] d = parts[1].split(",");
        int df = 0;
        for (String s : d) {
            if (labelTable.containsLabel(s)) {
                df = labelTable.getAddress(s);
            } else {
                df = s.charAt(0);
            }
        }
        return "24" + String.format("%04X", df);
    }

    protected static String processDIVF(String[] parts) {
        if (incorrectFormat("DIVF", 3, parts)) return null;
        String[] d = parts[1].split(",");
        int df = 0;
        for (String s : d) {
            if (labelTable.containsLabel(s)) {
                df = labelTable.getAddress(s);
            } else {
                df = s.charAt(0);
            }
        }
        return "64" + String.format("%04X", df);
    }

    protected static String processDIVR(String[] parts) {
        if (incorrectFormat("DIVR", 2, parts)) return null;
        String[] d = parts[1].split(",");
        return "9C" + d[0] + d[1];
    }

    protected static String processFIX(String[] parts) {
        if (incorrectFormat("FIX", 1, parts)) return null;
        return "C4";
    }

    protected static String processFLOAT(String[] parts) {
        if (incorrectFormat("FLOAT", 1, parts)) return null;
        return "C0";
    }

    protected static String processHIO(String[] parts) {
        if (incorrectFormat("HIO", 1, parts)) return null;
        return "F4";
    }

    protected static String processJ(String[] parts) {
        if (incorrectFormat("J", 2, parts)) return null;
        String addr = labelTable.containsLabel(parts[1]) ? String.format("%04X", labelTable.getAddress(parts[1])) : parts[1];
        return "3C" + addr;
    }

    protected static String processJEQ(String[] parts) {
        if(incorrectFormat("JEQ", 2, parts)) return null;
        String addr = labelTable.containsLabel(parts[1]) ? String.format("%04X", labelTable.getAddress(parts[1])) : parts[1];
        return "30" + addr;
    }

    protected static String processJGT(String[] parts) {
        if (incorrectFormat("JGT", 2, parts)) return null;
        String addr = labelTable.containsLabel(parts[1]) ? String.format("%04X", labelTable.getAddress(parts[1])) : parts[1];
        return "34" + addr;
    }

    protected static String processJLT(String[] parts) {
        if (incorrectFormat("JLT", 2, parts)) return null;
        String addr = labelTable.containsLabel(parts[1]) ? String.format("%04X", labelTable.getAddress(parts[1])) : parts[1];
        return "38" + addr;
    }

    protected static String processJSUB(String[] parts) {
        if (incorrectFormat("JSUB", 2, parts)) return null;
        String addr = labelTable.containsLabel(parts[1]) ? String.format("%04X", labelTable.getAddress(parts[1])) : parts[1];
        return "48" + addr;
    }

    protected static String processLDA(String[] parts) {
        if (incorrectFormat("LDA", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "00" + String.format("%04X", lf);
    }

    protected static String processLDB(String[] parts) {
        if (incorrectFormat("LDB", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "68" + String.format("%04X", lf);
    }

    protected static String processLDCH(String[] parts) {
        if (incorrectFormat("LDCH", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf += labelTable.getAddress(s);
            } else {
                lf += s.charAt(0);
            }
        }
        return "50" + String.format("%04X", lf);
    }

    protected static String processLDF(String[] parts) {
        if (incorrectFormat("LDF", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "70" + String.format("%04X", lf);
    }

    protected static String processLDL(String[] parts) {
        if (incorrectFormat("LDL", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "08" + String.format("%04X", lf);
    }

    protected static String processLDS(String[] parts) {
        if (incorrectFormat("LDS", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "6C" + String.format("%04X", lf);
    }

    protected static String processLDT(String[] parts) {
        if (incorrectFormat("LDT", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "74" + String.format("%04X", lf);
    }

    protected static String processLDX(String[] parts) {
        if (incorrectFormat("LDX", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "04" + String.format("%04X", lf);
    }

    protected static String processLPS(String[] parts) {
        if (incorrectFormat("LPS", 2, parts)) return null;
        String[] l = parts[1].split(",");
        int lf = 0;
        for (String s : l) {
            if (labelTable.containsLabel(s)) {
                lf = labelTable.getAddress(s);
            } else {
                lf = s.charAt(0);
            }
        }
        return "D0" + String.format("%04X", lf);
    }

    protected static String processMUL(String[] parts) {
        if (incorrectFormat("MUL", 2, parts)) return null;
        String[] m = parts[1].split(",");
        int mf = 0;
        for (String s : m) {
            if (labelTable.containsLabel(s)) {
                mf = labelTable.getAddress(s);
            } else {
                mf = s.charAt(0);
            }
        }
        return "20" + String.format("%04X", mf);
    }

    protected static String processMULF(String[] parts) {
        if (incorrectFormat("MULF", 2, parts)) return null;
        String[] m = parts[1].split(",");
        int mf = 0;
        for (String s : m) {
            if (labelTable.containsLabel(s)) {
                mf = labelTable.getAddress(s);
            } else {
                mf = s.charAt(0);
            }
        }
        return "60" + String.format("%04X", mf);
    }

    protected static String processMULR(String[] parts) {
        if (incorrectFormat("MULR", 2, parts)) return null;
        String[] m = parts[1].split(",");
        return "98" + m[0] + m[1];
    }

    protected static String processNORM(String[] parts) {
        if (incorrectFormat("NORM", 1, parts)) return null;
        return "C8";
    }

    protected static String processOR(String[] parts) {
        if (incorrectFormat("OR", 2, parts)) return null;
        String[] o = parts[1].split(",");
        int of = 0;
        for (String s : o) {
            if (labelTable.containsLabel(s)) {
                of = labelTable.getAddress(s);
            } else {
                of = s.charAt(0);
            }
        }
        return "44" + String.format("%04X", of);
    }

    protected static String processRD(String[] parts) {
        if (incorrectFormat("RD", 2, parts)) return null;
        String[] r = parts[1].split(",");
        int rf = 0;
        for (String s : r) {
            if (labelTable.containsLabel(s)) {
                rf = labelTable.getAddress(s);
            } else {
                rf = s.charAt(0);
            }
        }
        return "D8" + String.format("%04X", rf);
    }

    protected static String processRMO(String[] parts) {
        if (incorrectFormat("RMO", 2, parts)) return null;
        String[] r = parts[1].split(",");
        return "AC" + r[0] + r[1];
    }

    protected static String processRSUB(String[] parts) {
        if (incorrectFormat("RSUB", 1, parts)) return null;
        return "4C0000";
    }

    protected static String processSHIFTL(String[] parts) {
        if (incorrectFormat("SHIFTL", 2, parts)) return null;
        String[] s = parts[1].split(",");
        return "A4" + s[0] + s[1];
    }

    protected static String processSHIFTR(String[] parts) {
        if (incorrectFormat("SHIFTR", 2, parts)) return null;
        String[] s = parts[1].split(",");
        return "A8" + s[0] + s[1];
    }

    protected static String processSIO(String[] parts) {
        if (incorrectFormat("SIO", 1, parts)) return null;
        return "F0";
    }

    protected static String processSSK(String[] parts) {
        if (incorrectFormat("SSK", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "EC" + String.format("%04X", sf);
    }

    protected static String processSTA(String[] parts) {
        if (incorrectFormat("STA", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "0C" + String.format("%04X", sf);
    }

    protected static String processSTB(String[] parts) {
        if (incorrectFormat("STB", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "78" + String.format("%04X", sf);
    }

    protected static String processSTCH(String[] parts) {
        if (incorrectFormat("STCH", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            sf += Integer.parseInt(labelTable.containsLabel(s1) ? labelTable.getAddress(s1).toString() : Integer.toHexString(s1.charAt(0)));
        }
        return "54" + String.format("%04X", sf);
    }

    protected static String processSTF(String[] parts) {
        if (incorrectFormat("STF", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "80" + String.format("%04X", sf);
    }

    protected static String processSTI(String[] parts) {
        if (incorrectFormat("STI", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "D4" + String.format("%04X", sf);
    }

    protected static String processSTL(String[] parts) {
        if (incorrectFormat("STL", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "14" + String.format("%04X", sf);
    }

    protected static String processSTS(String[] parts) {
        if (incorrectFormat("STS", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "7C" + String.format("%04X", sf);
    }

    protected static String processSTSW(String[] parts) {
        if (incorrectFormat("STSW", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "E8" + String.format("%04X", sf);
    }

    protected static String processSTT(String[] parts) {
        if (incorrectFormat("STT", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "84" + String.format("%04X", sf);
    }

    protected static String processSTX(String[] parts) {
        if (incorrectFormat("STX", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "10" + String.format("%04X", sf);
    }

    protected static String processSUB(String[] parts) {
        if (incorrectFormat("SUB", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "1C" + String.format("%04X", sf);
    }

    protected static String processSUBF(String[] parts) {
        if (incorrectFormat("SUBF", 2, parts)) return null;
        String[] s = parts[1].split(",");
        int sf = 0;
        for (String s1 : s) {
            if (labelTable.containsLabel(s1)) {
                sf = labelTable.getAddress(s1);
            } else {
                sf = s1.charAt(0);
            }
        }
        return "5C" + String.format("%04X", sf);
    }

    protected static String processSUBR(String[] parts) {
        if (incorrectFormat("SUBR", 2, parts)) return null;
        String[] s = parts[1].split(",");
        return "94" + s[0] + s[1];
    }

    protected static String processSVC(String[] parts) {
        if (incorrectFormat("SVC", 2, parts)) return null;
        String n = parts[1];
        return "B0" + n;
    }

    protected static String processTD(String[] parts) {
        if (incorrectFormat("TD", 2, parts)) return null;
        String[] t = parts[1].split(",");
        int tf = 0;
        for (String s : t) {
            if (labelTable.containsLabel(s)) {
                tf = labelTable.getAddress(s);
            } else {
                tf = s.charAt(0);
            }
        }
        return "E0" + String.format("%04X", tf);
    }

    protected static String processTIO(String[] parts) {
        if (incorrectFormat("TIO", 1, parts)) return null;
        return "F8";
    }

    protected static String processTIX(String[] parts) {
        if (incorrectFormat("TIX", 2, parts)) return null;
        String[] t = parts[1].split(",");
        int tf = 0;
        for (String s : t) {
            if (labelTable.containsLabel(s)) {
                tf = labelTable.getAddress(s);
            } else {
                tf = s.charAt(0);
            }
        }
        return "2C" + String.format("%04X", tf);
    }

    protected static String processTIXR(String[] parts) {
        if (incorrectFormat("TIXR", 2, parts)) return null;
        String r = parts[1];
        int rf;
        if (labelTable.containsLabel(r)) {
            rf = labelTable.getAddress(r);
        } else {
            rf = r.charAt(0);
        }
        return "B8" + String.format("%04X", rf);
    }

    protected static String processWD(String[] parts) {
        if (incorrectFormat("WD", 2, parts)) return null;
        String[] w = parts[1].split(",");
        String wf = "";
        for (String s : w) {
            wf += (labelTable.containsLabel(s) ? labelTable.getAddress(s) : s);
        }
        return "DC" + wf;
    }

    protected static String processBYTE(String[] parts) {
        if (incorrectFormat("BYTE", 2, parts)) return null;
        Character type = parts[1].charAt(0);
        switch (type){
            case 'C':
                String s = parts[1].substring(2, parts[1].length() - 1);
                StringBuilder hex = new StringBuilder();
                for (char c : s.toCharArray()) {
                    hex.append(String.format("%02X", (int) c));
                }
                return hex.toString();
            case 'X':
                return parts[1].substring(2, parts[1].length() - 1);
            case 'B':
                int decimal = Integer.parseInt(parts[1].substring(2, parts[1].length() - 1), 2);
                String hexBin = Integer.toHexString(decimal);
                return hexBin.toUpperCase();
            default:
                System.err.println("Tipo de BYTE desconocido: " + type);
                return null;
        }
    }

    protected static String processRESB(String[] parts) {
        if (incorrectFormat("RESB", 2, parts)) return null;
        return String.format("%04X", Integer.parseInt(parts[1]));
    }

    protected static String processRESW(String[] parts) {
        if (incorrectFormat("RESW", 2, parts)) return null;
        return String.format("%02X", Integer.parseInt(parts[1]) * 3);
    }

    protected static String processWORD(String[] parts) {
        if (incorrectFormat("WORD", 2, parts)) return null;
        String r = parts[1];
        return "B8" + r;
    }

    protected static boolean incorrectFormat(String instruction, int size, String[] parts) {
        if (parts.length != size) {
            System.err.println("Formato incorrecto para " + instruction + ": " + String.join(" ", parts));
            return true;
        }
        return false;
    }

}

class LabelTable {
    private Map<String, Integer> labels;

    public LabelTable() {
        labels = new HashMap<>();
    }

    public void addLabel(String label, int address) {
        labels.put(label, address);
    }

    public Integer getAddress(String label) {
        return labels.get(label);
    }

    public boolean containsLabel(String label) {
        return labels.containsKey(label);
    }
}
