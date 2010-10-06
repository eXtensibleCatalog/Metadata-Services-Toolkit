package xc.mst.bo.record.marc;

import java.util.ArrayList;
import java.util.List;

public class Field {

	protected int tag = -1;
	protected int ind1 = -1;
	protected int ind2 = -1;
	
	protected String contents = null;
	protected List<Subfield> subfields = null;
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getInd1() {
		return ind1;
	}

	public void setInd1(int ind1) {
		this.ind1 = ind1;
	}

	public int getInd2() {
		return ind2;
	}

	public void setInd2(int ind2) {
		this.ind2 = ind2;
	}
	
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public List<Subfield> getSubfields() {
		if (subfields == null) {
			this.subfields = new ArrayList<Subfield>();
		}
		return subfields;
	}

	public void setSubfields(List<Subfield> subfields) {
		this.subfields = subfields;
	}
	
}
