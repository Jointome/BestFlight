
import java.util.LinkedList;


public class Node {
	int level,count;
	String attribute;
	LinkedList<Node> childs;
	String value;
	boolean isClass;

	
	
	public Node(){
		int level=count=0;
		attribute=null;
		childs=new LinkedList<Node>();
		value=null;
		isClass=false;
	}

	public Node(int level, String attribute, LinkedList<Node> childs, String value) {
		super();
		this.level = level;
		this.attribute = attribute;
		this.childs = childs;
		this.value = value;
		count=0;
		isClass=false;
	}

	public Node(Node n){
		super();
		this.setLevel(n.getLevel());
		this.setAttribute(n.getAttribute());
		this.setChilds(n.getChilds());
		this.setValue(n.getValue());
		this.setCount(n.getCount());
		this.setIsClass(n.isClass());
	}

	public Node(int level, String attribute, String value, int count, boolean isClass) {
		super();
		this.level = level;
		this.attribute = attribute;
		this.childs = new LinkedList<Node>();
		this.value = value;
		this.count=count;
		this.isClass=isClass;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public LinkedList<Node> getChilds() {
		return childs;
	}

	public void setChilds(LinkedList<Node> childs) {
		this.childs= new LinkedList<Node>(childs);
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value; 
	}

	public void setCount(int count){
		this.count=count;
	}

	public int getCount(){
		return count;
	}

	public void setIsClass(boolean isClass){
		this.isClass=isClass;
	}

	public boolean isClass(){
		return isClass;
	}
}
