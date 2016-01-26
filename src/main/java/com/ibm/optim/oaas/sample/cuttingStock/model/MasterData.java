package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used as a container to collect all required information for a 
 * master problem instance to be processed by the <code>cuttingStock</code> model.
 * 
 * All properties of this class are mapped to corresponding tuple and 
 * tuple sets in the input data section of the <code>cuttingStock.mod</code> model.
 */
public class MasterData {

	@JsonIgnore
	private String problemId;

	@JsonProperty("items")
	private Item.List items = new Item.List();

	@JsonProperty("patterns")
	private Pattern.List patterns = new Pattern.List();

	@JsonProperty("slices")
	private Slice.List slices = new Slice.List();

	public MasterData() {
		super();
		this.problemId = new String();
	}

	public MasterData(String problemId) {
		this();
		this.problemId = problemId;
	}

	public MasterData(String problemId, Item.List items, Pattern.List patterns, Slice.List slices) {
		super();
		this.problemId = problemId;
		this.items = items;
		this.patterns = patterns;
		this.slices = slices;
	}

	public MasterData(MasterData original) {
		this(original.getProblemId(), original.getItems().copy(), original.getPatterns().copy(), 
				original.getSlices().copy());	
	}

	public MasterData copy() {
		return new MasterData(this);
	}

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		if(problemId != null && problemId.length() != 0)
			throw new UnsupportedOperationException("The problem ID has already been set");
		this.problemId = problemId;
	}

	public Item.List getItems() {
		return items;
	}

	public Pattern.List getPatterns() {
		return patterns;
	}

	public Slice.List getSlices() {
		return slices;
	}

	public void displayData() {
		Map<Integer, String> patternSlices = this.getSlices().patternSlicesToStrings();
		System.out.println("Problem: " + getProblemId());
		System.out.println("Items:");
		System.out.println(getItems().toString());
		System.out.println("Patterns:");
		for(Pattern p: getPatterns()) {
			System.out.print(p.getId() + ": [");
			System.out.print(patternSlices.get(p));
			System.out.println("]");
		}
	}

	/**
	 * Creates a default data set for the Master problem.
	 */
	public static MasterData default1() {
		MasterData result = new MasterData("Cutting Stock Data Set 1");
		result.patterns = new Pattern.List();
		result.patterns.add(new Pattern(0, 1));
		result.patterns.add(new Pattern(1, 1));
		result.patterns.add(new Pattern(2, 1));
		result.patterns.add(new Pattern(3, 1));
		result.patterns.add(new Pattern(4, 1));

		result.items = new Item.List();
		result.items.add(new Item("XJC001_1", 20, 48));
		result.items.add(new Item("XJC001_2", 45, 35));
		result.items.add(new Item("XJC001_3", 50, 24));
		result.items.add(new Item("XJC001_4", 55, 10));
		result.items.add(new Item("XJC001_5", 75, 8));

		result.slices = new Slice.List();
		result.slices.add(new Slice("XJC001_1", 0, 1));
		result.slices.add(new Slice("XJC001_2", 1, 1));
		result.slices.add(new Slice("XJC001_3", 2, 1));
		result.slices.add(new Slice("XJC001_4", 3, 1));
		result.slices.add(new Slice("XJC001_5", 4, 1));

		return result;
	}
}
