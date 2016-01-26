package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used as a container to collect all output information for a solution 
 * calculated by the <code>cuttingStock</code> model.
 * 
 * All properties of this class are mapped to corresponding tuple and tuple sets 
 * in the post-processing section of the <code>cuttingStock.mod</code> model.
 */
public class MasterResult {

	@JsonIgnore
	private String problemId;

	@JsonProperty("objective")
	private Objective objective;

	@JsonProperty("use")
	private Usage.List use = new Usage.List();

	@JsonProperty("duals")
	private Dual.List duals = new Dual.List();

	public MasterResult() {
		super();
		this.problemId = new String();
	}

	public MasterResult(String problemId) {
		this();
		this.problemId = problemId;
	}

	public MasterResult(String problemId, Objective objective, Usage.List use, Dual.List duals) {
		this();
		this.problemId = problemId;
		this.objective = objective;
		this.use = use;
		this.duals = duals;
	}

	public MasterResult(MasterResult original) {
		this(original.getProblemId(), original.getObjective().copy(), original.getUse().copy(),
				original.getDuals().copy());
	}

	public MasterResult copy(){
		return new MasterResult(this);
	}

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		if(problemId != null && problemId.length() != 0)
			throw new UnsupportedOperationException("The problem ID has already been set");
		this.problemId = problemId;
	}

	public Objective getObjective() {
		return objective;
	}

	public void setObjective(Objective objective) {
		this.objective = objective;
	}

	public Usage.List getUse() {
		return use;
	}

	public void setUse(Usage.List use) {
		this.use = use;
	}

	public Dual.List getDuals() {
		return duals;
	}

	public void setDuals(Dual.List duals) {
		this.duals = duals;
	}

	public void displaySolution(MasterData masterData) {
		Map<Integer, String> patternSlices = masterData.getSlices().patternSlicesToStrings();
		System.out.println("Problem: " + getProblemId());
		System.out.println("Objective value: " + getObjective().toString());
		System.out.println("Pattern Usage:");
		for(Usage u: getUse()) {
			if(u.getNumber() > 0.0) {
				System.out.print("Pattern: " + u.getPattern() + " Use: " + u.getNumber() + " [");
				System.out.print(patternSlices.get(u.getPattern()));
				System.out.println("]");
			}
		}
	}
}
