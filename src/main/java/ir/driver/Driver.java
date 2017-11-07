package ir.driver;

import java.util.*;
import java.util.stream.Collectors;

import ir.data.RawDocument;
import ir.data.Reuter;
import ir.data.TokenStream;
import ir.utils.BM25;
import ir.utils.SPIMI;

/**
 * @author YangShen
 * Driver class
 */
public class Driver {

	private static List<Reuter> reuters;
	
	private static void initIndex() {
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
		TreeSet<Integer> rawResult = new TreeSet<Integer>();
		
		if(stringBuffer.length != 0) {
            if (stringBuffer.length == 1) {
                rawResult = SPIMI.getInstance().getMergedIndex().get(stringBuffer[0]);
            } else {
                if (interpreter.equalsIgnoreCase("and")) {
                    for (String term : stringBuffer) {

                        TreeSet<Integer> postingList = SPIMI.getInstance().getMergedIndex().get(term);

                        if (rawResult.isEmpty() && postingList != null) {
                            rawResult.addAll(postingList);

                        } else {
                            intersect(rawResult, postingList);
                        }
                    }
                } else if (interpreter.equalsIgnoreCase("or")) {

                    for (String term : stringBuffer) {

                        TreeSet<Integer> postingList = SPIMI.getInstance().getMergedIndex().get(term);

                        if (rawResult.isEmpty() && postingList != null) {
                            rawResult.addAll(postingList);

                        } else {
                            union(rawResult, postingList);
                        }
                    }
                }
            }
            System.out.println("Non-ranked Result: " + rawResult.toString());
            System.out.println("Size of result: " + rawResult.size());

            List<Reuter> reuters = getReuters(rawResult);
            Map<Integer, Double> resultMap = new HashMap<>();

            for(String s: stringBuffer){
                int dft = SPIMI.getInstance().getMergedIndex().get(s) == null? 0: SPIMI.getInstance().getMergedIndex().get(s).size();

                for(Reuter reuter: reuters){
                    double score = bm25Score(s, reuter, dft);

                    if(resultMap.containsKey(reuter.getDocID())){

                        double newVal = resultMap.get(reuter.getDocID()) + score;
                        resultMap.replace(reuter.getDocID(), newVal);
                    }else{

                        resultMap.put(reuter.getDocID(), score);
                    }
                }
            }

            resultMap = sortByValue(resultMap);
            System.out.println("Ranked Result: " + resultMap.keySet().toString());
            System.out.println("Size of result: " + resultMap.keySet().size());
        }

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
        processed = TokenStream.compress(processed);
		return processed;
	}

	private static double bm25Score(String term, Reuter reuter, int dft){
	    BM25 bm25 = BM25.getInstance();
	    return bm25.calculateScore(term, reuter, dft);
    }

	private static List<Reuter> getReuters(TreeSet<Integer> docIDs){
	    if(docIDs != null && docIDs.size()!=0) {
            List<Reuter> result = new ArrayList<>();

            for (Integer i : docIDs) {
                result.add(reuters.get(i));
            }

            return result;
        }

        return null;
    }

    /**
     * Sort map by its value in descending order.
     * @param map
     * @param <K>
     * @param <V>
     * @return Map sorted by its value in descending order
     */

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
	
}
