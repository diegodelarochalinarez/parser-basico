package tipos;

public class Ifx extends Statx{
    private Expx s1;
    private Statx s2;
    private Statx s3;
    
    public Ifx(Expx st1, Statx st2, Statx st3) {
        s1 = st1;
        s2 = st2;
        s3 = st3;  
    }
    
    public Object[] getVariables() {
        Object obj[] = new Object[3];
        obj[0] = s1;
        obj[1] = s2;
        obj[2] = s3;
        return obj;
    }
    
}
