package ir.driver;

import ir.utils.SPIMI;

public class Driver {

	public static void initIndex() {
		SPIMI spimi = SPIMI.getInstance();
		spimi.init();
	}
	
	public static void main(String[] args) {	
		initIndex();
		
		
	}
	
}
