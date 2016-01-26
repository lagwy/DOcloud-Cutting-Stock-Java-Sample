package com.ibm.optim.oaas.sample.cuttingStock.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the <code>Item</code> tuple.
 * <code>
 * tuple Item {
 *	key string id;
 *	int width; 
 *	int order; // Number of this item ordered
 * }
 * </code>
 */
public class Item {

	private String id;
	private Integer width;
	private Integer order;

	public Item() {
		super();
	}

	public Item(String id, Integer width, Integer order) {
		this();
		this.id= id;
		this.width= width;
		this.order= order; 
	}

	public Item(Item original){
		this(original.getId(), original.getWidth(), original.getOrder());
	}

	public Item copy() {
		return new Item(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("<");
		result.append(id + ", ");
		result.append(width + ", ");
		result.append(order);
		result.append(">");		
		return result.toString();
	}

	@SuppressWarnings("serial")
	public static class List extends ArrayList<Item> {

		public List(Collection<? extends Item> l) {
			super(l);
		}

		public List() {
			this(new ArrayList<Item>());
		}

		public List(List original) {
			this();
			for(Item i: this)
				this.add(i.copy());
		}

		public List copy() {
			return new List(this);
		}

		public Map<String, Item> items() {
			Map<String, Item> result = new LinkedHashMap<String, Item>();
			for(Item t: this)
				result.put(t.id, t);	
			return Collections.unmodifiableMap(result);
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("[");
			for(Item t: this) {
				result.append(" " + t.toString() + ",\n");			
			}
			result.deleteCharAt(result.length()-1);
			result.deleteCharAt(result.length()-1);
			result.append("]");		
			return result.toString();		
		}
	}
}
