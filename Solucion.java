public class Solucion implements Cloneable {
    private int[] s;
    private Float evaluacion;
    private float norm;

    public Solucion(int[] s) {
        this.s = s;
        this.evaluacion = oneMax(this.s);
    }

    public Float oneMax(int[] s){
        Float evaluacion = 0f;
        for(int i = 0; i < s.length; i++){
            evaluacion += s[i];
        }
        return evaluacion;
    }

    public Float getEvaluacion() {
        return evaluacion;
    }

    public void setNorm(float valor){
        this.norm = valor;
    }

    public float getNorm() {
        return norm;
    }

    public int[] getS() {
        return s;
    }

    public void evaluar(){
        this.evaluacion = oneMax(this.s);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
