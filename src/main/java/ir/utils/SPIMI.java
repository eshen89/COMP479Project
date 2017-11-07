package ir.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import ir.data.Token;
import ir.data.TokenStream;

/**
 * @author ethanshen
 *
 *	Class definitions for SPIMI.
 */
public class SPIMI {
	
	private static SPIMI instance;
	private TokenStream tokenStream;
	private List<Map<String, TreeSet<Integer>>> spimiList = new ArrayList<>();
	private Map<String, TreeSet<Integer>> spimiIndex = new HashMap<>();
	private Map<String, TreeSet<Integer>> mergedIndex = new TreeMap<>();
	
	private final String OUTPUT_DIR = "src/main/resources/Inverted_index/";
	private final String INVERTED_INDEX = "invertedIndex_";
	private final String SORTED_INVERTED_INDEX = "sorted_invertedIndex_";
	
	
	private SPIMI(){};
	
	/**
	 * @return SPIMI
	 */
	public static SPIMI getInstance() {
		if(instance == null) {
			instance = new SPIMI();
		}
		return instance;
	}
	
	/**
	 * Init method.
	 */
	public void init() {
		tokenStream = TokenStream.getInstance();
		tokenStream.init();
		
		long startTime = System.currentTimeMillis();
		
		spimiInvert(100000, 10000, tokenStream);		
		
		System.out.println("Merging all blocks.......");
		for(Map<String, TreeSet<Integer>> map: this.spimiList) {
			this.mergedIndex = merge(map, this.mergedIndex);
		}
		
		System.out.println("Writing sorted Inverted List into txt file.......");
		writeToFile(OUTPUT_DIR + SORTED_INVERTED_INDEX, mergedIndex, 0);
		System.out.println("Inverted Index Initialized!");
		
		timeUsed(startTime);
	}
	
	/**
	 * 
	 * Separate entire TokenStream into blocks;
	 * Construct inverted index for each block;
	 * Merge and sort all blocks.
	 * @param memorySize
	 * @param blockSize
	 * @param tokenStream
	 * 
	 */
	public void spimiInvert(long memorySize, long blockSize, TokenStream tokenStream) {
		System.out.println("Performing SPIMI.......");
		int ouputFileID = 0;
		long initSize = memorySize;
		
		while(tokenStream.hasNextToken()) {

			if(memorySize >= blockSize) {
				
				if(tokenStream.hasNextToken()) {
					Token token = tokenStream.nextToken();
					if(!isExist(token.getTerm())) {
						TreeSet<Integer> postingList = new TreeSet<>();
						postingList.add(token.getDocId());
						addToIndex(token.getTerm(), postingList);
					}else {
						spimiIndex.get(token.getTerm()).add(token.getDocId());
					}
				}
				
				memorySize-= 4;
				
			}else{
				this.spimiList.add(spimiIndex);
				writeToFile(OUTPUT_DIR + INVERTED_INDEX, spimiIndex, ouputFileID);
				ouputFileID++;
				memorySize = initSize;
				spimiIndex = new HashMap<>();
			}
		}
	}
	
	/**
	 * @param term
	 * @param posting
	 */
	private void addToIndex(String term, TreeSet<Integer> posting) {
		spimiIndex.put(term, posting);
	}
	
	/**
	 * @param term
	 * @return boolean
	 */
	private boolean isExist(String term) {
		return spimiIndex.containsKey(term);
	}
	
	/**
	 * @param filePath File path.
	 * @param source	 List of Reuter
	 * @param ouputFileID Document id for each output
	 */
	private void writeToFile(String filePath, Map<String, TreeSet<Integer>> source, int ouputFileID) {
		File outputFile = new File(filePath + ouputFileID + ".txt");
		try {
			PrintWriter printWriter = new PrintWriter(outputFile);
			for(String term: source.keySet()) {
				printWriter.write(term + ": " + source.get(term) + "\n");
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param sourceMap
	 * @param destinationMap
	 * @return Map<String, TreeSet<Integer>>
	 */
	public Map<String, TreeSet<Integer>> merge(Map<String, TreeSet<Integer>> sourceMap, Map<String, TreeSet<Integer>> destinationMap) {
		for(String term: sourceMap.keySet()) {
			if(destinationMap.containsKey(term)) {
				destinationMap.get(term).addAll(sourceMap.get(term));
			}else {
				destinationMap.put(term, sourceMap.get(term));
			}
		}
		return destinationMap;
	}

	/**
	 * @return Map<String, TreeSet<Integer>>
	 */
	public Map<String, TreeSet<Integer>> getMergedIndex() {
		return mergedIndex;
	}
		
    public TokenStream getTokenStream() {
		return tokenStream;
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
 