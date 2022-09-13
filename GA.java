import java.util.*;
import java.util.stream.Collectors;

// Genetic Algorithm
public class GA {

    // Instance of Random class to generate random numbers
    Random randomNum = new Random();

    // Constructor of Genetic Algorithm, here we receive 3 parameters
    // maxIt = max number of iterations
    // pob = population
    // dim = size of solution dimension
    public GA(int maxIt, int pob, int dim) throws CloneNotSupportedException {

        // On this list we are going to add solutions
        LinkedList<Solution> list = new LinkedList<>();

        // On this variable t, we will save
        int t = 0;

        // There we create the initial population of solutions
        for (int i = 0; i < pob; i++){
            int[] array = new int[dim];
            for(int j = 0; j < dim; j++){
                double d = randomNum.nextDouble();
                if(d < 0.5){
                    array[j] = 1;
                } else {
                    array[j] = 0;
                }
            }
            list.add(new Solution(array));
        }

        System.out.println("Initial population:");
        list.forEach(solution -> System.out.println(Arrays.toString(solution.getS()) + ", evaluation = " + solution.getEvaluacion()));
        System.out.println();

        while(true){

            // Increase t by 1
            t++;

            // This case execute when t is greater than max number of iterations, at final
            if(t >= maxIt){
                System.out.println("Population after evolution process:");
                list.forEach(solution -> System.out.println(Arrays.toString(solution.getS()) + ", evaluation = " + solution.getEvaluacion()));
                // Break to close while loop
                break;
            }
            // This list "indexes" contains the index of
            // every solution selected in the selection operator
            LinkedList<int[]> indexes = selectionOperator(list);

            // This list "children" contains the result
            // after applied crossover operator
            LinkedList<Solution> hijos = crossoverOperator(indexes, list);

            // This list "mutation" contains the result of mutation operator
            LinkedList<Solution> hijosMutation = mutationOperator(hijos);

            // Reevaluate the new solutions
            evaluacion(hijosMutation);

            // Apply elitism to have the better solutions of list
            // and mutation in just one list
            list = elitism(list, hijosMutation);
        }
    }

    // Main method to run the algorithm
    public static void main(String[] args) throws CloneNotSupportedException {
        // Instance of GA with 100 iterations, size of population = 10 and 4 dimensions
        GA ga = new GA(100, 10, 4);
    }

    // This operator "selection" take care of get the indexes
    // to form pairs of parents
    public LinkedList<int[]> selectionOperator(LinkedList<Solution> S){
        // Here we have 2 values
        // first sum = sum of the evaluation of all the solutions
        // second sumNorm =  sum of the normalization of all the solutions
        float sum = 0, sumNorm = 0;

        // Compute the value of sum, this value is calculated with
        // the sum of the evaluation of all the solutions
        // On this case I get every value of getSolution and sum using the function
        // sum of stream core
        sum = (float) S.stream().mapToDouble(Solution::getEvaluacion)
                .sum();

        // Compute the value "normalization" for each solution in the population
        // finalSum is just for copy the value of sum and can use in the lambda expression
        float finalSum = sum;
        // To normalize a value I get the evaluation and divide it by the finalSum
        S.forEach(solution -> solution.setNorm(solution.getEvaluacion() / finalSum));

        // Instance a new list to add the indexes in pairs
        // for example [1, 0] -> [index_first_parent, index_second_parent]
        LinkedList<int[]> list = new LinkedList<>();

        for(int i = 0; i < S.size()/2; i++){

            // Array to add the pair of parents (index)
            int[] indexes = new int[2];

            // I need a random value to get the parents
            double r = randomNum.nextDouble();

            // Choose the first parent
            indexes[0] = getParent(S, r);

            // Choose the second parent
            // First I need to get another random value
            r = randomNum.nextDouble();
            indexes[1] = getParent(S, r);

            // Add the pair of parents to the list
            list.add(indexes);
        }

        // Return the complete list of parents
        return list;
    }

