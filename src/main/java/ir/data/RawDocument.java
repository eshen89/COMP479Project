package ir.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawDocument implements Serializable {

	private static final long serialVersionUID = 1L;
	private static RawDocument instance;
	private List<Reuter> reuterList;
	
	private static final String REUTERS_DIR = "src/main/resources/reuters21578";
	private static final String BUFFER_DIR = "src/main/resources/reuterBuffer";
	
	private final File reutersDir = new File(REUTERS_DIR);
	
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static int docID=0;

    private Pattern EXTRACTION_PATTERN = Pattern.compile("<TITLE>(.*?)</TITLE>|<BODY>(.*?)</BODY>");
	private static String[] META_CHARS = {"&", "<", ">", "\"", "'", ""};
	private static String[] META_CHARS_SERIALIZATIONS = {"&amp;", "&lt;", "&gt;", "&quot;", "&apos;", "Reuter &#3;"};

    private RawDocument(){}
    
    public static RawDocument getInstance() {
    	if(instance == null) {
    		return instance = new RawDocument();
    	}
    	
    	return instance;
    }
    
    public void init() {
    	System.out.println("Reading from <Reuters21578>.......");
    	
		if (reutersDir.exists()){ 
			System.out.println("Extracting........");
			
			File outputBuffer = new File(BUFFER_DIR);
			outputBuffer.mkdir();
			
			this.extract();
			outputBuffer.delete();
			
		    System.out.println("Extracting done, Reuter document list length: "+ this.getReuterList().size());
		}
    }
    
	private void extract(){
        File [] sgmFiles = reutersDir.listFiles(new FileFilter(){
            public boolean accept(File file){
                return file.getName().endsWith(".sgm");
            }
        });
        
        if (sgmFiles != null && sgmFiles.length > 0){
	        	for (int i = 0; i < sgmFiles.length; i++){
	            	File sgmFile = sgmFiles[i];
	            	this.addReuters(extractFile(sgmFile));
	        	}
        }else{
            System.err.println("No .sgm files in " + reutersDir);
        }
    }

    private List<Reuter> extractFile(File sgmFile){
		try{
	        BufferedReader reader = new BufferedReader(new FileReader(sgmFile));
	
	        StringBuffer buffer = new StringBuffer(1024);
	        StringBuffer outBuffer = new StringBuffer(1024);
	
	        String line = null;
	        	List<Reuter> reuterList = new ArrayList<Reuter>();
	        	File outFile = null;
	        while((line = reader.readLine()) != null){
	        		//when we see a closing reuters tag, flush the file
	        		if (line.indexOf("</REUTERS") == -1) {
	        			//Replace the SGM escape sequences
	        			buffer.append(line).append(' ');//accumulate the strings for now, then apply regular expression to get the pieces,
	        		}else{
	                    //Extract the relevant pieces and write to a file in the output dir
	                    Matcher matcher = EXTRACTION_PATTERN.matcher(buffer);
	                    while (matcher.find()){
	                        for (int i = 1; i <= matcher.groupCount(); i++){
	                            if (matcher.group(i) != null){
	                                outBuffer.append(matcher.group(i));
	                            }
	                        }
	                        outBuffer.append(LINE_SEPARATOR);
	                    }
	                    String out = outBuffer.toString();
	                    for (int i = 0; i < META_CHARS_SERIALIZATIONS.length; i++){
	                        out = out.replaceAll(META_CHARS_SERIALIZATIONS[i], META_CHARS[i]);
	                    }
	                    outFile = new File(BUFFER_DIR, "buffer.txt");
	                    
	                    FileWriter writer = new FileWriter(outFile);
	                    writer.write(out);
	                    writer.close();
	                    outBuffer.setLength(0);
	                    buffer.setLength(0);
	                    
	                    docID++;
	                    
	                    BufferedReader reuterReader = new BufferedReader(new FileReader(outFile));
	                    Reuter reuter = new Reuter();
	                    reuter.setDocID(docID);
	                    reuter.setTitle(reuterReader.readLine());
	                    reuter.setBody(reuterReader.readLine());
	                    reuterList.add(reuter);
	                    reuterReader.close();
	                }
	        }
	        reader.close();
	        outFile.delete();
	        return reuterList;
	    }catch (IOException e){
	        throw new RuntimeException(e);
	    }
	}
    
    private void addReuters(List<Reuter> reuters) {
    		if(this.reuterList == null) {
    			this.reuterList = reuters;
    		}else {
    			this.reuterList.addAll(reuters);
    		}
    }
    
    public int size() {
    		return this.reuterList.size();
    }

	public List<Reuter> getReuterList() {
	    return reuterList;
	}

	public void setReuterList(List<Reuter> reuterList) {
	    this.reuterList = reuterList;
	}
}
