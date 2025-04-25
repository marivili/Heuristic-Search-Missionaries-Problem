import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State1 implements Comparable<State1> {

    private static int N; // Total number of missionaries/cannibals on each side initially
    private static int M; // Boat capacity
    private static int K; // Maximum number of allowed crossings

    private int missionaries; // Number of missionaries on the initial side
    private int cannibals; // Number of cannibals on the initial side
    private int boat; // Position of the boat: 1 = initial side, 0 = destination side
    private int crossings; // Current number of crossings
    private State1 parent; // Parent state for backtracking
    private int gCost; // Actual cost from start to this state
    private int hCost; // Heuristic cost to goal

    public State1(int missionaries, int cannibals, int boat, int crossings) {
        this.missionaries = missionaries;
        this.cannibals = cannibals;
        this.boat = boat;
        this.crossings = crossings;
        this.parent = null;
        this.gCost = 0;
        this.hCost = heuristic();
    }

    public static void initialize(int totalMissionaries, int boatCapacity, int maxCrossings) {
        N = totalMissionaries;
        M = boatCapacity;
        K = maxCrossings;
    }

    public int getMissionaries() {
        return missionaries;
    }

    public int getCannibals() {
        return cannibals;
    }

    public int getBoat() {
        return boat;
    }

    public int getCrossings() {
        return crossings;
    }

    // Getter for the g-cost (actual cost from the start state to this state)
    public int getGCost() {
        return gCost;
    }

    public void setGCost(int gCost) {
        this.gCost = gCost;
    }

    // Getter for the f-cost (sum of g-cost and h-cost)
    public int getFCost() {
        return gCost + hCost;
    }

    // Getter for the parent state (used for backtracking the solution path)
    public State1 getParent() {
        return parent;
    }

    // Setter for the parent state (used to link a state to its predecessor)
    public void setParent(State1 parent) {
        this.parent = parent;
    }

    private int heuristic() {
        // Calculate the total number of people remaining on the initial side
        int remainingPeople = missionaries + cannibals;

        // If there are no people left on the initial side, we have reached the goal
        if (remainingPeople == 0) {
            return 0; // No more crossings are required
        }

        // If the boat is on the destination side (boat == 0)
        // Every remaining person will require two trips (one for the boat to return and
        // one for transport)
        if (boat == 0) {
            return 2 * remainingPeople; // Minimum number of crossings when the boat is on the wrong side
        }

        // If there is only one person remaining on the initial side, only one crossing
        // is needed
        if (remainingPeople == 1) {
            return 1; // Single crossing required
        }

        // For more than one person remaining, the optimal strategy requires 2n - 3
        // crossings
        // This accounts for moving two people, bringing one back, and repeating
        return 2 * remainingPeople - 3; // Efficient strategy for multiple people
    }

    public int getCostTo(State1 other) {
        return 1; // Each transition has a cost of 1
    }

    // Method to check if the current state is the goal state
    // The goal is reached if there are no missionaries and cannibals on the left
    // and the boat is on the right side
    public boolean isGoal() {
        return missionaries == 0 && cannibals == 0 && boat == 0;
    }

    public boolean isValid() {
        // The number of missionaries and cannibals must be within valid bounds
        if (missionaries < 0 || cannibals < 0 || missionaries > N || cannibals > N) {
            return false;
        }
        // On the left side, the number of missionaries must not be less than cannibals,
        // unless there are no missionaries on that side
        if (missionaries > 0 && missionaries < cannibals) {
            return false;
        }
        int otherMissionaries = N - missionaries; // Missionaries on the right side
        int otherCannibals = N - cannibals; // Cannibals on the right side

        // On the right side, the number of missionaries must not be less than
        // cannibals, unless there are no missionaries on that side
        if (otherMissionaries > 0 && otherMissionaries < otherCannibals) {
            return false;
        }
        return true;
    }

    public List<State1> generateSuccessors() {
        List<State1> successors = new ArrayList<>();
        // Generate all possible moves for a given boat capacity M.
        // Try all combinations of missionaries and cannibals that fit in the boat (<= M
        // capacity).
        for (int m = 0; m <= M; m++) {
            for (int c = 0; c <= M - m; c++) {
                if (m + c > 0) { // At least one person must be in the boat

                    int newMissionaries;
                    int newCannibals;

                    // Update the number of missionaries and cannibals based on the boat's position
                    if (boat == 1) { // If the boat is on the initial side
                        newMissionaries = missionaries - m;
                        newCannibals = cannibals - c;
                    } else { // If the boat is on the destination side
                        newMissionaries = missionaries + m;
                        newCannibals = cannibals + c;
                    }

                    int newBoat = 1 - boat; // Toggle the boat's side
                    int newCrossings = crossings + 1;

                    // If the number of crossings exceeds the maximum allowed (K), skip this move
                    if (newCrossings > State1.K) {
                        continue;
                    }

                    // Create a new state with the updated values
                    State1 newState = new State1(newMissionaries, newCannibals, newBoat, newCrossings);
                    newState.setParent(this);
                    newState.setGCost(this.gCost + 1);
                    newState.hCost = newState.heuristic();

                    // Check if the new state is valid
                    if (newState.isValid()) {
                        successors.add(newState);
                    }
                }
            }
        }
        return successors;
    }

    @Override
    public boolean equals(Object obj) {
        // Check if the objects are the same reference
        if (this == obj)
            return true;
        // Check if the object is null or of a different class
        if (obj == null || getClass() != obj.getClass())
            return false;
        // Cast the object to State1 for comparison
        State1 state = (State1) obj;
        // Compare the missionaries, cannibals, and boat position for equality
        return missionaries == state.missionaries &&
                cannibals == state.cannibals &&
                boat == state.boat;
    }

    @Override
    public int hashCode() {
        // Objects with the same data have the same hash code
        return Objects.hash(missionaries, cannibals, boat);
    }

    @Override
    public int compareTo(State1 other) {
        // Compare the fCost of the current object and the other object
        // Return -1 if the current object has a lower fCost, 0 if equal, 1 if higher
        return Integer.compare(this.getFCost(), other.getFCost());
    }

    @Override
    public String toString() {
        String boatPosition = (boat == 1) ? "left" : "right";
        int otherMissionaries = N - missionaries; // People on the other side
        int otherCannibals = N - cannibals;

        return "Left Side: " + missionaries + " missionaries, " + cannibals + " cannibals | " +
                "Right Side: " + otherMissionaries + " missionaries, " + otherCannibals + " cannibals | " +
                "Boat on the " + boatPosition + " side.";
    }
}