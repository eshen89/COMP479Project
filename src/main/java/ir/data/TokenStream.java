package ir.data;

import java.util.LinkedList;
import java.util.Queue;

public class TokenStream {
	
	private static TokenStream instance;
	private RawDocument rawDocument;
	private Queue<Token> tokenList = new LinkedList<Token>();
	private TokenStream() {};
	
	public static TokenStream getInstance() {
		if(instance == null) {
			return instance = new TokenStream();
		}
		return instance;
	}
	
	public void init() {
		this.rawDocument = RawDocument.getInstance();
		this.rawDocument.init();
		System.out.println("Tokenizing the reuters........");
		
		String rawString = "";
		String[] stringBuffer;
		
		for(Reuter reuter: this.rawDocument.getReuterList()) {
			int docId = reuter.getDocID();
			StringBuilder sb = new StringBuilder();
			sb.append(reuter.getTitle());
			sb.append(reuter.getBody());
			rawString = sb.toString();
			rawString = compress(rawString);
			stringBuffer = rawString.split(" ");
			
			for(String rawToken: stringBuffer) {
				if(rawToken != null) {
					if(!(rawToken.equalsIgnoreCase("") && rawToken.equalsIgnoreCase(" "))) {
						Token token = new Token();
						token.setDocId(docId);
						token.setTerm(rawToken);
						this.tokenList.add(token);
					}
				}
			}
		}
		
		System.out.printf("Done, total token number: %d", this.tokenList.size());
		
	}
	
	private String compress(String rawString) {
		if(!rawString.equals("")) {
			String processedString = rawString;
			processedString = processedString.replaceAll("\\p{Punct}|\\d", " ");
			processedString = processedString.replaceAll("^ +| +$|( )+", "$1");
			processedString = processedString.toLowerCase();
			return processedString;
		}
		return rawString;
	}

}
