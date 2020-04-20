Project 2: Non-Deterministic Finite Automata

* Authors : Rohit Gangurde, Steven Kim, Andre, Maldonado
* Class: CS361 Section 2
* Semester: Spring 2020

* Overview

This java program implements a non-deterministic finite automata from an input file specifying the final states, start state, additional states and valid transitions.

* Compiling and Using

To compile, execute the following command in the project directory :
	$ javac fa/nfa/NFADriver.java

Run the class with this command and input file p1tc1.txt :
	$ java fa.nfa.NFADriver ./tests/p2tc1.txt

* Discussion

The main class in our program was NFA.java, and the methods we spent most time on was getDFA() and eClosure(). 

For the eClosure method, we first thought about setting it up as a recursive method. We tried to make it work but failed to get the correct implementation. Most of the time we had an empty set added to the return value. After several iterations of eClosure method, we settled on using a queue of NFAStates, and performing depth first search until that queue is empty. 

The getDFA method took most of the time of our development. We used different datastructures in this method. We used a set, an array list and a queue. Queue was used for implementing breadth first search. At the beginning of the method, we add the eclosure of the start state to the queue and the 'history' arraylist. After this we iterate the queue till its empty. Same as eClosure method, we had many iterations of the getDFA method. One of our most important mistake was not deepCopying the NFAStates and not having a data structure to keep track of what we encounter. Once we had figured this out, things became easier.


