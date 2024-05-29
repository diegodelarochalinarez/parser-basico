package analisis;

import javax.swing.JOptionPane;
import tipos.*;
import analisis.*;
import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;

public class Parser {
    // Declaración de variables
    Programax a = null;
    Programax p = null;
    String[] tipo = null;
    String[] variable;
    String byteString;
    private Vector tablaSimbolos = new Vector();
    private boolean correcto = true;
    private final Scanner s;
    final int ifx = 1, thenx = 2, elsex = 3, printx = 6, semi = 7,
            sum = 8, igual = 9, intx = 11, stringx = 12, id = 13, EOf_code = 14;
    private int tknCode, tokenEsperado;
    private String token, tokenActual, log;

    // Sección de bytecode
    private int cntBC = 0;
    private int cntIns = 0;
    private String pilaBC[] = new String[100];
    private String memoriaBC[] = new String[10];
    private String pilaIns[] = new String[50];

    public Parser(String codigo) {
        s = new Scanner(codigo);
        token = s.getToken(true);
        tknCode = stringToCode(token);
        p = P();
        if (tknCode != EOf_code) {
            error(token, "EOF");
        }
    }

    public void advance() {
        if (tknCode != EOf_code) {
            token = s.getToken(true);
            tokenActual = s.getToken(false);
            tknCode = stringToCode(token);
        }
    }

    public void eat(int t) {
        tokenEsperado = t;
        if (tknCode == t) {
            setLog("Token: " + token + "\n" + "Tipo:  " + s.getTipoToken());
            advance();
        } else {
            error(token, "token tipo:" + t);
        }
    }

    public Programax P() {
        Declarax d = D();
        createTable();
        Statx s = S();
        if (tknCode != EOf_code) {
            error(token, "EOF");
        }
        return new Programax(tablaSimbolos, s);
    }

    public Declarax D() {
        //D   -> id D'
        //D'  -> (int | string) ℇ ; D
        //D'  -> ℇ (cadena nula)
        if(tknCode == id) {
            if(stringToCode(s.getToken(false)) == intx || stringToCode(s.getToken(false)) == stringx) {
                String s = token;
                eat(id); Typex t = T(); eat(semi); D();
                tablaSimbolos.addElement(new Declarax(s, t));
                return new Declarax(s, t);
            }
            else{return null;}
        } else if(tknCode != id){
            return null;
        }
        else{
            error(token, "(id)");
            return null;
        }
    }

    public Typex T() {
        if (tknCode == intx) {
            eat(intx);
            return new Typex("int");
        } else if (tknCode == stringx) {
            eat(stringx);
            return new Typex("string");
        } else {
            error(token, "(int / string)");
            return null;
        }
    }

    public Statx S() {
        switch (tknCode) {
            case ifx:
                Expx e1;
                Statx s1, s2;
                eat(ifx);
                e1 = E();
                byteCode("condicion", e1.toString());
                eat(thenx);
                s1 = S();
                eat(elsex);
                s2 = S();
                return new Ifx(e1, s1, s2);
            case id:
                Idx i;
                Expx e;
                String varName = token;
                eat(id);
                i = new Idx(varName);
                declarationCheck(varName);
                eat(igual);
                e = E();
                byteCode("asignacion", varName, e.toString());
                compatibilityCheck(varName, e.toString());
                return new Asignax(i, e);
            case printx:
                Expx ex;
                eat(printx);
                ex = E();
                byteCode("print", tokenActual);
                return new Printx(ex);
            default:
                error(token, "(if | id | print)");
                return null;
        }
    }

    public Expx E() {
        Idx i = null;
        String comp1 = token;
        if (tknCode == id) {
            declarationCheck(token);
            eat(id);
            i = new Idx(comp1);
            return EPrime(i);
        } else {
            error(token, "(id)");
            return null;
        }
    }

    public Expx EPrime(Idx i) {
        if (tknCode == sum) {
            String comp2 = token;
            Idx i2;
            eat(sum);
            declarationCheck(token);
            comp2 = token;
            eat(id);
            i2 = new Idx(comp2);
            compatibilityCheck(i.getIdx(), comp2);
            byteCode("suma", i.getIdx(), comp2);
            return new Sumax(i, i2);
        } else {
            return i;
        }
    }

    public void error(String token, String t) {
        correcto = false;
        switch (JOptionPane.showConfirmDialog(null,
                "Error sintáctico:\n"
                        + "El token:(" + token + ") no concuerda con la gramática del lenguaje,\n"
                        + "se espera: " + t + ".\n"
                        + "¿Desea detener la ejecución?",
                "Ha ocurrido un error",
                JOptionPane.YES_NO_OPTION)) {
            case JOptionPane.NO_OPTION:
                int e = 1;
                break;
            case JOptionPane.YES_OPTION:
                System.exit(0);
                break;
        }
    }

