package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the <code>Dual</code> tuple.
 * <code>
 * tuple Dual {
 *	key string item;
 *	float price;
 * }
 * </code>
 */
public class Dual {

	private String item;
	private Double price;

	public Dual() {
		super();
	}

	public Dual(String item, Double price) {
		this();
		this.item = item;
		this.price = price;
	}

	public Dual(Dual original) {
		this(original.getItem(), original.getPrice());
	}

	public Dual copy() {
		return new Dual(this);
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<");
		result.append(item + ", ");
		result.append(price);
		result.append(">");		
		return result.toString();
	}

	@SuppressWarnings("serial")
	public static class List extends ArrayList<Dual> {

		public List(Collection<? extends Dual> l) {
			super(l);
		}

		public List() {
			this(new ArrayList<Dual>());
		}

		public List(List original) {
			this();
			for(Dual d: this)
				this.add(d.copy());
		}

		public List copy() {
			return new List(this);
		}

		public Map<String, Dual> duals() {
			Map<String, Dual> result= new LinkedHashMap<String, Dual>();
			for(Dual t: this)
				result.put(t.getItem(), t);	
			return Collections.unmodifiableMap(result);
		}

		@Override
		public String toString() {
			StringBuilder result= new StringBuilder();
			result.append("[");
			for(Dual t: this) {
				result.append(" " + t.toString() + ",\n");			
			}
			result.deleteCharAt(result.length()-1);
			result.append("]");		
			return result.toString();		
		}
	}
}
