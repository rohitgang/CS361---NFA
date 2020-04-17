package fa.nfa;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;

import java.util.*;

public class NFA implements NFAInterface {
    NFAState start; //Only one state can be the start in a NFA
    Set<NFAState> finals; //HashSets track order that objects are stored into it, described in 3.3 #3
    Set<Character> transitions; // state -> (character -> next state)
    Set<NFAState> Q; //Set of all states in the NFA (including start, accepting and other states, if any)
    Set<Character> Sigma; // Symbols used as transition characters for our NFA


public NFA() {
    Q = new HashSet<NFAState>();
    Sigma = new HashSet<>();
    transitions = new HashSet<>();
    finals = new HashSet<NFAState>();
}


     /**
     * Sets the state with the name as label as start state
     * @param name
     */
    public void addStartState(String name) {
        for(NFAState state : Q){
            if(state.getName().equals(name)){
                state.setStartState(true);
                start = state;
                return;
            }
        }
        start = new NFAState(name);
        Q.add(start);
        start.setStartState(true);
    }

    

     /**
     * Adds a non-final, not initial state to the NFA instance
     * 
     * @param name is the label of the state
     */
    public void addState(String name) {
       
        NFAState newState = new NFAState(name);
        Q.add(newState);
    }

    /**
     * Adds a non-final, not initial state to the NFA instance using the state object
     * @param state
     */
    public void addState(NFAState state) {
        Q.add(state);
    }


     /**
     * Adds a final state to the NFA
     * 
     * @param name is the label of the state
     */
    public void addFinalState(String name) {
        NFAState finalState = new NFAState(name);
        for(NFAState state : Q){
            if (state.getName().equals(name)) {
                state.setFinalState(true);
                return;
            }
        }
        finals.add(finalState);
        finalState.setFinalState(true);
        Q.add(finalState);
    }


