import java.util.*;

// Genetic Algorithm to solve One Max problem (optimization)
public class GA {

    // Instance of Random class to generate random numbers
    Random randomNum = new Random();

    // Constructor of Genetic Algorithm, here we receive 3 parameters
    // maxIt = max number of iterations
    // pob = population
    // dim = size of solution dimension
    public GA(int maxIt, int pob, int dim) {

        // On this list we are going to add solutions
        LinkedList<Solution> population = new LinkedList<>();

        // I will save the number of iteration (season)
        int season = 0;

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
            population.add(new Solution(array));
        }

        System.out.println("Initial population:\n");
        population.forEach(solution -> System.out.println(Arrays.toString(solution.getS()) + ", evaluation = " + solution.getEvaluation()));
        System.out.println();

        // Infinite loop, I'll stop it when necessary
        while(true){

            // Increment the season by 1
            season++;

            // This case execute when t is greater than max number of iterations, at final
            if(season >= maxIt){
                System.out.println("Population after evolution process:\n");
                population.forEach(solution -> System.out.println(Arrays.toString(solution.getS()) + ", evaluation = " + solution.getEvaluation()));
                // Break to close while loop
                break;
            }
            // This list "indexes" contains the index of
            // every solution selected in the selection operator
            LinkedList<int[]> indexes = selectionOperator(population);

            // This list "children" contains the result
            // after applied crossover operator
            LinkedList<Solution> children = crossoverOperator(indexes, population);

            // This list "mutation" contains the result of mutation operator
            LinkedList<Solution> mutated = mutationOperator(children);

            // Reevaluate the new solutions
            evaluation(mutated);

            // Apply elitism to have the better solutions of list
            // and mutation in just one list
            population = elitism(population, mutated);
        }
    }

    // Main method to run the algorithm
    public static void main(String[] args) {
        // Instance of GA with 100 iterations, size of population = 10 and 4 dimensions
        GA ga = new GA(100, 10, 4);
    }

    // This operator "selection" take care of get the indexes
    // to form pairs of parents
    public LinkedList<int[]> selectionOperator(LinkedList<Solution> S){

        // Compute the value of sum, this value is calculated with
        // the sum of the evaluation of all the solutions
        // On this case I get every value of getSolution and sum using the function
        // sum of stream core
        float sum = (float) S.stream().mapToDouble(Solution::getEvaluation)
                .sum();

        // Compute the value "normalization" for each solution in the population
        // To normalize a value I get the evaluation and divide it by the sum
        S.forEach(solution -> solution.setNorm(solution.getEvaluation() / sum));

        // Instance a new list to add the indexes in pairs
        // for example [1, 0] -> [index_first_parent, index_second_parent]
        LinkedList<int[]> parents = new LinkedList<>();

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
            parents.add(indexes);
        }

        // Return the complete list of parents
        return parents;
    }

    // This operator "crossover" is responsible for making a cross
    // between solutions to obtain new solutions
    // To do that the operator use the parent indexes
    // that we have in the list "indexes"
    public LinkedList<Solution> crossoverOperator(LinkedList<int[]> indexes, LinkedList<Solution> solutions) {
        // New list to add new cross solutions
        LinkedList<Solution> s = new LinkedList<>();

        // Here I make copies of each solution to avoid ghost copies,
        // before this the code made me a mess with those copies,
        // that I mention is because solution is an array
        solutions.forEach(solution -> s.add(new Solution(solution.getS().clone())));

        // Here we get an index from dimension of solution
        // for example I have the solution [1, 0, 0, 1]
        // I could get the index 2 and from that index start the crossing process
        int section = (int)(Math.random()*(s.getFirst().getS().length));

        // For each pair of parents we cross starting in the dimension "section",
        // more specifically we exchange the values of
        // the solutions with each other to form a cross
        indexes.forEach(parents -> {
            for(int j = section; j < s.getFirst().getS().length; j++){
                int aux = s.get(parents[0]).getS()[j];

                // On the next 2 lines is where we do the exchange
                s.get(parents[0]).getS()[j] = s.get(parents[1]).getS()[j];
                s.get(parents[1]).getS()[j] = aux;
            }
        });

        // Return the crossover population
        return s;
    }

    // This operator "mutation" is responsible for making mutations in code
    // on this case I modify the binary values, changing between 0 and 1
    public LinkedList<Solution> mutationOperator(LinkedList<Solution> population){
        // New list to make a copy and used to do the mutation process
        LinkedList<Solution> mutated = new LinkedList<>();

        // Here I make copies of each solution to avoid ghost copies,
        // before this the code made me a mess with those copies,
        // that I mention is because solution is an array
        population.forEach(solution -> mutated.add(new Solution(solution.getS().clone())));

        // For each solution I make a little modification
        mutated.forEach(this::mutateSolution);

        return mutated;

    }

    // This method help me to calculate the evaluation of each solution in the population
    public LinkedList<Solution> evaluation(LinkedList<Solution> population){
        // For each solution in the list I call the method evaluate
        population.forEach(Solution::evaluate);
        return population;
    }

    // This method apply the process "elitism" that refers to get the best solutions
    // between the first population and population after mutation
    public LinkedList<Solution> elitism(LinkedList<Solution> parents, LinkedList<Solution> children) {

        // Two new list to make a copy and used to do the elitism process
        LinkedList<Solution> first_generation = new LinkedList<>();
        LinkedList<Solution> next_generation = new LinkedList<>();
        LinkedList<Solution> new_generation = new LinkedList<>();

        // The same thing that I did in another methods to make a copy
        parents.forEach(solution -> first_generation.add(new Solution(solution.getS().clone())));

        // I sort the list to have the best values in the top of the list
        first_generation.sort((o1, o2) -> {
            // Here I compare the solutions through evaluation
            return o2.getEvaluation().compareTo(o1.getEvaluation());
        });

        // The same thing that I did in another methods to make a copy
        children.forEach(solution -> next_generation.add(new Solution(solution.getS().clone())));


        // Same process to sort
        next_generation.sort((o1, o2) -> {
            // Here I compare the solutions through evaluation
            return o2.getEvaluation().compareTo(o1.getEvaluation());
        });

        // For each list (first and next generation) I get the first size/2 values, so at the final
        // we will have the best solutions from both populations
        for(int i = 0; i < first_generation.size()/2; i++){
            new_generation.add(new Solution(first_generation.get(i).getS().clone()));
        }
        for(int i = 0; i < next_generation.size()/2; i++){
            new_generation.add(new Solution(next_generation.get(i).getS().clone()));
        }

        return new_generation;
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

    // Method to mutate a solution
    public void mutateSolution(Solution solution) {
        // First I generate a random number, this random number is
        // any index in the solution
        int n = (int)(Math.random()*(solution.getS().length)+0);

        // if the value is 1 it changes to 0 and vice versa
        if(solution.getS()[n] == 0){
            solution.getS()[n] = 1;
        } else {
            solution.getS()[n] = 0;
        }
    }

}

