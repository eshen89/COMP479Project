package ir.utils;

import ir.data.Reuter;
import ir.data.TokenStream;

/**
 * @author Yang Shen(27159390)
 * 
 * BM25 Implementation based on the formula:
 * 
 * RSVd = ∑ log[N/dft] · [(k1 + 1)tftd / k1((1 − b) + b × (Ld/Lave)) + tftd]
 *       t∈q
 * 
 * N = total number of documents in corpus;
 * dft = the number of documents that contain term t;
 * tftd = frequency of term t in document d;
 * k1 = tuning parameter;
 * b = tuning parameter;
 * Ld = length of document;
 * Lave = average document length for the whole collection;
 * 
 *
 */


public class BM25 {
	
	private final double k1 = 2.0;
	private final double b = 0.75;
	private final int N = 21578;
	private final double Lave = 118;  //pre-calculated for Reuter21758

	private static BM25 instance;
	
	private BM25(){}

	public static BM25 getInstance() {
		if(instance == null){
			instance = new BM25();
		}
		return instance;
	}
	
	public double calculateScore(String term, Reuter reuter, int dft) {
		if(dft != 0) {
			int tftd = calculateTFTD(term, reuter);
			int ld = calculateLd(reuter);

			double numerator = (this.k1 + 1) * tftd;
			double denominator = this.k1 * ((1 - b) + b * (ld / this.Lave)) + tftd;

			return Math.log(this.N / dft) * (numerator / denominator);
		}
		return 0;
	}
	
	private int calculateTFTD(String term, Reuter reuter) {
		int counter = 0;
		String[] body = splitBody(reuter);
		
		if(body != null && body.length != 0) {
			for(String s: body) {
				if(term.equalsIgnoreCase(s)) {
					counter++;
				}
			}
		}
		
		return counter;
		
	}
	
	private int calculateLd(Reuter reuter) {
		int length = 0;
		String[] body = splitBody(reuter);
		
		length = body == null? 0 : body.length;
		
		//Remove invalid elements in body[].
		if(length != 0) {
			for (String s : body) {

				if (s.equalsIgnoreCase("") || s.equalsIgnoreCase(" ")) {

					length--;
				}
			}
		}
		return length;
	}
	
	private String[] splitBody(Reuter reuter) {
		String[] body = null;

		if(reuter.getBody() !=null && !reuter.getBody().equals("")) {
			String s = reuter.getBody();
			s = TokenStream.compress(s);
			body = s.split(" ");
		}
		return body;
	}

}
