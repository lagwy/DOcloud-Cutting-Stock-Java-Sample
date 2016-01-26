package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the <code>Slice</code> tuple.
 * <code>
 * tuple Slice {
 *	key string item;
 *	key int pattern;
 *	int number; // Number of slices of this item in this pattern
 * }
 * </code>
 */
public class Slice {

	private String item;
	private	Integer pattern;
	private	Integer number;

	public Slice() {
		super();
		this.item = null;
		this.pattern = null;
		this.number = null;
	}

	public Slice(String item, Integer pattern, Integer number) {
		this();
		this.item = item;
		this.pattern = pattern;
		this.number = number;
	}

	public Slice(Slice original) {
		this(original.getItem(), original.getPattern(), original.getNumber());
	}

	public Slice copy() {
		return new Slice(this);
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	public Integer getPattern() {
		return pattern;
	}

	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Slice other = (Slice) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<");
		result.append(item + ", ");
		result.append(pattern + ", ");
		result.append(number);
		result.append(">");		
		return result.toString();
	}

	@SuppressWarnings("serial")
	public static class List extends ArrayList<Slice> {

		public List(Collection<? extends Slice> l) {
			super(l);
		}

		public List() {
			this(new ArrayList<Slice>());
		}

		public List(List original) {
			this();
			for(Slice s: this)
				this.add(s.copy());
		}

		public List copy() {
			return new List(this);
		}

		public Map<Integer, Map<String, Integer>> patternSlices() {
			Map<String, Integer> row; // key: item id, value: number of slices
			Map<Integer, Map<String, Integer>> result = new LinkedHashMap<Integer, Map<String, Integer>>(); // key: pattern id, value: row map
			for(Slice s: this) {
				if (result.containsKey(s.getPattern()))
					row = result.get(s.getPattern());
				else
					row = new LinkedHashMap<String, Integer>(); 
				row.put(s.getItem(), s.getNumber());
				result.put(s.getPattern(), row);
			}			
			return Collections.unmodifiableMap(result);
		}

		public Map<Integer, String> patternSlicesToStrings() {
			Map<Integer, String> result = new LinkedHashMap<Integer, String>(); // key: pattern id, value: string of slices
			Map<String, Integer> row; // key: item id; value: number of slices
			StringBuilder rowString = new StringBuilder();
			for(Integer patternId: this.patternSlices().keySet()) {
				row = this.patternSlices().get(patternId);
				for(String itemId: row.keySet()) {
					if(row.get(itemId)>0)
						rowString.append("<item: " + itemId + " slices: " + row.get(itemId) + ">");
				}
				result.put(patternId, rowString.toString());
				rowString.delete(0, rowString.length());
			}			
			return Collections.unmodifiableMap(result);
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("[");
			for(Slice s: this) {
				result.append(" " + s.toString() + ",\n");			
			}
			result.deleteCharAt(result.length()-1);
			result.append("]");		
			return result.toString();		
		}		
	}
}
