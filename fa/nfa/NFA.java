package fa.nfa;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;

import java.util.*;

public class NFA {
	NFAState start; // Only one state can be the start in a NFA
	Set<NFAState> finals; // HashSets track order that objects are stored into it, described in 3.3 #3
	Set<Character> transitions; // state -> (character -> next state)
	Set<NFAState> Q; // Set of all states in the NFA (including start, accepting and other states, if
						// any)
	Set<Character> Sigma; // Symbols used as transition characters for our NFA
	static ArrayList<Set<NFAState>> history;
	
	public NFA() {
		Q = new HashSet<NFAState>();
		Sigma = new HashSet<>();
		transitions = new HashSet<>();
		finals = new HashSet<NFAState>();
	}

	/**
	 * Sets the state with the name as label as start state
	 * 
	 * @param name
	 */
	public void addStartState(String name) {
		for (NFAState state : Q) {
			if (state.getName().equals(name)) {
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
	 * Adds a non-final, not initial state to the NFA instance using the state
	 * object
	 * 
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
		for (NFAState state : Q) {
			if (state.getName().equals(name)) {
				state.setFinalState(true);
				return;
			}
		}
		finals.add(finalState);
		finalState.setFinalState(true);
		Q.add(finalState);
	}

	public void addTransition(String fromState, char onSymb, String toState) {
		Sigma.add(onSymb);
		for (NFAState state : Q) {
			if (state.getName().equals(fromState)) {
				Set<NFAState> nextOne = state.getNextWithTransition(onSymb);
				if (nextOne == null) {
					for (NFAState q : Q) {
						if (q.getName().equals(toState)) {
							state.addStateWithTransition(q, onSymb);
						}
					}
				} else {
					for (NFAState el : nextOne) {
						if (!el.getName().equals(toState)) {
							for (NFAState q : Q) {
								if (q.getName().equals(toState)) {
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
		// Access stateMap of variable from. Get the set of states using get method of
		// stateMap.
		return from.getNextWithTransition(onSymb);
	}

	public void recursiveClosure(NFAState state, Set<NFAState> states) {
		// base condition
		if (state.getNextWithTransition('e') == null) {
			states.add(state);
		} // When we're done searching for 'e'
		for (NFAState empty : state.getNextWithTransition('e')) {// Finding e closure reachable states
			if (!empty.isVisited()) {
				empty.visited(true);
				states.add(empty);
				recursiveClosure(empty, states);
			}
		}
		// return null;
	}

	public Set<NFAState> eClosure(Set<NFAState> states) {
		
		for(NFAState state : states) {
			state.visited(false);
		}
		
		Set<NFAState> eClosureStates = new HashSet<NFAState>();
		for(NFAState state : states) {
		
		Queue<NFAState> qq = new LinkedList<>();
		qq.add(state);
		eClosureStates.add(state); // From State

		while (!qq.isEmpty()) {
			NFAState stateQ = qq.remove();
			if (!stateQ.isVisited()) {
				stateQ.visited(true);
				if (stateQ.getNextWithTransition('e') != null) // If there exist an etransition
				{
					Set<NFAState> eTransitionStates = stateQ.getNextWithTransition('e');
					if (eTransitionStates != null) {
						qq.addAll(eTransitionStates);
						eClosureStates.addAll(eTransitionStates);
					}
				}
			}

		}
		}
		return eClosureStates;
	}

	public boolean isFinal(Set<NFAState> s) {
		for(NFAState f : s)
		{
			if(f.isFinal())
			{
				return true;
			}
		}
        return false;
    }
	
	public DFA getDFA() {
		history = new ArrayList<Set<NFAState>>();
		Set<NFAState> curr = new HashSet<NFAState>(); // check
		Set<NFAState> emptySet = new HashSet<NFAState>();
		emptySet.add(start); // purpose of emptySet?

		DFA retDFA = new DFA(); // return DFA
		Queue<Set<NFAState>> q = new LinkedList<Set<NFAState>>(); // Queue containing set of NFAStates

		Set<NFAState> DFAStart = eClosure(emptySet); // return set of state that can reach with e-transition
		q.add(DFAStart); // q is for expanding and finding the nodes
		history.add(DFAStart);
		//System.out.println("DFA Start is: "+DFAStart.toString());
		retDFA.addStartState(DFAStart.toString()); // we have startState in DFA
		while (!q.isEmpty()) {// expand as we explore nodes

//			for(Set<NFAState> n : q)
//			{
//				System.out.println("q has : "+n.toString());
//			}
				
			Set<NFAState> pulled = q.remove(); // get first element in q
			for(NFAState e : pulled) // deepcopy method
			{
				curr.add(e);
			}
			pulled.addAll(eClosure(pulled));
			for (Character c : Sigma) { // Check every transition
				boolean finalState = false; //flag for finalState
				if (c != 'e') { //everything besides e
					Set<NFAState> enclosureState = new HashSet<NFAState>(); //State the the toState can go as eTrans
					Set<NFAState> transitionState = new HashSet<NFAState>(); // All states that a can go to
					Set<NFAState> totalTrans = new HashSet<NFAState>();
					
					for (NFAState pull : pulled) // Every state that pulled can go to with eTran
					{
						try{
							transitionState.addAll(pull.getNextWithTransition(c)); // Get all available states from fromState
						}catch(NullPointerException e)
						{
							
						}
					}

					
					if (transitionState != null)// If there are transition
					{
							enclosureState.addAll(eClosure(transitionState));
							if (isFinal(enclosureState)) {
								finalState = true;
							}
					}
					
					
					boolean contains = false;
					for(DFAState d : retDFA.getStates())//contains
					{
						if(d.toString().equals(enclosureState.toString()))
						{
							contains = true;
						}
					}
					
					
					
					if(finalState)
					{
						if(!curr.equals(enclosureState)&&!contains)
						{
							retDFA.addFinalState(enclosureState.toString());
						}
						retDFA.addTransition(pulled.toString(), c, enclosureState.toString());
					}else {
						
						if(!curr.equals(enclosureState) && !contains)
						{
							retDFA.addState(enclosureState.toString());
						}
						retDFA.addTransition(pulled.toString(), c, enclosureState.toString());
					}
					if(!pulled.toString().equals(enclosureState.toString())&& !history.contains(enclosureState)) {
						q.add(enclosureState);
						history.add(enclosureState);
					}
					
					
					//System.out.println("From " + curr.toString() + " with " + c + " to " + enclosureState.toString());

				}
			}
			
			}
//		for(DFAState d : retDFA.getStates())
//		{
//			d.getTransition();
//		}

		return retDFA;

	}
}
