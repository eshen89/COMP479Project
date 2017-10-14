package ir.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;

import ir.data.RawDocument;
import ir.data.Reuter;
import ir.data.TokenStream;

public class DocumentUtils {
	private static final String INVERTED_INDEX_DIR = "src/main/resources/Inverted_index";
	private static final String INVERTED_INDEX_RAW = "InvertedIndexRaw.txt";
	private RawDocument documentList;
	private Map<String, TreeSet<Integer>> dictionary;
	
	public DocumentUtils(){};
	
	@SuppressWarnings("null")
	public void spmiInvert() {
		
		this.dictionary = new HashMap<String, TreeSet<Integer>>();
		String rawString = "";
		String[] stringBuffer;
		
		List<Reuter> reuters = this.documentList.getReuterList();
		for(Reuter reuter: reuters) {
			StringBuilder sb = new StringBuilder();
			sb.append(reuter.getTitle());
			sb.append(reuter.getBody());
			rawString = sb.toString();				
			stringBuffer = rawString.split(" ");
		
			for(String token: stringBuffer) {
				if(token != null || token.equalsIgnoreCase("")) {
					if(!dictionary.containsKey(token.toLowerCase())) {
						TreeSet<Integer> postingList = new TreeSet<Integer>();
						postingList.add(reuter.getDocID());
						dictionary.put(token.toLowerCase(), postingList);
					}else {
						if(!(dictionary.get(token.toLowerCase()).contains(reuter.getDocID()))) {
							dictionary.get(token.toLowerCase()).add(reuter.getDocID());
						}
					}
				}
			}
		}
        try {
        		File file = new File(INVERTED_INDEX_DIR);
        		file.mkdirs();
        		File outputFile = new File(file, INVERTED_INDEX_RAW);
            String json = new ObjectMapper().writeValueAsString(this.dictionary);
            PrintWriter output =new PrintWriter(outputFile);
            output.write(json);
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	public RawDocument getDocumentList() {
		return documentList;
	}

	public void setDocumentList(RawDocument documentList) {
		this.documentList = documentList;
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
		TokenStream tokenStream = TokenStream.getInstance();
		
		tokenStream.init();
		
	}
}