    public int stringToCode(String t) {
        int codigo = 0;
        switch (t) {
            case "if": codigo = 1; break;
            case "then": codigo = 2; break;
            case "else": codigo = 3; break;
            case "print": codigo = 6; break;
            case ";": codigo = 7; break;
            case "+": codigo = 8; break;
            case "=": codigo = 9; break;
            case "int": codigo = 11; break;
            case "string": codigo = 12; break;
            case "EOF": codigo = 14; break;
            default: codigo = 13; break;
        }
        return codigo;
    }

    public void setLog(String l) {
        if (log == null) {
            log = l + "\n \n";
        } else {
            log = log + l + "\n \n";
        }
    }

    public String getLog() {
        return log;
    }

    public void createTable() {
        variable = new String[tablaSimbolos.size()];
        tipo = new String[tablaSimbolos.size()];

        System.out.println("-----------------");
        System.out.println("TABLA DE SÍMBOLOS");
        System.out.println("-----------------");
        for (int i = 0; i < tablaSimbolos.size(); i++) {
            Declarax dx = (Declarax) tablaSimbolos.get(i);
            variable[i] = dx.s1;
            tipo[i] = dx.s2.getTypex();
            System.out.println(variable[i] + ": " + tipo[i]);
            byteCode("igual", variable[i]);
        }

        ArrayUtils.reverse(variable);
        ArrayUtils.reverse(tipo);

        System.out.println("-----------------\n");
    }

    public void declarationCheck(String s) {
        boolean declarada = false;
        for (int i = 0; i < tablaSimbolos.size(); i++) {
            Declarax declaration = (Declarax) tablaSimbolos.elementAt(i);
            if (s.equals(declaration.s1)) {
                declarada = true;
                break;
            }
        }
        if (!declarada && !s.equals(";")) {
            System.out.println("La variable '" + s + "' no ha sido declarada.\nSe detuvo la ejecución.");
            JOptionPane.showMessageDialog(null, "La variable [" + s + "] no ha sido declarada", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void compatibilityCheck(String s1, String s2) {
        Declarax elementoCompara1 = null, elementoCompara2 = null;
        boolean termino = false;
        for (int i = 0; i < tablaSimbolos.size(); i++) {
            elementoCompara1 = (Declarax) tablaSimbolos.elementAt(i);
            if (s1.equals(elementoCompara1.s1)) {
                for (int j = 0; j < tablaSimbolos.size(); j++) {
                    elementoCompara2 = (Declarax) tablaSimbolos.elementAt(j);
                    if (s2.equals(elementoCompara2.s1)) {
                        if (tipo[i].equals(tipo[j])) {
                            termino = true;
                            break;
                        } else {
                            termino = true;
                            JOptionPane.showMessageDialog(null, "Incompatibilidad de tipos: " + elementoCompara1.s1 + " ("
                                            + elementoCompara1.s2.getTypex() + "), " + elementoCompara2.s1 + " ("
                                            + elementoCompara2.s2.getTypex() + ").", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            int e = 1;
                        }
                        break;
                    }
                }
            }
            if (termino) {
                break;
            }
        }
    }

    public void byteCode(String tipo, String s1, String s2) {
        System.out.println(tipo + "," + s1 + "," + s2);
        int pos1 = -1, pos2 = -1;

        for (int i = 0; i < variable.length; i++) {
            if (s1.equals(variable[i])) {
                pos1 = i;
            }
            if (s2.equals(variable[i])) {
                pos2 = i;
            }
        }

        switch (tipo) {
            case "asignacion":
                ipbc(cntIns + ": iload_" + pos1);
                ipbc(cntIns + ": istore_" + pos2);
                break;
            case "suma":
                ipbc(cntIns + ": iload_" + pos1);
                ipbc(cntIns + ": iload_" + pos2);
                ipbc(cntIns + ": iadd");
                break;
        }
    }

    public void byteCode(String tipo, String s1) {
        System.out.println(tipo + "," + s1);
        int pos1 = -1;
        for (int i = 0; i < variable.length; i++) {
            if (s1.equals(variable[i])) {
                pos1 = i;
            }
        }
        switch (tipo) {
            case "condicion":
                ipbc(cntIns + ": iload_" + pos1);
                ipbc(cntIns + ": ifne " + (cntIns + 4));
            break;
            case "igual":
                ipbc(cntIns + ": istore_" + pos1);
                break;
            case "print":
                ipbc(cntIns + ": iprint");
                break;
        }
    }

    public void ipbc(String ins) {
        while (pilaBC[cntBC] != null) {
            cntBC++;
        }
        cntIns++;
        pilaBC[cntBC] = ins;
        cntBC++;
        System.out.println(cntBC);
    }

    public String getBytecode() {
        System.out.println("se ejecuto bytecode");
        String JBC = "";
        for (int i = 0; i < pilaBC.length; i++) {
            if (pilaBC[i] != null) {
                JBC = JBC + pilaBC[i] + "\n";
            }
        }
        return JBC;
    }

    public boolean isCorrecto(){
        return correcto;
    }

    public boolean isCorrectoLexico(){
        return s.isError();
    }
}
