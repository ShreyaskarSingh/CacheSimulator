
public class DS {
	
	String data, tag;
	int lastaccess;
	
	
	public DS(String data, String tag, int lastaccess, Boolean dirty) {
		super();
		this.data = data;
		this.tag = tag;
		this.lastaccess = lastaccess;
		this.dirty = dirty;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	Boolean dirty = true;
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getLastaccess() {
		return lastaccess;
	}
	public void setLastaccess(int lastaccess) {
		this.lastaccess = lastaccess;
	}
	public Boolean getDirty() {
		return dirty;
	}
	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}
	
}
