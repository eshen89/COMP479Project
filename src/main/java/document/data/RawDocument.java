package document.data;

import java.util.ArrayList;
import java.util.List;

public class RawDocument {

    private List<Reuters> reuterList;


    public List<Reuters> getReuterList() {
        return reuterList;
    }

    public void setReuterList(List<Reuters> reuterList) {
        this.reuterList = reuterList;
    }
    
    public void addReuter(Reuters reuter) {
    		if(this.reuterList == null) {
    			this.reuterList = new ArrayList<Reuters>();
    			this.reuterList.add(reuter);
    		}else {
    			this.reuterList.add(reuter);
    		}
    }
}
