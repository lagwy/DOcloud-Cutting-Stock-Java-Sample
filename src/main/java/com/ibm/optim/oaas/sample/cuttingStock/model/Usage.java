package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the <code>Usage</code> tuple.
 * <code>
 * tuple Usage {
 *	key int pattern;
 *	float number; // Number of times the pattern is used
 * }
 * </code>
 */
public class Usage {

	private Integer pattern;
	private	Double number;

	public Usage() {
		super();
		this.pattern = null;
		this.number = null;
	}

	public Usage(Integer pattern, Double number) {
		this();
		this.pattern = pattern;
		this.number = number;
	}

	public Usage(Usage original) {
		this(original.getPattern(), original.getNumber());
	}

	public Usage copy() {
		return new Usage(this);
	}

	public Integer getPattern() {
		return pattern;
	}

	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}

	public Double getNumber() {
		return number;
	}

	public void setNumber(Double number) {
		this.number = number;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<");
		result.append(pattern + ", ");
		result.append(number);
		result.append(">");		
		return result.toString();
	}

	@SuppressWarnings("serial")
	public static class List extends ArrayList<Usage> {

		public List(Collection<? extends Usage> l) {
			super(l);
		}

		public List() {
			this(new ArrayList<Usage>());
		}

		public List(List original) {
			this();
			for(Usage u : this)
				this.add(u.copy());
		}

		public List copy() {
			return new List(this);
		}		

		public Map<Integer, Usage> uses() {
			Map<Integer, Usage> result = new LinkedHashMap<Integer, Usage>();
			for(Usage t : this)
				result.put(t.getPattern(), t);	
			return Collections.unmodifiableMap(result);
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("[");
			for(Usage t: this) {
				result.append(" " + t.toString() + ",\n");			
			}
			result.deleteCharAt(result.length()-1);
			result.append("]");		
			return result.toString();		
		}
	}
}
