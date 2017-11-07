package ir.data;

import java.io.Serializable;


/**
 * @author YangShen
 *
 *	This is POJO class for Reuter
 */
public class Reuter implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int docID;
	private String title;
	private String body;
   
	public Reuter(){}
	
	public Reuter(int docID, String title, String body) {
		this.docID = docID;
		this.title = title;
		this.body = body;
	}
	
	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

}
