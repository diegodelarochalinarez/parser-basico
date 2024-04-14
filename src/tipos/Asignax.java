package tipos;

public class Asignax extends Statx{
    private Idx s1;
    private Expx s2;
    
    public Asignax(Idx st1, Expx st2) {
        s1 = st1;
        s2 = st2;
    }
    
    public String getAsignax() {
        return s1.toString();
    }
    
}
