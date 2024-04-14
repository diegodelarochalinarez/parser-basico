package tipos;

import java.util.Vector;

public class Programax {
    //private tipos.Declarax s1;
    private Declarax s1;

    private Statx s2;
    private Vector s3;
    public Programax(Declarax st1, Statx st2) {
        s1 = st1;
        s2 = st2;
    }

    public Programax(Vector st1, Statx st2){
        s3 = st1;
        s2 = st2;
    }
    public Declarax getDeclaration() {return s1;}
    
    public Statx getStatement() {return s2;}
}
