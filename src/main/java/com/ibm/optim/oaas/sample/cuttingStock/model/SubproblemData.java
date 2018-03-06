package com.ibm.optim.oaas.sample.cuttingStock.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used as a container to collect all required information for a 
 * subproblem instance to be processed by the <code>cuttingStock-sub</code> model.
 * 
 * All properties of this class are mapped to corresponding tuple and tuple 
 * sets in the input data section of the <code>cuttingStock-sub.mod</code> model.
 * 
 */
public class SubproblemData {

	@JsonIgnore
	private String problemId;

	@JsonProperty("parameter")
	public Parameters parameters = new Parameters(0, 0);

	@JsonProperty("items")
	public Item.List items = new Item.List();

	@JsonProperty("duals")
	public Dual.List duals = new Dual.List();

	public SubproblemData() {
		super();
		this.problemId = null;
	}

	public SubproblemData(String problemId) {
		this();
		this.problemId = problemId;
	}

	public SubproblemData(String problemId, Parameters parameters, Item.List items, Dual.List duals) {
		this();
		this.problemId = problemId;
		this.parameters = parameters;
		this.items = items;
		this.duals = duals;
	}

	public SubproblemData(SubproblemData original) {
		this(original.getProblemId(), original.getParameters().copy(), original.getItems().copy(),
				original.getDuals().copy());
	}

	public SubproblemData copy() {
		return new SubproblemData(this);
	}

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		if(problemId != null && problemId.length() != 0)
			throw new UnsupportedOperationException("The problem ID has already been set");
		this.problemId = problemId;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public Item.List getItems() {
		return items;
	}

	public Dual.List getDuals() {
		return duals;
	}

	public void setDuals(Dual.List duals) {
		this.duals = duals;
	}

	public void displayData() {
		System.out.println("Problem: " + getProblemId());
		System.out.println("Parameters: " + getParameters().toString());	
		System.out.println("Items:");
		System.out.println(getItems().toString());
		System.out.println("Duals:");
		System.out.println(getDuals().toString());
	}

	/**
	 * Creates a default data set for the Subproblem.
	 */
	public static SubproblemData default1() {
		SubproblemData result = new SubproblemData("Cutting Stock Data Set 1");

		result.parameters = new Parameters(110, 5);

		result.items = new Item.List();
		result.items.add(new Item("XJC001_1", 20, 48));
		result.items.add(new Item("XJC001_2", 45, 35));
		result.items.add(new Item("XJC001_3", 50, 24));
		result.items.add(new Item("XJC001_4", 55, 10));
		result.items.add(new Item("XJC001_5", 75, 8));

		return result;
	}
}
