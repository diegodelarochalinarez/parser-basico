package analisis;

import javax.swing.JOptionPane;
import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;
import tipos.*;

public class Parser {
    //Declaración de variables----------------
    Programax p = null;
    String[] tipo = null;
    String[] variable;
    String byteString;
    private Vector tablaSimbolos = new Vector();
    private final Scanner s;
    final int ifx=1, thenx=2, elsex=3, printx=4, semi=5,
            sum=6, asignacion=7, intx=8, stringx=9, id=10, endx = 11;
    private int tknCode, tokenEsperado;
    private String token, tokenActual, log;
    
    //Sección de bytecode
    private int cntBC = 0; // Contador de lineas para el código bytecode
    private String bc; // String temporal de bytecode
    private int jmp1, jmp2, jmp3;
    private int aux1, aux2, aux3;
    private String pilaBC[] = new String[100];
    private String memoriaBC[] = new String[10];
    private String pilaIns[] = new String [50];
    private int retornos[]= new int[10];
    private int cntIns = 0;
    private boolean correcto = true;
    private Programax arbolAbstractoSintaxis;
    //---------------------------------------------
    
    public Parser(String codigo) {  
        s = new Scanner(codigo);
        token = s.getToken(true);
        tknCode = stringToCode(token);
        p = P();
    }
    
    
    //INICIO DE ANÁLISIS SINTÁCTICO
    public void advance() {
        token = s.getToken(true);
        tokenActual = s.getToken(false);
        tknCode = stringToCode(token);
    }
    
    public void eat(int t) {
        tokenEsperado = t;
        if(tknCode == t) {
            setLog("Token: " + token + "\n" + "Tipo:  "+ s.getTipoToken());
            advance();
        }
        else{
            error(token, "token tipo:"+t);
        }
    }

    public boolean isCorrectoLexico(){
        return s.isError();
    }
   /*
    public Programax P() {
        Declarax d = D();
        createTable();
        Statx s = S();

        return new Programax(tablaSimbolos,s);
    }
    */
    public Programax P() {
        Vector<Declarax> declarations = new Vector<>();
        Declarax d;
        while ((d = D()) != null) {
            declarations.add(new Declarax(d.s1, d.s2));
        }
        createTable();
        Statx s = S();
        arbolAbstractoSintaxis = new Programax(declarations, s);
        return arbolAbstractoSintaxis;
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
        if(tknCode == intx) {
            eat(intx);
            return new Typex("int");
        }
        else if(tknCode == stringx) {
            eat(stringx);
            return new Typex("string");
        }
        else{
            error(token, "(int / string)");
            return null;
        }
    }
    
    public Statx S() { //return statement
        //S   -> if E then S else S
        //S   -> id = E
        //S   -> print E
        switch(tknCode) {
            case ifx:
                Expx e1;
                Statx s1, s2;
                eat(ifx);
                e1= E();
                eat(thenx);
                s1=S();
                eat(elsex);
                s2=S();                
                return new Ifx(e1, s1, s2);

            case id:
                Idx i;
                Expx e;
                eat(id);   i=new Idx(tokenActual);  declarationCheck(tokenActual); byteCode("igual", tokenActual);  eat(asignacion);   e=E();
                return new Asignax(i, e);
                
            case printx:
                Expx ex;
                eat(printx);    ex=E();
                return new Printx(ex);
                
            default: error(token, "(if | id | print)");
                return null;
        }
    }
    
    public void L() {
        switch(tknCode) {
            case endx:
                eat(endx);
                break;

            case semi:
                eat(semi);      S();    L();
                break;
            default: error(token, "(end | ;)");
        }
    }
    public Expx Ep(){
        //E'  -> + id E'
        //E'  -> ℇ (cadena nula)

        Idx i1, i2;
        String comp1, comp2;

        comp1 = token;
        //declarationCheck(token);
        //eat(id);
        i1 = new Idx(token);

        if(tknCode==sum){
            comp2 = tokenActual;
            eat(sum);   eat(id);
            i2 = new Idx(comp2); //(tokenActual)
            declarationCheck(comp2);
            compatibilityCheck(comp1,comp2);
            byteCode("suma", comp1, comp2);
            System.out.println("Operación: " + comp1 + "+" + comp2);
            return new Sumax(i1, i2);
        }

        return null;
    }
    public Expx E() {
        //E   -> id E'
       Idx i1, i2;
       String comp1, comp2;
       
       if(tknCode == id) {
           comp1 = token;
           declarationCheck(token);
           eat(id); 
           i1 = new Idx(token); 
           Ep();
           return i1;
       } else{
           error(token, "(id)");
           return null;
       }
    } //FIN DEL ANÁLISIS SINTÁCTICO
    
    
    
