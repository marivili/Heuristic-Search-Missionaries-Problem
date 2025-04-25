import java.util.HashSet;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SpaceSearcher {
    // PriorityQueue to manage states based on their f-cost (g-cost + h-cost)
    private PriorityQueue<State1> queueOfState1;
    // HashSet to store visited states and prevent revisiting them
    private HashSet<State1> visitedState;

    SpaceSearcher() { // Constructor
        // Initialize the PriorityQueue with a comparator based on f-cost
        this.queueOfState1 = new PriorityQueue<>(Comparator.comparingInt(State1::getFCost));
        this.visitedState = new HashSet<>();
    }

    State1 AStarvisitedState(State1 initialState, int heuristic) {
        // If the initial state is already the goal, return it
        if (initialState.isGoal())
            return initialState;

        // Add the initial state to the priority queue
        this.queueOfState1.offer(initialState);

        // Continue searching while there are states in the priority queue
        while (!this.queueOfState1.isEmpty()) {
            // Remove the state with the lowest f-cost from the queue
            State1 currentState = this.queueOfState1.poll();

            // If the current state is the goal, return it
            if (currentState.isGoal())
                return currentState;

            // If the current state has not been visited yet
            if (!this.visitedState.contains(currentState)) {
                // Mark the current state as visited
                this.visitedState.add(currentState);

                // Generate all valid successor states
                for (State1 successor : currentState.generateSuccessors()) {
                    // Update the g-cost (cost from the start state to this successor)
                    successor.setGCost(currentState.getGCost() + currentState.getCostTo(successor));

                    // Add the successor to the priority queue for further exploration
                    this.queueOfState1.offer(successor);
                }
            }
        }
        // If the priority queue is empty and no goal state was found, return null
        return null;
    }
}
