import java.util.Stack;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner Scanner = new Scanner(System.in);

        System.out.print("Enter the number of missionaries and cannibals N: ");
        int N = Scanner.nextInt();

        System.out.print("Enter the boat capacity M: ");
        int M = Scanner.nextInt();

        System.out.print("Enter the maximum number of crossings allowed K: ");
        int K = Scanner.nextInt();

        // Set the parameters in State1
        State1.initialize(N, M, K);

        // Create the initial state: all missionaries, cannibals on the starting side,
        // and boat at start and crossings at 0
        State1 initialState = new State1(N, N, 1, 0);

        // Initialize the A* space searcher
        SpaceSearcher searcher = new SpaceSearcher();

        long startTime = System.nanoTime();
        // Run the A* algorithm with initial state and heuristic type
        State1 solution = searcher.AStarvisitedState(initialState, 1);
        long endTime = System.nanoTime();

        System.out.println();
        long duration = (endTime - startTime) / 1_000_000; // Convert into milliseconds
        System.out.println("Execution time: " + duration + " ms");

        // Print the result
        if (solution != null) {
            System.out.println("Solution:");
            printSolutionPath(solution);
        } else {
            System.out.println("No solution found within the crossing limit.");
        }
    }

    private static void printSolutionPath(State1 state) {
        // Stack to store the path from the goal state back to the initial state
        Stack<State1> path = new Stack<>();

        // Traverse the linked states backwards from the goal to the initial state
        while (state != null) {
            path.push(state); // Push each state onto the stack
            state = state.getParent(); // Move to the parent state
        }

        State1 previous = null; // Used to keep track of the previous state

        // Print the initial state at the top of the path
        if (!path.isEmpty()) {
            State1 initialState = path.pop(); // Retrieve the initial state
            System.out.println("Initial State: " + initialState); // Print the initial state
            previous = initialState; // Set the initial state as the previous state
        }

        // Iterate through the remaining states in the stack
        while (!path.isEmpty()) {
            State1 current = path.pop(); // Pop the next state from the stack

            // Calculate the number of missionaries and cannibals moved in this step
            int missionariesMoved = Math.abs(current.getMissionaries() - previous.getMissionaries());
            int cannibalsMoved = Math.abs(current.getCannibals() - previous.getCannibals());

            // Determine the position of the boat after the move
            String boatPosition = (current.getBoat() == 1) ? "left" : "right";

            // Print the move details
            System.out.printf("Moved %d missionaries and %d cannibals to the %s side.\n\n",
                    missionariesMoved, cannibalsMoved, boatPosition);

            // Print the current state after the move
            System.out.println("Current State: " + current);

            // Update the previous state to the current one for the next iteration
            previous = current;
        }
    }

}