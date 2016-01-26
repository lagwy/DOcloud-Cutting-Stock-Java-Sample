package com.ibm.optim.oaas.sample.cuttingStock.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used as a container to collect all output information for a 
 * solution calculated by the <code>cuttingStock</code> subproblem.
 * 
 * All properties of this class are mapped to corresponding tuple and 
 * tuple sets in the input data section of the <code>cuttingStock-sub.mod</code> model.
 */
public class SubproblemResult {

	@JsonIgnore
	private String problemId;

	@JsonProperty("objective")
	private Objective objective;

	@JsonProperty("slices")
	private Slice.List slices = new Slice.List();

	@JsonProperty("newPattern")
	private Pattern newPattern;

	public SubproblemResult() {
		super();
		this.problemId = null;
	}

	public SubproblemResult(String problemId) {
		this();
		this.problemId = problemId;
	}

	public SubproblemResult(String problemId, Objective objective, Slice.List slices, Pattern newPattern) {
		this();
		this.problemId = problemId;
		this.objective = objective;
		this.slices = slices;
		this.newPattern = newPattern;
	}

	public SubproblemResult(SubproblemResult original) {
		this(original.getProblemId(), original.getObjective().copy(),
				original.getSlices().copy(), original.getNewPattern().copy());
	}

	public SubproblemResult copy() {
		return new SubproblemResult(this);
	}

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		if(problemId != null && problemId.length() != 0)
			throw new UnsupportedOperationException("The problem ID has already been set");
		this.problemId= problemId;
	}

	public Objective getObjective() {
		return objective;
	}

	public void setObjective(Objective objective) {
		this.objective= objective;
	}

	public Pattern getNewPattern() {
		return newPattern;
	}

	public void setNewPattern(Pattern newPattern) {
		this.newPattern= newPattern;
	}

	public Slice.List getSlices() {
		return this.slices;
	}

	public void setSlices(Slice.List slices) {
		this.slices = slices;
	}

	public void displaySolution(SubproblemData subData) {
		System.out.println("Problem: " + getProblemId());
		System.out.println("Reduced Cost: " + getObjective().toString());
		System.out.println("New Pattern:");
		System.out.print(getNewPattern().getId() + ": ");
		System.out.println(getSlices().toString());
	}
}
