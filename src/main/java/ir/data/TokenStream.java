package ir.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author YangShen
 *	Class definition for TokenStream
 */
public class TokenStream {
	
	private static TokenStream instance;
	private static RawDocument rawDocument;
	private static Queue<Token> tokenList = new LinkedList<Token>();
	private List<String> stopwords = new ArrayList<String>();
	
	public void setStopwords(List<String> stopwords) {
		this.stopwords = stopwords;
	}

	private final String STOP_WORD_DIR = "src/main/resources/StopWords/stopwords.txt";
	
	private TokenStream() {};
	
	/**
	 * @return TokenStream
	 */
	public static TokenStream getInstance() {
		if(instance == null) {
			return instance = new TokenStream();
		}
		return instance;
	}
	
	/**
	 * init method
	 */
	public void init() {
		rawDocument = RawDocument.getInstance();
		rawDocument.init();
		System.out.println("Tokenizing the reuters.......");
		long startTime = System.currentTimeMillis();
		
		String rawString = "";
		String[] stringBuffer;
		
		System.out.println("Compressing.......");
		for(Reuter reuter: rawDocument.getReuterList()) {
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
						tokenList.add(token);
					}
				}
			}
		}
		System.out.printf("Done, total token number: %d \n", tokenList.size());
		timeUsed(startTime);
	}
	
	/**
	 * Lower-cased rawString with removed punctuation, number, special character and stop words
	 * @param rawString
	 * @return String
	 *
	 */
	private String compress(String rawString) {
		if(!rawString.equals("")) {
			String processedString = rawString;
			processedString = processedString.replaceAll("-", " ");
			processedString = processedString.replaceAll("\t", " ");
			processedString = processedString.replaceAll("\\p{Punct}|\\d", " ");
			processedString = processedString.replaceAll("^ +| +$|( )+", "$1");
			processedString = processedString.toLowerCase();
			processedString = removeStopWord(processedString);
			return processedString;
		}
		return rawString;
	}
	
	/**
	 * Initialization of stop word
	 * Read from stopword.txt, load into a list.
	 */
	public void initStopwordList() {
		System.out.println("Initializing StopWord list.......");
		File stopWordRef = new File(STOP_WORD_DIR);
		String line = null;
		try {
			FileReader fileReader = new FileReader(stopWordRef);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				this.stopwords.add(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stop word removed string
	 * @param lowercased_string
	 * @return String
	 *
	 */
	@SuppressWarnings("unused")
	private String removeStopWord(String lowercased_string) {
		if(this.stopwords.size() == 0) {
			initStopwordList();
		}
		
		String[] buffer = lowercased_string.split(" ");
		String output;
		StringBuilder sb = new StringBuilder();
		for(String token: buffer) {
			if(!this.stopwords.contains(token)) {
				sb.append(token);
				sb.append(" ");
			}
		}
		
		return output = sb.toString();
	}
	
	/**
	 * @return boolean
	 */
	public boolean hasNextToken() {
		return !tokenList.isEmpty();
	}
	
	/**
	 * @return Token
	 * Return token if exist.
	 */
	public Token nextToken() {
		if(tokenList != null || hasNextToken()) {
			return tokenList.poll();
		}
		return null;
	}

	/**
	 * @return List<String>
	 */
	public List<String> getStopwords() {
		return stopwords;
	}
	
    /**
     * @param start
     */
    private void timeUsed(long start) {
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		
		System.out.printf("<Time consumed: %.2f sec> \n",  elapsedTimeSec);
    }

}
