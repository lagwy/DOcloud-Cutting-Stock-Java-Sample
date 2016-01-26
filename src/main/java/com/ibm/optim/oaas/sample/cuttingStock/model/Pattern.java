package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the <code>Pattern</code> tuple.
 * <code>
 * tuple Pattern {
 *   key int id;
 *   int cost;
 * }
 * </code>
 */
public class Pattern {

	private Integer id;
	private Integer cost;

	public Pattern() {
		super();
		this.id = null;
		this.cost = null;
	}

	public Pattern(Integer id, Integer cost) {
		this();
		this.id = id;
		this.cost = cost;
	}

	public Pattern(Pattern original) {
		this(original.getId(), original.getCost());
	}

	public Pattern copy() {
		return new Pattern(this);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<");
		result.append(id + ", ");
		result.append(cost);
		result.append(">");		
		return result.toString();
	}

	@SuppressWarnings("serial")
	public static class List extends ArrayList<Pattern> {

		public List(Collection<? extends Pattern> l) {
			super(l);
		}

		public List() {
			this(new ArrayList<Pattern>());
		}

		public List(List original) {
			this();
			for(Pattern p: this)
				this.add(p.copy());
		}

		public List copy() {
			return new List(this);
		}

		public Map<Integer, Pattern> patterns() {
			Map<Integer, Pattern> result = new HashMap<Integer, Pattern>();
			for(Pattern t: this)
				result.put(t.id, t);	
			return Collections.unmodifiableMap(result);
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("[");
			for(Pattern t: this) {
				result.append(" " + t.toString() + ",\n");			
			}
			result.deleteCharAt(result.length()-1);
			result.deleteCharAt(result.length()-1);
			result.append("]");		
			return result.toString();		
		}
	}
}
