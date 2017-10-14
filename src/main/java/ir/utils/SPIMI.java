package ir.utils;

import ir.data.TokenStream;

public class SPIMI {
	
	private static SPIMI instance;
	private TokenStream tokenStream;
	
	private SPIMI(){};
	
	public static SPIMI getInstance() {
		if(instance == null) {
			instance = new SPIMI();
		}
		return instance;
	}
	
	public void init() {
		tokenStream = TokenStream.getInstance();
		tokenStream.init();
	}
	
	public void spmiInvert() {
		
	}

	public static void main(String[] args) {
//		DocumentUtils util = new DocumentUtils();
//		util.writePOJOReuters();
//		if(util.readPOJOReuters()) {
//			System.out.println(util.getDocumentList().size());
//		}else {
//			System.err.println("error occurred");
//		}
//		util.spmiInvert();
		TokenStream tokenStream = TokenStream.getInstance();
		
		tokenStream.init();
		
	}
}
