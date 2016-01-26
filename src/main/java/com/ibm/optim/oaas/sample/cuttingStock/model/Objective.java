package com.ibm.optim.oaas.sample.cuttingStock.model;

/**
 * Class used to report information about the objective value for solutions to 
 * the optimization model.
 * 
 * Instances of this class are mapped to entries of the <code>Objective</code> post-processed
 * singleton tuple of the <code>cuttingStock.mod</code> and <code>cuttingStock-sub.mod</code> model. 
 * Properties are mapped to the corresponding fields of the <code>objective</code> tuple definition.
 */
public class Objective {

	private Double value;

	public Objective() {
		super();
	}

	public Objective(Objective original) {
		this();
		this.setValue(original.getValue());
	}

	public Objective copy() {
		return new Objective(this);
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
