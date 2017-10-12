package document.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RawDocument implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<List<Reuter>> reuterList;

    public RawDocument(){}

    public List<List<Reuter>> getReuterList() {
        return reuterList;
    }

    public void setReuterList(List<List<Reuter>> reuterList) {
        this.reuterList = reuterList;
    }
    
    public void addReuter(List<Reuter> reuter) {
    		if(this.reuterList == null) {
    			this.reuterList = new ArrayList<List<Reuter>>();
    			this.reuterList.add(reuter);
    		}else {
    			this.reuterList.add(reuter);
    		}
    }
}
