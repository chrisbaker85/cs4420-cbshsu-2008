import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;

public class IndexHelper {
	Hashtable<Integer, Long> bTree = null;
	String name;

	public IndexHelper() {
		name = null;
		bTree = null;
	}

	public IndexHelper(String db_name, String table_name) {
		name = db_name + "_" + table_name + "_index";
		bTree = new Hashtable<Integer, Long>();
		try {
			FileOutputStream os = new FileOutputStream(name + ".dat");
			XMLEncoder encoder = new XMLEncoder(os);
			encoder.writeObject(bTree);
			encoder.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Hashtable<Integer, Long> getBTree() {
		return bTree;
	}

	public void setBTree(Hashtable<Integer, Long> tree) {
		bTree = tree;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static IndexHelper read(String aName) {
		IndexHelper retVal = new IndexHelper();
		retVal.setName(aName);
		try {
			FileInputStream os = new FileInputStream(aName + ".Index");
			XMLDecoder decoder = new XMLDecoder(os);
			Hashtable<Integer, Long> tree = (Hashtable<Integer, Long>) decoder
					.readObject();
			retVal.setBTree(tree);
			decoder.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	public void insert(int key, long pointer) {
		bTree.put(new Integer(key), new Long(pointer));
	}

	public long get(int key) {
		return ((Long) bTree.get(new Integer(key))).longValue();
	}

	public void remove(int key) {
		bTree.remove(new Integer(key));
	}

	public boolean contains(int key) {
		return bTree.contains(new Integer(key));
	}

	public void update() {
		try {
			FileOutputStream os = new FileOutputStream(name + ".Index");
			XMLEncoder encoder = new XMLEncoder(os);
			encoder.writeObject(bTree);
			encoder.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		/* 
		IndexHelper helper = new IndexHelper("test");
		helper.insert(12, 15);
		helper.update();
		
		IndexHelper test = IndexHelper.read("test");
		System.out.println(test.get(12));
		*/
	}
}