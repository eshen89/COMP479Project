package document.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Reuters {

    private int docID;
	private String date;
    private String topics;
    private Text text;
    
	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public String getDate() {
		return date;
	}
	@XmlElement
	public void setDate(String date) {
		this.date = date;
	}
	public String getTopic() {
		return topics;
	}
	@XmlElement
	public void setTopic(String topic) {
		this.topics = topic;
	}
	public Text getText() {
		return text;
	}
	@XmlElement
	public void setText(Text text) {
		this.text = text;
	}

}
