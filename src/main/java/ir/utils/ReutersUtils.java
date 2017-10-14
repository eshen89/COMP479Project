package document.utils;

/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import document.data.RawDocument;
import document.data.Reuter;


/**
 * Split the Reuters SGML documents into Simple Text files containing: Title, Body
 */
public class ReutersUtils
{
    private File reutersDir;
    private File outputDir;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static int docID=0;

    private Pattern EXTRACTION_PATTERN = Pattern.compile("<TITLE>(.*?)</TITLE>|<BODY>(.*?)</BODY>");
	private static String[] META_CHARS = {"&", "<", ">", "\"", "'", ""};
	private static String[] META_CHARS_SERIALIZATIONS = {"&amp;", "&lt;", "&gt;", "&quot;", "&apos;", "Reuter &#3;"};

	public ReutersUtils(File reutersDir, File outputDir)
    {
        this.reutersDir = reutersDir;
        this.outputDir = outputDir;
    }

    public RawDocument extract()
    {
        File [] sgmFiles = reutersDir.listFiles(new FileFilter(){
            public boolean accept(File file){
                return file.getName().endsWith(".sgm");
            }
        });
        
        RawDocument rawDocuments = null;
        
        if (sgmFiles != null && sgmFiles.length > 0){
            rawDocuments = new RawDocument();
	        	for (int i = 0; i < sgmFiles.length; i++){
	            	File sgmFile = sgmFiles[i];
	            	rawDocuments.appendReuterList(extractFile(sgmFile));
	        	}
        }else{
            System.err.println("No .sgm files in " + reutersDir);
        }
        return rawDocuments;
    }

    /**
     * Override if you wish to change what is extracted
     *
     * @param sgmFile
     */
    protected List<Reuter> extractFile(File sgmFile){
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
	                    outFile = new File(outputDir, "buffer.txt");
	                    
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
}
