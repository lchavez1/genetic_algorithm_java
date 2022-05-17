import java.util.*;

public class GA {

    Random numAleatorio = new Random();

    public GA(int maxIt, int pob, int dim) throws CloneNotSupportedException {

        LinkedList<Solucion> list = new LinkedList();

        int t = 0;

        //Aqui genero las soluciones inciales
        for (int i = 0; i < pob; i++){
            int[] array = new int[dim];
            for(int j = 0; j < dim; j++){
                double d = numAleatorio.nextDouble();
                if(d < 0.5){
                    array[j] = 1;
                } else {
                    array[j] = 0;
                }
            }
            list.add(new Solucion(array));
        }

        System.out.println("Poblacion inicial:");
        for (int i = 0; i < list.size(); i++){
            System.out.println("[" + i + "]" + Arrays.toString(list.get(i).getS()) + " evaluacion = " + list.get(i).getEvaluacion());
        }
        System.out.println();

        //seleccion y cruza funcionan bien hasta ahora
        while(true){

            t++;

            if(t >= maxIt){
                System.out.println("Poblacion despues del proceso de evolucion:");
                for (int i = 0; i < list.size(); i++){
                    System.out.println("[" + i + "] " + Arrays.toString(list.get(i).getS()) + " evaluacion = " + list.get(i).getEvaluacion());
                }
                break;
            }
            LinkedList<int[]> indexes = selectionOperator(list);

            LinkedList<Solucion> hijos = crossoverOperator(indexes, list);

            LinkedList<Solucion> hijosMutation = mutationOperator(hijos);

            evaluacion(hijosMutation);

            list = elitism(list, hijosMutation);
        }
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        GA ga = new GA(100, 10, 4);
    }

    public LinkedList<int[]> selectionOperator(LinkedList<Solucion> S){
        float sum = 0;
        float sumNorm = 0;

        for(int i = 0; i < S.size(); i++){
            sum += S.get(i).getEvaluacion();
        }

        float valor = 0;
        for(int i = 0; i < S.size(); i++){
            float norm = (float) S.get(i).getEvaluacion() / sum;
            valor += norm;
            S.get(i).setNorm(norm);
        }

        LinkedList<int[]> list = new LinkedList<>();
        for(int i = 0; i < S.size()/2; i++){
            sumNorm = 0;
            int[] indexes = new int[2];
            //elegir padre 1
            double r = numAleatorio.nextDouble();
            for(int j = 0; j < S.size(); j++){
                //System.out.println("Valor de norm: " + S.get(i).getNorm());
                sumNorm += S.get(j).getNorm();
                //System.out.println("Valor de normSUm: " + sumNorm);
                if(sumNorm > r){
                    //aqui guardo el padre
                    indexes[0] = j;
                    break;
                }
            }

            //elegir padre 2
            sumNorm = 0;
            r = numAleatorio.nextDouble();
            for(int j = 0; j < S.size(); j++){
                sumNorm += S.get(j).getNorm();
                if(sumNorm > r){
                    //aqui guardo el padre
                    indexes[1] = j;
                    break;
                }

            }
            list.add(indexes);
        }
        return list;
    }

    public LinkedList<Solucion> crossoverOperator(LinkedList<int[]> indexes, LinkedList<Solucion> solucions) throws CloneNotSupportedException {
        LinkedList<Solucion> s = new LinkedList<>();
        for(int i = 0; i < solucions.size(); i++){
            s.add(new Solucion(solucions.get(i).getS().clone()));
        }
        int corte = (int)(Math.random()*(s.getFirst().getS().length)+0);
        for(int i = 0; i < indexes.size(); i++){
            for(int j = corte; j < s.getFirst().getS().length; j++){
                int aux = s.get(indexes.get(i)[0]).getS()[j];
                s.get(indexes.get(i)[0]).getS()[j] = s.get(indexes.get(i)[1]).getS()[j];
                s.get(indexes.get(i)[1]).getS()[j] = aux;
            }
        }
        return s;
    }

    public LinkedList<Solucion> mutationOperator(LinkedList<Solucion> list){
        LinkedList<Solucion> hijosMutation = new LinkedList<>();
        for(int i = 0; i < list.size(); i++){
            hijosMutation.add(new Solucion(list.get(i).getS().clone()));
        }
        for(int i = 0; i < hijosMutation.size(); i++){
            int n = (int)(Math.random()*(hijosMutation.getFirst().getS().length)+0);
            if(hijosMutation.get(i).getS()[n] == 0){
                hijosMutation.get(i).getS()[n] = 1;
            } else {
                hijosMutation.get(i).getS()[n] = 0;
            }
        }
        return hijosMutation;

    }

    public LinkedList<Solucion> evaluacion(LinkedList<Solucion> list){
        for(int i = 0; i < list.size(); i++){
            list.get(i).evaluar();
        }
        return list;
    }

    public LinkedList<Solucion> elitism(LinkedList<Solucion> padres, LinkedList<Solucion> hijos) throws CloneNotSupportedException {
        LinkedList<Solucion> listPadres = new LinkedList<>();
        for(int i = 0; i < padres.size(); i++){
            listPadres.add(new Solucion(padres.get(i).getS().clone()));
        }
        Collections.sort(listPadres, new Comparator<Solucion>() {
            @Override
            public int compare(Solucion o1, Solucion o2) {
                return o2.getEvaluacion().compareTo(o1.getEvaluacion());
            }
        });

        LinkedList<Solucion> listHijos = new LinkedList<>();
        for(int i = 0; i < hijos.size(); i++){
            listHijos.add(new Solucion(hijos.get(i).getS().clone()));
        }
        Collections.sort(listHijos, new Comparator<Solucion>() {
            @Override
            public int compare(Solucion o1, Solucion o2) {
                return o2.getEvaluacion().compareTo(o1.getEvaluacion());
            }
        });

        LinkedList<Solucion> nuevaGeneracion = new LinkedList<>();
        for(int i = 0; i < listPadres.size()/2; i++){
            nuevaGeneracion.add(new Solucion(listPadres.get(i).getS().clone()));
        }
        for(int i = 0; i < listHijos.size()/2; i++){
            nuevaGeneracion.add(new Solucion(listHijos.get(i).getS().clone()));
        }

        return nuevaGeneracion;
    }

}
