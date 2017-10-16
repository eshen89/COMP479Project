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

public class SPIMI {
	
	private static SPIMI instance;
	private TokenStream tokenStream;
	private List<Map<String, TreeSet<Integer>>> spimiList = new ArrayList<Map<String, TreeSet<Integer>>>();
	private Map<String, TreeSet<Integer>> spimiIndex = new HashMap<String, TreeSet<Integer>>();
	private Map<String, TreeSet<Integer>> mergedIndex = new TreeMap<String, TreeSet<Integer>>();
	
	private final String OUTPUT_DIR = "src/main/resources/Inverted_index/";
	private final String INVERTED_INDEX = "invertedIndex_";
	private final String SORTED_INVERTED_INDEX = "sorted_invertedIndex_";
	
	
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
		
		long startTime = System.currentTimeMillis();
		
		spimiInvert(100000, 10000, tokenStream);		
		
		startTime = System.currentTimeMillis();
		
		System.out.println("Merging all blocks.......");
		for(Map<String, TreeSet<Integer>> map: this.spimiList) {
			this.mergedIndex = merge(map, this.mergedIndex);
		}
		System.out.println("Done!");
		
		timeUsed(startTime);
		
		System.out.println("Writing sorted Inverted List into txt file.......");
		writeToFile(OUTPUT_DIR + SORTED_INVERTED_INDEX, mergedIndex, 0);
		System.out.println("Inverted Index Initialized!");
		
	}
	
	public void spimiInvert(long memorySize, long blockSize, TokenStream tokenStream) {
		System.out.println("Performing SPIMI.......");
		long startTime = System.currentTimeMillis();
		int ouputFileID = 0;
		long initSize = memorySize;
		
		while(tokenStream.hasNextToken()) {

			if(memorySize >= blockSize) {
				
				if(tokenStream.hasNextToken()) {
					Token token = tokenStream.nextToken();
					if(!isExist(token.getTerm())) {
						TreeSet<Integer> postingList = new TreeSet<Integer>();
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
				spimiIndex = new HashMap<String, TreeSet<Integer>>();
			}
		}
		System.out.println("Done!");
		timeUsed(startTime);
	}
	
	private void addToIndex(String term, TreeSet<Integer> posting) {
		spimiIndex.put(term, posting);
	}
	
	private boolean isExist(String term) {
		return spimiIndex.containsKey(term);
	}
	
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

	public Map<String, TreeSet<Integer>> getMergedIndex() {
		return mergedIndex;
	}
	
    private void timeUsed(long start) {
		long elapsedTimeMillis = System.currentTimeMillis() - start;
		
		float elapsedTimeSec = elapsedTimeMillis/1000F;
		
		System.out.printf("Time consumed: %f \n",  elapsedTimeSec);
    }
}
 