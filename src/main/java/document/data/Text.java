package document.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Text {
	
	private String title;
	private String body;
	
	public String getTitle() {
		return title;
	}
	@XmlElement
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	@XmlElement
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
