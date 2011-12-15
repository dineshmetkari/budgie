package com.jayceebee.budgie;

public class TransactionCategory {
	private String name;
	private double limit;
	
	public TransactionCategory(String name, double limit) {
		super();
		this.name = name;
		this.limit = limit;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public double getLimit() {
		return limit;
	}
	public void setLimit(double limit) {
		this.limit = limit;
	}
	
	
}
