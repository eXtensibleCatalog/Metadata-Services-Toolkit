package xc.mst.bo.record.marc;

import java.util.ArrayList;
import java.util.List;

public class Field {
	
	public static char NULL_CHAR = Character.MAX_HIGH_SURROGATE;

	protected int tag = -1;
	protected char ind1 = NULL_CHAR;
	protected char ind2 = NULL_CHAR;
	
	protected String contents = null;
	protected List<Subfield> subfields = null;
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public char getInd1() {
		return ind1;
	}

	public void setInd1(char ind1) {
		this.ind1 = ind1;
	}

	public char getInd2() {
		return ind2;
	}

	public void setInd2(char ind2) {
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
