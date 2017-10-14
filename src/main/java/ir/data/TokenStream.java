package ir.data;

import java.util.LinkedList;
import java.util.Queue;

public class TokenStream {
	public static TokenStream instance;
	
	private TokenStream() {};
	
	public static TokenStream getInstance() {
		if(instance == null) {
			return instance = new TokenStream();
		}
		return instance;
	}
	
	private Queue<Token> tokenList = new LinkedList<Token>();
	
}
