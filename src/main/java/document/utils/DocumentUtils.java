package document.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.ObjectMapper;

import document.data.RawDocument;
import document.data.Reuter;

public class DocumentUtils {
	private static final String REUTERS_DIR = "src/main/resources/reuters21578";
	private static final String BUFFER_DIR = "src/main/resources/reuterBuffer";
	private static final String POJO_OUTPUT_DIR = "src/main/resources/POJOReuters";
	private static final String POJOReuterList = "POJOReuterList";
	private static final String INVERTED_INDEX_DIR = "src/main/resources/Inverted_index";
	private static final String INVERTED_INDEX_RAW = "InvertedIndexRaw.json";
	private RawDocument documentList;
	private Map<String, TreeSet<Integer>> dictionary;
	
	public DocumentUtils(){};
	
	public void writePOJOReuters() {
		File reutersDir = new File(REUTERS_DIR);
	
		if (reutersDir.exists()){ 
			System.out.println("Extracting........");
			File outputBuffer = new File(BUFFER_DIR);
			File outputPOJODir = new File(POJO_OUTPUT_DIR);
			outputBuffer.mkdir();
			outputPOJODir.mkdirs();
			ReutersUtils extractor = new ReutersUtils(reutersDir, outputBuffer);
			System.out.println("Now reading Reuter corpus........");
			RawDocument raw = extractor.extract();
			System.out.println("Now writing processed Reuter corpus into binary file........");
			try {
				File outFile = new File(outputPOJODir, POJOReuterList);
				ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(outFile));
				writer.writeObject(raw);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Now deleting our temp buffer directory........");
		    if(outputBuffer.delete()){
		    		System.out.println("Directory deleted.");
		    }
		        System.out.println("Done, document list length: "+ raw.getReuterList().size());
		}
	}
	
	public boolean readPOJOReuters() {
		File reuterByte = new File(POJO_OUTPUT_DIR + "/" + POJOReuterList);
        if(reuterByte.exists()) {
        		try {
        				System.out.println("Reading raw document data from file......");
					ObjectInputStream reader = new ObjectInputStream(new FileInputStream(reuterByte));
					this.documentList = (RawDocument) reader.readObject();
					reader.close();
					System.out.println("Done.");
				} catch (IOException|ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        return this.documentList != null;
	}
	
	@SuppressWarnings("null")
	public void spmiInvert() {
		if(this.documentList == null) {
			this.readPOJOReuters();
		}
		
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
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(outputFile));
            output.writeUTF(json);
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
		DocumentUtils util = new DocumentUtils();
		util.writePOJOReuters();
		if(util.readPOJOReuters()) {
			System.out.println(util.getDocumentList().size());
		}else {
			System.err.println("error occurred");
		}
		util.spmiInvert();
		
	}
}
