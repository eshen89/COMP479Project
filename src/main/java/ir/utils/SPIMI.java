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
		
		spimiInvert(100000, 10000, tokenStream);
		
		System.out.println("Merging all blocks............");
		for(Map<String, TreeSet<Integer>> map: this.spimiList) {
			this.mergedIndex = merge(map, this.mergedIndex);
		}
		
		writeToFile(OUTPUT_DIR + SORTED_INVERTED_INDEX, mergedIndex, 0);
	}
	
	public void spimiInvert(long memorySize, long blockSize, TokenStream tokenStream) {		
		System.out.println("Performing SPIMI.........");
		int ouputFileID = 0;
		long initSize = memorySize;
		
		while(tokenStream.hasNextToken()) {

			if(!(blockSize >= memorySize)) {
				
				if(tokenStream.hasNextToken()) {
					Token token = tokenStream.nextToken();
					if(!isExist(token.getTerm())) {
						TreeSet<Integer> postingList = new TreeSet<Integer>();
						postingList.add(token.getDocId());
						addToIndex(token.getTerm(), postingList);
					}else {
						spimiIndex.get(token.getTerm()).add(token.getDocId());
						System.out.println(token.getTerm() + " is now added to map, list size: " + spimiIndex.get(token.getTerm()).size());
					}
				}
				
				memorySize--;
				
			}else{
				System.out.println("Writing block to disk........................................................");
				this.spimiList.add(spimiIndex);
				writeToFile(OUTPUT_DIR + INVERTED_INDEX, spimiIndex, ouputFileID);
				ouputFileID++;
				memorySize = initSize;
				spimiIndex = new HashMap<String, TreeSet<Integer>>();
			}
		}
		System.out.println("Done.");
	}
	
	private void addToIndex(String term, TreeSet<Integer> posting) {
		spimiIndex.put(term, posting);
		System.out.println(term + " is newly added, posting list size " + posting.size());
	}
	
	private boolean isExist(String term) {
		return spimiIndex.containsKey(term);
	}
	
	private void writeToFile(String filePath, Map<String, TreeSet<Integer>> source, int ouputFileID) {
		System.out.println("Writing output to file.........");
		File outputFile = new File(filePath + ouputFileID + ".txt");
		try {
			PrintWriter printWriter = new PrintWriter(outputFile);
			for(String term: source.keySet()) {
				printWriter.write(term + ": " + source.get(term) + "\n");
			}
			printWriter.close();
			System.out.println("Done.........");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, TreeSet<Integer>> merge(Map<String, TreeSet<Integer>> sourceMap, Map<String, TreeSet<Integer>> destinationMap) {
		if(destinationMap == null || destinationMap.isEmpty()) {
			for(String term: sourceMap.keySet()) {
				destinationMap.put(term, sourceMap.get(term));
			}
		}else {
			
			for(String term: sourceMap.keySet()) {
				if(destinationMap.containsKey(term)) {
					destinationMap.get(term).addAll(sourceMap.get(term));
				}else {
					destinationMap.put(term, sourceMap.get(term));
				}
			}
			
		}
		return destinationMap;
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
		SPIMI spimi = SPIMI.getInstance();
		
		spimi.init();
		
		
	}
}
 