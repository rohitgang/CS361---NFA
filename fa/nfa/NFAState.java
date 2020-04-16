package fa.nfa;

import java.util.*;

public class NFAState extends fa.State {
    protected String name ; 
    private Hashtable<Character, ArrayList<NFAState>> stateMap ;
    private char transition;
    private Boolean isFinal;
    private boolean isStart;
    

    public NFAState(String name){
        this.name = name;
        this.stateMap = new Hashtable<Character, ArrayList<NFAState>>();
        this.transition = 'n';
        this.isFinal = false;
        this.isStart = false;
    }

    
    public ArrayList<NFAState> getNextWithTransition(char transition){
        ArrayList<NFAState> ret = stateMap.get(transition);
        if (ret == null) return null;
        else return ret;
    }

    public boolean addStateWithTransition(NFAState state, char transition){
        if (stateMap.get(transition) == null){
            ArrayList<NFAState> states = new ArrayList<NFAState>();
            states.add(state);
            this.stateMap.put(transition, states);
            return true;
        } else {
            if(stateMap.get(transition).contains(state)) return false;
            else{
                ArrayList<NFAState> states = stateMap.get(transition);
                states.add(state);
                stateMap.put(transition, states);
                return true;
            }
        }
    }
    public void setTransition(char transition)
    {
        this.transition = transition;
    }
    public char getTransition(){
        return this.transition;
    }
    public boolean isStart() {
        return isStart;
    }
    public void setStartState(boolean bool) {
        isStart = bool;
    }
    public boolean isFinal() {
        return isFinal;
    }
    public void setFinalState(boolean bool){
        isFinal = bool;
    }
    public String getName(){ return this.name; }

    public String toString(){ return name;}

    public NFAState deepCopy(){
        NFAState state = new NFAState(this.name);
        state.isFinal = this.isFinal;
        state.isStart = this.isStart;
        state.transition = this.transition;
        Set<Character> keys = this.stateMap.keySet();
        
        for(Character key: keys){
            state.stateMap.put(key, this.stateMap.get(key));
        }
        
        return state;
    }
}