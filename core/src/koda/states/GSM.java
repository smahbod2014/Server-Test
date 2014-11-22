package koda.states;

import java.util.Stack;

public class GSM {

	private Stack<State> states;
	
	public GSM() {
		states = new Stack<State>();
	}
	
	public void push(State s) {
		states.push(s);
	}
	
	public State pop() {
		return states.pop();
	}
	
	public State peek() {
		return states.peek();
	}
	
	public void set(State s) {
		pop();
		push(s);
	}
}