    // This operator "crossover" is responsible for making a cross
    // between solutions to obtain new solutions
    // To do that the operator use the parent indexes
    // that we have in the list "indexes"
    public LinkedList<Solution> crossoverOperator(LinkedList<int[]> indexes, LinkedList<Solution> solutions) throws CloneNotSupportedException {
        // New list to add new cross solutions
        LinkedList<Solution> s = new LinkedList<>();

        // Here I make copies of each solution to avoid ghost copies,
        // before this the code made me a mess with those copies,
        // that I mention is because solution is an array
        solutions.forEach(solution -> s.add(new Solution(solution.getS())));

        // Here we get an index from dimension of solution
        // for example I have the solution [1, 0, 0, 1]
        // I could get the index 2 and from that index start the crossing process
        int corte = (int)(Math.random()*(s.getFirst().getS().length));

        // For each pair of parents we cross starting in the dimension "corte",
        // more specifically we exchange the values of
        // the solutions with each other to form a cross
        indexes.forEach(parents -> {
            for(int j = corte; j < s.getFirst().getS().length; j++){
                int aux = s.get(parents[0]).getS()[j];

                // On the next 2 lines is where we do the exchange
                s.get(parents[0]).getS()[j] = s.get(parents[1]).getS()[j];
                s.get(parents[1]).getS()[j] = aux;
            }
        });

        // Return the crossover population
        return s;
    }

    public LinkedList<Solution> mutationOperator(LinkedList<Solution> list){
        LinkedList<Solution> hijosMutation = new LinkedList<>();
        for(int i = 0; i < list.size(); i++){
            hijosMutation.add(new Solution(list.get(i).getS().clone()));
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

    public LinkedList<Solution> evaluacion(LinkedList<Solution> list){
        for(int i = 0; i < list.size(); i++){
            list.get(i).evaluar();
        }
        return list;
    }

    public LinkedList<Solution> elitism(LinkedList<Solution> padres, LinkedList<Solution> hijos) throws CloneNotSupportedException {
        LinkedList<Solution> listPadres = new LinkedList<>();
        for(int i = 0; i < padres.size(); i++){
            listPadres.add(new Solution(padres.get(i).getS().clone()));
        }
        Collections.sort(listPadres, new Comparator<Solution>() {
            @Override
            public int compare(Solution o1, Solution o2) {
                return o2.getEvaluacion().compareTo(o1.getEvaluacion());
            }
        });

        LinkedList<Solution> listHijos = new LinkedList<>();
        for(int i = 0; i < hijos.size(); i++){
            listHijos.add(new Solution(hijos.get(i).getS().clone()));
        }
        Collections.sort(listHijos, new Comparator<Solution>() {
            @Override
            public int compare(Solution o1, Solution o2) {
                return o2.getEvaluacion().compareTo(o1.getEvaluacion());
            }
        });

        LinkedList<Solution> nuevaGeneracion = new LinkedList<>();
        for(int i = 0; i < listPadres.size()/2; i++){
            nuevaGeneracion.add(new Solution(listPadres.get(i).getS().clone()));
        }
        for(int i = 0; i < listHijos.size()/2; i++){
            nuevaGeneracion.add(new Solution(listHijos.get(i).getS().clone()));
        }

        return nuevaGeneracion;
    }

    // Method to get the index of a parent
    public int getParent(LinkedList<Solution> s, double random) {
        // I compute the sum of normalization
        // At the final compare with that random value and
        // if the "sumNorm" is grater than "random" we choose
        // the index to be a parent

        // "sumNorm" is a variable that have the value of the sum of every normalization,
        float sumNorm = 0;

        // Index is an auxiliary that will have the parent index at the final
        int index = -1;

        for(Solution solution : s) {
            index++;
            sumNorm += solution.getNorm();
            if(sumNorm > random){
                // Close the loop to have the correct index
                break;
            }
        }
        // Return the parent index
        return index;
    }

}

class Solution implements Cloneable {
    private int[] s;
    private Float evaluacion;
    private float norm;

    public Solution(int[] s) {
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

