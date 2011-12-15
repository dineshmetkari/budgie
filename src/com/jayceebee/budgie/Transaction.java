package com.jayceebee.budgie;

import java.util.Date;

public class Transaction {
	
	private Date date;
	private double value;
	private String place;
	
	public Transaction(Date date, double value, String place) {
		super();
		this.date = date;
		this.value = value;
		this.place = place;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	
	
	
	

}
