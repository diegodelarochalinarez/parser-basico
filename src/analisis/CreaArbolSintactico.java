package analisis;
import tipos.*;

public class CreaArbolSintactico{

	public static void main(String[] a){

	Programax p = new Programax(new Declarax("x", new Typex("Int")),
			new Ifx(new Comparax(new Idx("a"), new Idx("b")),
				new Asignax(new Idx("c"), new Idx("d")),
				new Asignax(new Idx("e"), new Idx("f"))));
			
    }
}

