package ir.data;

import org.tartarus.snowball.ext.EnglishStemmer;

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
	private static Queue<Token> tokenList = new LinkedList<>();
	private List<String> stopWords = new ArrayList<>();
	
	private final String STOP_WORD_DIR = "src/main/resources/StopWords/stopWords.txt";
	
	private TokenStream() {}
	
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

		String rawString;
		String[] stringBuffer;
		
		System.out.println("Compressing.......");
		for(Reuter reuter: RawDocument.getReuterList()) {
			int docId = reuter.getDocID();
            rawString = reuter.getTitle() + reuter.getBody();
			rawString = compress(rawString);
			stringBuffer = rawString.split(" ");
			
			for(String rawToken: stringBuffer) {
				if(rawToken != null) {
                    Token token = new Token();
                    token.setDocId(docId);
                    token.setTerm(rawToken);
                    tokenList.add(token);
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
        String processedString;
	    if(!rawString.equals("")) {
			processedString = rawString;
			processedString = processedString.replaceAll("-", " ");
			processedString = processedString.replaceAll("\t", " ");
			processedString = processedString.replaceAll("\\p{Punct}|\\d", " ");
			processedString = processedString.replaceAll("^ +| +$|( )+", "$1");
			processedString = processedString.toLowerCase();
			processedString = removeStopWord(processedString);
            processedString = stem(processedString);

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
				this.stopWords.add(line);
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
		if(this.stopWords.size() == 0) {
			initStopwordList();
		}
		
		String[] buffer = lowercased_string.split(" ");
		String output;
		StringBuilder sb = new StringBuilder();
		for(String token: buffer) {
			if(!this.stopWords.contains(token)) {
				sb.append(token);
				sb.append(" ");
			}
		}
		
		return output = sb.toString();
	}

	public static String stem(String data){
        String[] strings = data.split(" ");
        StringBuilder sb = new StringBuilder();
        EnglishStemmer stemmer = new EnglishStemmer();
        for(String s: strings){
            stemmer.setCurrent(s);
            if(stemmer.stem()){
                sb.append(stemmer.getCurrent());
                sb.append(" ");
            }
        }
        return sb.toString();
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
	public List<String> getStopWords() {
		return stopWords;
	}
	
    public void setStopWords(List<String> stopWords) {
		this.stopWords = stopWords;
	}

	public static RawDocument getRawDocument() {
		return rawDocument;
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