// Class solution on this case I am solving the problem One Max
// this problem search to get the max number of 1 in all dimensions
// for example [1, 1, 1, 1]
class Solution implements Cloneable {
    // Solution array, here we will have the 1's
    private int[] s;
    // Evaluation of the solution
    private Float evaluation;
    // Normalization of the solution
    private float normalization;

    // Constructor, to instance a Solution we need just put an array solution
    // for example Solution solution = new Solution({1, 0, 0, 0}];
    public Solution(int[] s) {
        this.s = s;
        this.evaluation = oneMax(this.s);
    }

    // This is the function that calculate the evaluation of the solution
    public Float oneMax(int[] s){
        // This is very nice example to use stream because first I get the value
        // using mapToDouble next sum this values.
        // Finally, I cast to float and return the evaluation
        return evaluation = (float) Arrays.stream(s).mapToDouble(value -> value).sum();
    }

    // Evaluation getter
    public Float getEvaluation() {
        return evaluation;
    }

    // Normalization setter
    public void setNorm(float valor){
        this.normalization = valor;
    }

    // Normalization getter
    public float getNorm() {
        return normalization;
    }

    // Array solution (s) getter
    public int[] getS() {
        return s;
    }

    // Method that call the function one max to evaluate this solution
    public void evaluate(){
        this.evaluation = oneMax(this.s);
    }

    // I needed to modify the clone method to get copies of the solution
    // and avoid ghost copies
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

