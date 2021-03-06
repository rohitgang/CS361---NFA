package fa.nfa;

import java.util.*;
/**
 * NFAState class inherits State class. Contains all attributes of state class.
 * @author Rohit Gangurde, Andre Maldonado, Steven Kim
 */
public class NFAState extends fa.State {
    protected String name ; 
    private Hashtable<Character, Set<NFAState>> stateMap ;
    private char transition;
    private Boolean isFinal;
    private boolean isStart;
    private boolean visited;
    
    /**
     * Contructor to initialize the instance variables
     * @param name : label of the state
     */
    public NFAState(String name){
        this.name = name;
        this.stateMap = new Hashtable<Character, Set<NFAState>>();
        this.transition = 'n';
        this.isFinal = false;
        this.isStart = false;
        this.visited = false;
    }
    /**
     * Sets the visited attribute of the state
     * @param val
     */
    public void setVisited(boolean val){ this.visited = val;}
    
    /**
     * Checks the visited attribute of the state
     * @return this.visited
     */
    public boolean isVisited(){ return this.visited; }
    
    /**
     * Returns the next state provided the transition character
     * @param transition : char representing the transition
     * @return ret : set of next states if found, null otherwise 
     */
    public Set<NFAState> getNextWithTransition(char transition){
        Set<NFAState> ret = stateMap.get(transition);
        if (ret == null) return null;
        else return ret;
    }
    /**
     * Adds the next state to the stateMap dictionary
     * @param state : NFAState object to add with the corresponding transition
     * @param transition : char representing the transition
     * @return
     */
    public boolean addStateWithTransition(NFAState state, char transition){
        if (stateMap.get(transition) == null){
            Set<NFAState> states = new HashSet<NFAState>();
            states.add(state);
            this.stateMap.put(transition, states);
            return true;
        } else {
            if(stateMap.get(transition).contains(state)) return false;
            else{
                Set<NFAState> states = stateMap.get(transition);
                states.add(state);
                stateMap.put(transition, states);
                return true;
            }
        }
    }

    /**
     * Sets the transition of the current state
     * @param transition
     */
    public void setTransition(char transition){ this.transition = transition; }
    
    /** 
     * Gets the transition string of the current state
     * @return transition
     */
    public char getTransition(){ return this.transition; }
    
    /**
     * Returns the isStart attribute of the state object
     * @return isStart
     */
    public boolean isStart() { return isStart; }

    /**
     * Sets the isStart attribute of the state object
     * @param bool
     */
    public void setStartState(boolean bool) { isStart = bool; }

    /**
     * Returns the isFinal attribute of the state object
     * @return isFinal
     */
    public boolean isFinal() { return isFinal; }

    /**
     * Sets the isFinal atribute of the state object
     * @param bool
     */
    public void setFinalState(boolean bool){ isFinal = bool; }

    /**
     * Returns the label of the state object
     */
    public String getName(){ return this.name; }

    /**
     * Returns a string representation of the state
     */
    public String toString(){ return name; }

    /**
     * Creates a deepcopy of the current state object
     * @return state : deep copy of the current state object
     */
    public NFAState deepCopy(){
        NFAState state = new NFAState(this.name);
        state.isFinal = this.isFinal;
        state.isStart = this.isStart;
        state.transition = this.transition;
        state.visited = this.visited;
        Set<Character> keys = this.stateMap.keySet();
        
        for(Character key: keys){
            state.stateMap.put(key, this.stateMap.get(key));
        } 
        return state;
    }
}