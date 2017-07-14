package com.cubead.clm.io.sogou;

class Count {
	private final int systemcount;
	private int ccount = 0;

	Count(int count){
		this.systemcount=count;
	}
	
	boolean add(){
		return systemcount < 0 || ++ccount < systemcount;
	}
}