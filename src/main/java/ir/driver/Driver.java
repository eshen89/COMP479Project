package ir.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import ir.data.RawDocument;
import ir.data.Reuter;
import ir.data.TokenStream;
import ir.utils.SPIMI;

/**
 * @author YangShen
 * Driver class
 */
public class Driver {

	private static List<String> stopwords = new ArrayList<String>();
	private static List<Reuter> reuters;
	
	public static void initIndex() {
		SPIMI spimi = SPIMI.getInstance();
		spimi.init();
		
		reuters = RawDocument.getReuterList();
		
	}
	
	public static void main(String[] args) {	
		System.out.println("Initializing Inverted Index......");
		initIndex();		
		
		boolean keepSessionAlive = true;
		Scanner sc = new Scanner(System.in);
		
		while(keepSessionAlive) {
			System.out.println("Please enter your query:");
			String query = sc.nextLine();
			
			System.out.println("Which interpreter you wanna use? AND/OR");
			String interpreter = sc.nextLine();
		
			if(interpreter.equalsIgnoreCase("and") || interpreter.equalsIgnoreCase("or")) {
				searchQuery(interpreter, query);
				
			} else {
				System.out.println("Wrong input, expecting AND/OR, your input: " + interpreter);
				
			}
			
			System.out.println("Do you want to search another one? Y/N");
			String keepAlive = sc.nextLine();
			
			if(keepAlive.equalsIgnoreCase("y") || keepAlive.equalsIgnoreCase("n")) {
				keepSessionAlive = keepAlive.equalsIgnoreCase("y");
				
			}
		}
		
		sc.close();
		System.out.println("Thank you for using IR, bye!");
		
	}

	/**
	 * Pre-process query into lower-cased, number-free, stop-word-free string.
	 * Lookup result for query.
	 * 
	 * @param interpreter
	 * @param query
	 * 
	 */
	private static void searchQuery(String interpreter, String query) {
		String processedQuery = processQuery(query);
		String[] stringBuffer = processedQuery.split(" ");
		TreeSet<Integer> result = new TreeSet<Integer>();
		
		if(stringBuffer.length == 1) {
			result = SPIMI.getInstance().getMergedIndex().get(stringBuffer[0]);
		}else {		
			if(interpreter.equalsIgnoreCase("and")) {
				for(String term: stringBuffer) {
					
					TreeSet<Integer> postingList = SPIMI.getInstance().getMergedIndex().get(term);
					
					if(result.isEmpty() && postingList != null) {
						result.addAll(postingList);
						
					}else {
						intersect(result, postingList);
					}
				}
			}else if(interpreter.equalsIgnoreCase("or")) {
				
				for(String term: stringBuffer) {
					
					TreeSet<Integer> postingList = SPIMI.getInstance().getMergedIndex().get(term);
					
					if(result.isEmpty() && postingList != null) {
						result.addAll(postingList);
						
					}else {
						union(result, postingList);	
					}
				}
			}
		}
		
		System.out.println("Result: " + result.toString());	
		System.out.println("Size of result: " + result.size());
	}

	/**
	 * @param postingList1
	 * @param postingList2
	 */
	private static void union(TreeSet<Integer> postingList1, TreeSet<Integer> postingList2) {
		//Union of two posting list.
		if(postingList2 != null) {

			postingList1.addAll(postingList2);
			
		}
	}

	/**
	 * @param postingList1
	 * @param postingList2 
	 */
	private static void intersect(TreeSet<Integer> postingList1, TreeSet<Integer> postingList2) {
		//Intersection of two posting list.
		if(postingList2 != null) {
			
			if(postingList1.size() > postingList2.size()) {
				postingList1.retainAll(postingList2);
				
			}else {
				postingList1.retainAll(postingList1);
				
			}
		}
	}
	
	/**
	 * @param query
	 * @return String
	 *
	 */
	private static String processQuery(String query) {
		String processed = query;
		processed = processed.toLowerCase();
		processed = removeStopWord(processed);
		processed = TokenStream.stem(processed);
		return processed;
	}
	
	/**
	 * @param query
	 * @return String
	 */
	@SuppressWarnings("unused")
	private static String removeStopWord(String query) {
		stopwords = TokenStream.getInstance().getStopWords();
		
		if(stopwords.size() == 0) {
			TokenStream.getInstance().initStopwordList();
		}
		
		String[] buffer = query.split(" ");
		String output;
		StringBuilder sb = new StringBuilder();
		
		for(String token: buffer) {
			
			if(!stopwords.contains(token)) {
				sb.append(token);
				sb.append(" ");
				
			}
		}
		
		return output = sb.toString();
	}
	
}