    @Override
    public void addTransition(String fromState, char onSymb, String toState) {
        Sigma.add(onSymb);
        for(NFAState state : Q){
            if (state.getName().equals(fromState)){
                Set<NFAState> nextOne = state.getNextWithTransition(onSymb);
                if(nextOne != null){
                    for(NFAState el : nextOne){
                        if (!el.getName().equals(toState)){
                            for(NFAState q : Q){
                                if (q.getName().equals(toState)){
                                    state.addStateWithTransition(q, onSymb);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Getter for Q
     * 
     * @return a set of states that the NFA has
     */
    public Set<NFAState> getStates() {
        return Q;
    }

     /**
     * Getter for final states
     * 
     * @return a set of final states that the NFA has
     */
    public Set<NFAState> getFinalStates() {
        return finals;
    }
     /**
     * Getter for q0
     * 
     * @return the start state of the NFA
     */
    public State getStartState() {
        return start;
    }

    /**
     * Getter for Sigma
     * 
     * @return the alphabet of the NFA
     */

    public Set<Character> getABC() {
        return Sigma;
    }

    /**
     * Uses transition function delta of FA
     * 
     * @param from   the source state
     * @param onSymb the label of the transition
     * @return the sink state.
     */
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        // Access stateMap of variable from. Get the set of states using get method of stateMap.   
        return from.getNextWithTransition(onSymb);
    }

    // public Set<NFAState> eClosure(NFAState state){
    //     // We create a Set of NFAState's and add the "state" to it.
    //     Set<NFAState> eClosureStates = new HashSet<NFAState>();
    //     eClosureStates.add(state);
    //     state.visited(true);
        
        
    //     return eClosureStates;
    // }

    public void recursiveClosure(NFAState state, Set<NFAState> states)
    {
        // base condition
        if(state.getNextWithTransition('e') == null){  states.add(state);} //When we're done searching for 'e'
        for(NFAState empty : state.getNextWithTransition('e')){//Finding e closure reachable states
            if(!empty.isVisited()){
                empty.visited(true);
                states.add(empty);
                recursiveClosure(empty, states);
            }
        }
        // return null;
    }


    public Set<NFAState> eClosure(NFAState state, Set<NFAState> epsilon){
        Set<NFAState> curr = state.getNextWithTransition('e');
        if(curr != null){   //If there are states that have 'e'
            for(NFAState empty : curr){
                if(!empty.isVisited()){
                    empty.visited(true);
                    NFAState next = empty.deepCopy();
                    epsilon.add(next);
                    eClosure(next, epsilon);
                }
            }
        } else {
            curr = new HashSet<NFAState>();
        }
        return epsilon;


    }

    @Override
    public DFA getDFA() {
        Set<NFAState> emptySet = new HashSet<NFAState>();
        emptySet.add(start);
        DFA retDFA = new DFA();
        Queue<Set<NFAState>> q = new LinkedList<>();

        Set<NFAState> DFAStart = eClosure(start, emptySet);
        // q.add(DFAStart);
        retDFA.addStartState(DFAStart.toString());
        // Set<NFAState> allClosures = new HashSet<NFAState>();
        for (NFAState state : Q){
            Set<NFAState> eOfState = eClosure(state, emptySet);
            if (!eOfState.toString().equals(state.toString())) q.add(eOfState);
        }
        for(Set<NFAState> set_of_state : q){
            NFAState newOne = new NFAState(set_of_state.toString());
            Hashtable<Character, Set<NFAState>> forNewState = new Hashtable<Character, Set<NFAState>>();
            for(NFAState state : set_of_state){
                for(Character alphabet: Sigma){
                    Set<NFAState> toStates = state.getNextWithTransition(alphabet);
                    if(forNewState.get(alphabet) == null && toStates != null) 
                        forNewState.put(alphabet, toStates);
                    else{
                        if(toStates!= null && !forNewState.get(alphabet).toString().equals(toStates.toString())){
                            forNewState.put(alphabet, toStates);
                        }
                    }

                }    
                for(Character ch: forNewState.keySet()){ // Created statemap for new states
                    for(NFAState el : forNewState.get(ch))
                        newOne.addStateWithTransition(el, ch);
                }
            }
            retDFA.addState(newOne.toString());
            // retDFA.addTransition(fromState, onSymb, toState);
        } //
        return retDFA;    
    }

    
    //NFA SPECIFIC METHODS

    /**
     * This method converts an NFA object into a DFA
     * @return a converted DFA
     */
    // @Override
    // public DFA getDFA() {
    //     //Initialize necessary variables for BFS
    //     DFA answerDFA = new DFA(); // Create the new DFA
    //     Queue<HashSet<NFAState>> queue = new LinkedList<>();  // Create a queue (of sets of NFAStates) for BFS

    //     // Create a set that contains the startState, and add this set to our DFA and queue
    //     Set<NFAState> startDFAState = eClosure(start);
    //     answerDFA.addStartState(startDFAState.toString());
    //     queue.add((HashSet<NFAState>) startDFAState); //Add the start set to the queue

    //     return internetMethod(queue,answerDFA);
    // }

    /**
     * Internet pseudocode method of using BFS with queue to create DFA
     */
    public DFA internetMethod(Queue<HashSet<NFAState>> queue, DFA answerDFA) {
        while(!queue.isEmpty()) { //For each X in Q’...
            HashSet<NFAState> currentSet = queue.poll();
            HashSet<DFAState> allDFAStates = (HashSet<DFAState>) answerDFA.getStates();

            //Perform closure on the current set
            for(NFAState currentState : currentSet) { //Perform closure on the current state set
                currentSet.addAll(eClosure(currentState));
            }

            for(char currentTransition : Sigma) { //For each a in sigma...
                if(currentTransition != 'e') { // Except for e, obviously

                    // Do the GOTO operation on the closure set
                    HashSet<NFAState> current_goTo_Set = new HashSet<>();
                    for(NFAState currentNFAState : currentSet) {
                        current_goTo_Set.addAll(getToState(currentNFAState,currentTransition));
                    }

                    //If that set isn't empty...
                    HashSet<NFAState> current_goTo_Closure_Set = new HashSet<>();
                    if(!current_goTo_Set.isEmpty()) {

                        // Do a e-closure of the state set.
                        for(NFAState current_goTo_Closure_State : current_goTo_Set) {
                            current_goTo_Closure_Set.addAll(eClosure(current_goTo_Closure_State));
                        }

                        // If this closure is a new set of states...
                        int sizeOfDFAStates = answerDFA.getStates().size();
                        addDFAState(current_goTo_Closure_Set,allDFAStates,answerDFA); // Add to DFA machine
                        answerDFA.addTransition(currentSet.toString(),currentTransition,current_goTo_Closure_Set.toString()); // Add transition
                        if(sizeOfDFAStates < answerDFA.getStates().size()) //If a state was added...
                            queue.add(current_goTo_Closure_Set); // Add the set to our queue to repeat the process
                    }
                    else {
                        //Add trap state
                        int sizeOfDFAStates = answerDFA.getStates().size();
                        addDFAState(new HashSet<NFAState>(),allDFAStates,answerDFA);
                        answerDFA.addTransition(currentSet.toString(),currentTransition,current_goTo_Closure_Set.toString());
                        if(sizeOfDFAStates < answerDFA.getStates().size()) //If a state was added...
                            queue.add(current_goTo_Closure_Set); // Add the set to our queue to repeat the process
                    }
                }
            }
        }
        return answerDFA;
    }

    /**
     * This method computes the set of NFA states that can be reached from the argument state s
     * by going only along ε transitions, including s itself.
     * This is accomplished using Depth First Search (DFS) algorithm
     * @param s
     * @return
     */
   // @Override
    // public Set<NFAState> eClosure(NFAState s) {
    //     //Create a new set of states that will be our eClosure and add our source state to this set immediately
    //     Set<NFAState> eClosureSet = new HashSet<>();
    //     eClosureSet.add(s);

    //     //Create a stack to store state names in, and push the source s to this stack immediately
    //     Stack<NFAState> dfsStack = new Stack<>();
    //     dfsStack.push(s);

    //     //Create a map to track visited states
    //     LinkedHashMap<NFAState,Boolean> visitedMap = new LinkedHashMap<>();
    //     for(NFAState currentState : Q) {
    //         visitedMap.put(currentState,false);
    //     }

    //     //Mark the source as visited
    //     visitedMap.replace(s,true);

    //     //Now initiate DFS...
    //     while(!dfsStack.isEmpty()) {
    //         //Pop a state from stack to visit next
    //         NFAState currentStackState = dfsStack.pop();

    //         //Get all the 'e' transition neighbors of the currentStackState
    //         Set<NFAState> eNeighborSet = getToState(getSpecificState(currentStackState.name),'e'); //Get all the 'e' transition neighbors of currentStackNodeString THIS METHOD DOESNT WORK BECAUSE IT CAN'T TRAVERSE

    //         //If this set isn't empty...
    //         if(!eNeighborSet.isEmpty()) {

    //             //Get the eClosure of all the neighbors...
    //             for(NFAState currentState : eNeighborSet)
    //                 eClosureSet.add(currentState);

    //             //For each 'e' neighbor...
    //             for(NFAState currentNeighbor : eNeighborSet) {
    //                 if(!visitedMap.get(currentNeighbor)) { //If the current neighbor was not visited...
    //                     dfsStack.push(currentNeighbor); //Then push it to the DFS stack...
    //                     visitedMap.replace(currentNeighbor,true); //...and mark it as visited
    //                 }
    //             }

    //         }
    //     }

    //     return eClosureSet;
    // }

    

}