    public void error(String token, String t) {
        correcto = false;
        switch(JOptionPane.showConfirmDialog(null,
                "Error sintáctico:\n"
                        + "El token:("+ token + ") no concuerda con la gramática del lenguaje,\n"
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

        switch(t) {
            case "if": codigo=1; break;    
            case "then": codigo=2; break;
            case "else": codigo=3; break;
            case "print": codigo=4; break;
            case ";": codigo=5; break;
            case "+": codigo=6; break;
            case "=": codigo=7; break;
            case "int": codigo=8; break;
            case "string": codigo=9; break;
            case "end": codigo=11; break;
            default: codigo=10; break;
        }
        return codigo;
    }
    
    //Métodos para recoger la información de los tokens para luego mostrarla
    public void setLog(String l) {
        if(log == null) {
            log = l + "\n \n";
        }
        else{
            log=log + l + "\n \n";
        }      
    }
    public boolean isCorrecto(){
        return correcto;
    }
    public String getLog() {
        return log;
    }
    //-----------------------------------------------
    
    //Recorrido de la parte izquierda del árbol y creación de la tabla de símbolos
    public void createTable() {
        //String[] aux1 = new String[tablaSimbolos.size()];
        //String[] aux2 = new String[tablaSimbolos.size()];
        variable = new String[tablaSimbolos.size()];
        tipo = new String[tablaSimbolos.size()];
        
        //Imprime tabla de símbolos
        System.out.println("-----------------");
        System.out.println("TABLA DE SÍMBOLOS");
        System.out.println("-----------------");
        for(int i=0; i<tablaSimbolos.size(); i++) {
            Declarax dx;
            Typex tx;
            dx = (Declarax)tablaSimbolos.get(i);
            variable[i] = dx.s1;
            tipo[i] = dx.s2.getTypex();                   
            System.out.println(variable[i] + ": "+ tipo[i]); //Imprime tabla de símbolos por consola.
        }
        
        ArrayUtils.reverse(variable);
        ArrayUtils.reverse(tipo);
        
        System.out.println("-----------------\n");
    }
    
    
    //Verifica las declaraciones de las variables consultando la tabla de símbolos
    public void declarationCheck(String s) {
        boolean valido = false;
        for (int i=0; i<tablaSimbolos.size(); i++) {
            if(s.equals(variable[i])) {
                valido = true;
                break;
            }
        }
        if(!valido) {
            System.out.println("La varible "+ s +  " no está declarada.\nSe detuvo la ejecución.");
             javax.swing.JOptionPane.showMessageDialog(null, "La varible [" + s + "] no está declarada", "Error",
                   javax.swing.JOptionPane.ERROR_MESSAGE);
             int e = 1;
        }
    }
    
    //Chequeo de tipos consultando la tabla de símbolos
    public void compatibilityCheck(String s1, String s2) {
        Declarax elementoCompara1;
        Declarax elementoCompara2;
        System.out.println("CHECANDO COMPATIBILIDAD ENTRE TIPOS ("+s1+", "+s2+"). ");
        boolean termino = false;
        for(int i=0; i<tablaSimbolos.size() ; i++) {
          elementoCompara1 = (Declarax) tablaSimbolos.elementAt(i);
          if(s1.equals(elementoCompara1.s1)) {
            System.out.println("Se encontró el primer elemento en la tabla de símbolos...");
            for(int j=0; j<tablaSimbolos.size() ; j++) {
              elementoCompara2 = (Declarax) tablaSimbolos.elementAt(j);
              if(s2.equals(elementoCompara2.s1)) {
                System.out.println("Se encontró el segundo elemento en la tabla de símbolos...");
                if(tipo[i].equals(tipo[j])) {
                  termino = true;
                  break;
                }else{
                  termino = true;
                    javax.swing.JOptionPane.showMessageDialog(null, "Incompatibilidad de tipos: "+ elementoCompara1.s1 +" ("
                      + elementoCompara1.s2.getTypex() + "), "+elementoCompara2.s1 +" (" + elementoCompara2.s2.getTypex()
                      +").", "Error",
                      javax.swing.JOptionPane.ERROR_MESSAGE);
                    int e = 1;
                }
                break;
              }
            }
          }
          if(termino) {
            break;
          }
        }
    }
    
    public void byteCode(String tipo, String s1,String s2){
        int pos1=-1, pos2=-1;
        
        for(int i=0; i<variable.length; i++) {
            if(s1.equals(variable[i])) {
                pos1 = i;
            }
            if(s2.equals(variable[i])) {
                pos2 = i;
            }
        }
        
        switch(tipo) {
          case "igualdad":
            ipbc(cntIns + ": iload_"+pos1);
            ipbc(cntIns + ": iload_"+pos2);
            ipbc(cntIns + ": ifne " + (cntIns+4));
            jmp1 = cntBC;
          break;

          case "suma":
            ipbc(cntIns + ": iload_"+pos1);
            ipbc(cntIns + ": iload_"+pos2);
            ipbc(cntIns + ": iadd");
            jmp2 = cntBC;
          break;
        }
    }
    
    public void byteCode(String tipo, String s1) {
        int pos1 = -1;
        for(int i=0; i<variable.length; i++) {
            if(s1.equals(variable[i])) {
                pos1 = i;
            }
        }
        switch(tipo) {
            case "igual":
                pilaBC[cntBC+3] = cntIns+4 + ": istore_" + pos1;
                cntIns++;
                jmp2 = cntBC;
            break;
        }
    }
    
    public void ipbc(String ins) {
        while(pilaBC[cntBC] != null) {
            cntBC++;
        }
        cntIns++;
        pilaBC[cntBC] = ins;
        cntBC++;
    }
    
    public String getBytecode() {
        String JBC = "";
        for(int i=0; i<pilaBC.length; i++) {
            if(pilaBC[i] != null){
                JBC = JBC + pilaBC[i] + "\n";
            }
        }
        return JBC;
    }    
}

