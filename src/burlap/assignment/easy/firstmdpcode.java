package burlap.assignment.easy;

import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.oomdp.statehashing.DiscretizingHashableStateFactory;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.*;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction; 
import burlap.oomdp.singleagent.RewardFunction; 


public class firstmdpcode {
	DomainGenerator 	dg;
	Domain 				domain;
	State 				initState;
	RewardFunction		rf;
	TerminalFunction	tf;
	DiscretizingHashableStateFactory hashFactory;
	
	int 				numStates;
	
	public firstmdpcode(double p1, double p2, double p3, double p4){
		
		int numStates = 6;
		this.dg = new GraphDefinedDomain(numStates);
		
		//actions from initial state 0
		((GraphDefinedDomain) this.dg).setTransition(0,0,1,1.);
		((GraphDefinedDomain) this.dg).setTransition(0,1,2,1.);
		((GraphDefinedDomain) this.dg).setTransition(0,2,3,1.);
		
		//transition from action 'a' outcome state
		((GraphDefinedDomain) this.dg).setTransition(1,0,1,1.);
		
		//transition from action 'b' outcome state
		((GraphDefinedDomain) this.dg).setTransition(2,0,4,1.);
		((GraphDefinedDomain) this.dg).setTransition(4,0,2,1.);
		
		//transition from action 'c' outcome state
		((GraphDefinedDomain) this.dg).setTransition(3,0,5,1.);
		((GraphDefinedDomain) this.dg).setTransition(5,0,5,1.);		


		this.domain = this.dg.generateDomain();
		this.initState = GraphDefinedDomain.getState(this.domain, 0);
		this.rf = new FourParamRF(p1,p2,p3,p4);
		this.tf = new NullTermination();
		this.hashFactory = new DiscretizingHashableStateFactory(p1);
	}
	
	public static class FourParamRF implements RewardFunction{
		double p1;
		double p2;
		double p3;
		double p4;
		
		public FourParamRF(double p1, double p2, double p3, double p4){
			this.p1 = p1; 
			this.p2 = p2; 
			this.p3 = p3; 
			this.p4 = p4; 
		}
		
		@Override
		public double reward(State s, GroundedAction a, State sprime) {
			int sid = GraphDefinedDomain.getNodeId(s);
			double r;
			
			if (sid == 0 || sid == 3) { // initial state or c1
				r = 0;
			}
			else if (sid == 1 ) { //a
				r = this.p1;
			}
			else if(sid == 2) { //b1
				r = this.p2;
			}
			else if( sid == 4 ){ //b2
				r = this.p3;
			}
			else if (sid == 5 ){ //c2
				r = this.p4;
			}
			else {
				throw new RuntimeException("Unknown state: " + sid);
			}
			
			return r;
		}
	
	}
	
	public Domain getDomain() {
		return this.domain;
	}
	
	private ValueIteration computeValue(double gamma) {
		double maxDelta = 0.0001;
		int maxIterations = 1000;
		ValueIteration vi = new ValueIteration(this.domain, this.rf, this.tf, gamma, 
				this.hashFactory, maxDelta, maxIterations);
		vi.planFromState(this.initState);
		return vi; 
		
	}
	
	public String bestFirstAction(double gamma){
		ValueIteration vi = computeValue(gamma);
		
		double[] V = new double[this.numStates];
		for (int i = 0; i < this.numStates; i++) {
			State s = GraphDefinedDomain.getState(this.domain,i);
			V[i] = vi.value(s);
		}
		
		String actionName = null;
		if( V[1] >= V[2] && V[1] >= V[3]) actionName = "action a";
		else if( V[2] >= V[1] && V[2] >= V[3] ) actionName = "action b";
		else if( V[3] >= V[1] && V[3] >= V[2] ) actionName = "action c";
		return actionName;
	}
	
	public static void main(String[] args){
	}
	
}







