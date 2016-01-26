package com.ibm.optim.oaas.sample.cuttingStock.model;

/**
 * Represents the <code>Parameters</code> tuple, which is used to specify global 
 * parameters for the model.
 * <code>
 * tuple Parameters {
 *	int RollWidth;
 *	int nbPatterns; // The number of patterns generated so far
 * }
 * </code>
 */
public class Parameters {

	private Integer rollWidth;
	private Integer nbPatterns;

	public Parameters() {
		super();
		this.rollWidth = null;
		this.nbPatterns = null;
	}

	public Parameters(Integer rollWidth, Integer nbPatterns) {
		this();
		this.rollWidth = rollWidth;
		this.nbPatterns = nbPatterns;
	}

	public Parameters(Parameters original) {
		this(original.getRollWidth(), original.getNbPatterns());
	}

	public Parameters copy() {
		return new Parameters(this);
	}

	public Integer getRollWidth() {
		return rollWidth;
	}

	public void setRollWidth(Integer rollWidth) {
		this.rollWidth = rollWidth;
	}

	public Integer getNbPatterns() {
		return nbPatterns;
	}

	public void setNbPatterns(Integer nbPatterns) {
		this.nbPatterns = nbPatterns;
	}

	public Integer incrementNbPatterns() {
		return ++this.nbPatterns;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<");
		result.append(rollWidth + ", ");
		result.append(nbPatterns);
		result.append(">");		
		return result.toString();
	}
}
