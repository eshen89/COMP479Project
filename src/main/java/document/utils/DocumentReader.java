package document.utils;

import java.io.File;
import java.util.List;

import document.data.RawDocument;
import edu.stanford.nlp.coref.data.Document;

public class DocumentReader {
	private final String[] REUT2 = {"reut2-000.sgm","reut2-001.sgm", "reut2-002.sgm", "reut2-003.sgm", 
			"reut2-004.sgm", "reut2-005.sgm", "reut2-006.sgm", "reut2-007.sgm", "reut2-008.sgm", 
			"reut2-009.sgm", "reut2-010.sgm", "reut2-011.sgm", "reut2-012.sgm", "reut2-013.sgm", 
			"reut2-014.sgm", "reut2-015.sgm", "reut2-016.sgm", "reut2-017.sgm", "reut2-018.sgm", 
			"reut2-019.sgm", "reut2-020.sgm", "reut2-021.sgm"};
	private RawDocument documentList;
	private File file;
	private Document doc;
	
}
