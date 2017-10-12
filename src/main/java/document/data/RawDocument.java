package document.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RawDocument implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Reuter> reuterList;

    public RawDocument(){}

    public List<Reuter> getReuterList() {
        return reuterList;
    }

    public void setReuterList(List<Reuter> reuterList) {
        this.reuterList = reuterList;
    }
    
    public void addReuter(Reuter reuter) {
    		if(this.reuterList == null) {
    			this.reuterList = new ArrayList<Reuter>();
    			this.reuterList.add(reuter);
    		}else {
    			this.reuterList.add(reuter);
    		}
    }
    
    public void appendReuterList(List<Reuter> reuters) {
    		if(this.reuterList == null) {
    			this.reuterList = reuters;
    		}else {
    			this.reuterList.addAll(reuters);
    		}
    }
}
