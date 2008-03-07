import java.io.*;
import org.w3c.dom.Document;
import java.util.*;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sovandy Hang
 *
 */

public class Main implements QueryEngine
{
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	private SystemCatalog syscat;
	private BufferManager bufman;
	private boolean dbUsed = false;
	
	public Main()
	{
		bufman = new BufferManager(); 
		bufman.initialize();
	}
	
	public BufferManager getBm()
	{
		return this.bufman;
	}
	
	public void setBm(BufferManager bm)
	{
		this.bufman = bm;
	}
	
	public SystemCatalog getSysCat()
	{
		return this.syscat;
	}

	public void setSysCat(SystemCatalog obj)
	{
		this.syscat = obj;
	}
	
	public Hashtable readAttributes(String dbname, String table_name)
	{
		Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();
		String filename = dbname + "_" + table_name + ".xml";
		try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filename));

            doc.getDocumentElement ().normalize ();

			NodeList listOfRelations = doc.getElementsByTagName("attribute");
			int totalRelations = listOfRelations.getLength();

			for(int s=0; s< listOfRelations.getLength(); s++)
			{				
                Node firstRelationNode = listOfRelations.item(s);
                if(firstRelationNode.getNodeType() == Node.ELEMENT_NODE)
                {
					Element firstRelationElement = (Element)firstRelationNode;

					NodeList node = firstRelationElement.getElementsByTagName("name");
					Element firstNameElement = (Element)node.item(0);
					NodeList textFNList = firstNameElement.getChildNodes();
					String name = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("type");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String type = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("length");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String length = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("isnullable");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String isnullable = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("id");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String id = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("num_values");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String num_values = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					Attribute attObj = new Attribute(name, type, length, isnullable, table_name, id, num_values);
					attributes.put(name, attObj);
				}
			}
        }
        catch (SAXParseException err)
        {
        	System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
        	System.out.println(" " + err.getMessage ());
        }
        catch (SAXException e)
        {
        	Exception x = e.getException ();
        	((x == null) ? e : x).printStackTrace ();

        }
        catch (Throwable t)
        {
        	t.printStackTrace ();
        }
        return attributes;
	}
	/**
	 * It will read database relations XML file for database dbname. Then it will instantiate 
	 * RelationInfo object, add to hashtable in SystemCatalog. For each relation, it will read 
	 * attribute XML file. Then it will instantiate Attribute object, add it to hashtable in 
	 * SystemCatalog
	 * @param db_name: the name of the database
	 */
	public void readDBRelations(String db_name)
	{
		String filename = db_name + "_relations.xml";
		
    	try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filename));

            doc.getDocumentElement ().normalize ();

			NodeList listOfRelations = doc.getElementsByTagName("relation");
			int totalRelations = listOfRelations.getLength();

			for(int s=0; s< listOfRelations.getLength(); s++)
			{
                Node firstRelationNode = listOfRelations.item(s);
                if(firstRelationNode.getNodeType() == Node.ELEMENT_NODE)
                {
					Element firstRelationElement = (Element)firstRelationNode;

					NodeList node = firstRelationElement.getElementsByTagName("name");
					Element firstNameElement = (Element)node.item(0);
					NodeList textFNList = firstNameElement.getChildNodes();
					String table_name = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("date_created");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String dateCreated = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("date_modified");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String dateModified = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("num_tuple");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String numTuple = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("id");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String id = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("cols_indexed");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String colsIndexed = (String)((Node)textFNList.item(0)).getNodeValue().trim();

					node = firstRelationElement.getElementsByTagName("num_block");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					String numBlock = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					
					Hashtable attributes = readAttributes(db_name, table_name);
					RelationInfo relation = new RelationInfo(table_name, dateCreated, dateModified, numTuple, Integer.parseInt(id), colsIndexed, table_name + "_" + db_name + "index.dat", numBlock, attributes);
					this.syscat.addRelationCatalog(table_name, relation);
				}
			}
        }
        catch (SAXParseException err)
        {
        	System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
        	System.out.println(" " + err.getMessage ());
        }
        catch (SAXException e)
        {
        	Exception x = e.getException ();
        	((x == null) ? e : x).printStackTrace ();

        }
        catch (Throwable t)
        {
        	t.printStackTrace ();
        }
	}
	
	/**
	 * create a blank file for relation info
	 * @param db_name
	 */
	public void createDB(String db_name)
	{
		try {	
		File file = new File(db_name+"_relations.xml");
	    BufferedWriter output = new BufferedWriter(new FileWriter(file));
	    output.write("<relations>\n");
	    output.write("</relations>");
	    output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		// this.useDatabase(db_name);
	}
	
	/**
	 * When CREATED TABLE command is entered, it will open file relations file for that database
	 * Then, it append table info to the file. Then it create a file containing all the attribute
	 * info for that table.  
	 * @param table_name: name of table
	 * @param attributes: list of attributes for that table
	 * @param db_name: the name of the database
	 */
	public boolean createTable(String db_name, String table_name, String [][] attributes)
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String cur_date = (String)sdf.format(cal.getTime());
		String line = "";
        ArrayList<String> data = new ArrayList<String>();
        Hashtable<String, Attribute> atts = new Hashtable<String, Attribute>();
        int id = 0;
		try {	
			FileReader fr = new FileReader(db_name + "_relations.xml");
       		BufferedReader br = new BufferedReader(fr);	// Can also use a Scanner to read the file.
       		while((line = br.readLine()) != null)
       		{
       	 		data.add(line);
       		}
       		id = (data.size() - 2) / 7;
       		int ind = data.size() - 1;
			data.add(ind++, "<relation>\n");
			data.add(ind++, "<name>" + table_name + "</name>\n");
			data.add(ind++, "<date_created>" + cur_date + "</date_created>\n");
			data.add(ind++, "<date_modified>" + cur_date + "</date_modified>\n");
			data.add(ind++, "<num_tuple>0</num_tuple>\n");
			data.add(ind++, "<id>" + id + "</id>\n");
			data.add(ind++, "<cols_indexed>-1</cols_indexed>\n");
			data.add(ind++, "<num_block>1</num_block>\n");
      		data.add(ind++, "</relation>\n");
      		data.add(ind++, "</relations>");
      		
			File file = new File(db_name + "_relations.xml");
		    BufferedWriter output = new BufferedWriter(new FileWriter(file));
		    
		    for (int i = 0; i < data.size(); i++)
		    {
		    	output.write(data.get(i));
		    }
		    output.close();
		    
		    // write attributes for that relation into table
		    file = new File(db_name + "_" + table_name + ".xml");
			output = new BufferedWriter(new FileWriter(file));
			output.write("<attributes>\n");
			for (int i = 0; i < attributes.length; i++)
			{
				output.write("<attribute>\n");
				output.write("<name>" + attributes[i][0] + "</name>\n");
				output.write("<type>" + attributes[i][1] + "</type>\n");
				output.write("<length>" + attributes[i][2] + "</length>\n");
				output.write("<isnullable>" + attributes[i][3] + "</isnullable>\n");
				output.write("<relation_name>" + table_name + "</relation_name>\n");
				output.write("<id>" + i + "</id>\n");
				output.write("<num_values>0</num_values>\n");
				output.write("</attribute>\n");
				// how to get attribute id for the relation
				Attribute attObj = new Attribute(attributes[i][0], attributes[i][1], attributes[i][2], attributes[i][3], table_name, Integer.toString(i), "0");
				atts.put(attributes[i][0], attObj);
			}
			output.write("</attributes>");
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		RelationInfo relObj = new RelationInfo(table_name, cur_date, cur_date, "0", id, "-1", "", "1", atts);
		this.syscat.addRelationCatalog(table_name, relObj);
		// update filename Hashtable in BufferManager 
		this.bufman.getTableNames(syscat.getRelationCatalog());
		// create a blank data file
		try {
			File file = new File(db_name + "_" + table_name + "_data.dat");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
	    
		// create a blank block and insert it into buffer and file
		Block block = new Block();
		long blockID = Utility.combine(id, 0);
		block.setBlockID(blockID);
		bufman.addBlockToBuffer(block);
		bufman.writeBlock(blockID);
		block.setUpdated(false);
		
		return true;
	}
	
	/**
	 * 1. calculate offset of the last
		 * 2. combine file_id & offset into long
		 * 3. get that block (copy it to buffer)
		 * 4. test if that block has enough room for a tuple
		 * 5. if yes, writeToBlock()
		 * 6. else, instantiate Block object
		 * 7. insert that into BufferManager
		 * 8. writeToBlock()
		 * 9. write block to file
			 * 1. create a new block
			 * 2. add block to buffer
			 * 3. calculate offset
			 * 4. combine into blockID
			 */ 

	public boolean insertQuery(String table_name, String [][] query)
	{
		RelationInfo relObj = (RelationInfo)syscat.getRelationCatalog().get(table_name);
		Hashtable atts = relObj.getAttribute();
		
		for (int i = 0; i < query[1].length; i++)
		{
			// verify fields name against attribute list
			if (!atts.containsKey(query[0][i])) 
			{
				System.out.println("Attribute " + query[1][i] + " doesn't exist");
				return false;
			}
			// verify the length of string
			else 
			{
				Attribute att = (Attribute)atts.get(query[0][i]);
				if(att.getType().equals("string"))
				{
					if (query[1][i].length() > Integer.parseInt(att.getLength()))
					{
						System.out.println(query[1][i] + " exceeds maximum length");
						return false;
					}
				}
			}
		}
		// convert data to array of byte to write to the block and file
		byte [] dataToWrite = Utility.dataToByte(query[0], query[1], atts); 
		
		
		int blockNum = Integer.parseInt(relObj.getNumDataBlocks().trim());
		int lastOffset = Parameters.BLOCK_SIZE * (blockNum - 1);
		int fileId = relObj.getId();
		long blockID = Utility.combine(fileId, lastOffset);
		Block block = bufman.getBlock(blockID);
		
		int maxRecNum = Parameters.BLOCK_SIZE / Utility.getTotalLength(atts);
		int recNum = block.getRecordNumber();
		
		if (recNum < maxRecNum)
		{
			block.writeToBlock(dataToWrite);
			bufman.writeBlock(blockID);
			relObj.updateDateModified();
			relObj.updateTupleNumber(1);
			block.setUpdated(false);
		}
		else
		{
			block = new Block();
			block.writeToBlock(dataToWrite);
			bufman.addBlockToBuffer(block);
			lastOffset = Parameters.BLOCK_SIZE * blockNum;
			blockID = Utility.combine(fileId, lastOffset);
			block.writeToBlock(dataToWrite);
			bufman.writeBlock(blockID);
			relObj.updateDateModified();
			relObj.updateTupleNumber(1);
			block.setUpdated(false);
			relObj.updateBlockNumber(1);
		}
		this.writeSystemCataglog();
		return true;
	}
	
	/**
	 * Creates a new index (index_name) on a field (field_name) of a relation
	 * (table_name)
	 * @param index_name: name of the index to be created
	 * @param table_name: name of table
	 * @param field_name: the name of the field we're creating an index on
	 */
	public boolean createIndexQuery(String index_name, String table_name, String field_name, boolean duplicates) {
		
		// TODO: FIX ME
		return false;
		
	}
	
	/**
	 * Performs a selection on an index
	 * @param table_name the table
	 * @param index_name the index
	 */
	public boolean selectIndexQuery(String table_name, String index_name) 
	{
		return false;
		
	}
	
	/**
	 * Displays the catalog information
	 */
	public boolean selectCatalogQuery() 
	{	
		System.out.println("Database name:" + syscat.getDBName());
		Enumeration e = syscat.getRelationCatalog().elements();
		while(e.hasMoreElements())
		{
			RelationInfo relObj = (RelationInfo)e.nextElement();
			System.out.println("===============================================");
			System.out.println("Relation name:\t\t" + relObj.getName());
			System.out.println("Date created:\t\t" + relObj.getDateCreated());
			System.out.println("Date modified:\t\t" + relObj.getDateModified());
			System.out.println("Tuple numbers:\t\t" + relObj.getNumTuples());
			System.out.println("Block numbers:\t\t" + relObj.getNumDataBlocks());
			System.out.println("===============================================");
			Enumeration e1 = relObj.getAttribute().elements();
			while (e1.hasMoreElements())
			{
				System.out.println("");
				Attribute att = (Attribute)e1.nextElement();
				System.out.println("Name:\t\t" + att.getName());
				System.out.println("Type:\t\t" + att.getType());
				System.out.println("Length:\t\t" + att.getLength());
				System.out.println("Nullable:\t" + att.getIsNullable());
			}
		}
		return true;
	}
	
	public boolean selectQuery(String table_name, String [] fields, String [][] where)
	{
		
		RelationInfo relObj = (RelationInfo)syscat.getRelationCatalog().get(table_name);
		Hashtable atts = relObj.getAttribute();
		int tupleLength = Utility.getTotalLength(atts);
		boolean selectAll = false;
		// testing if the field entered by user exists
		if (fields != null)
		{
			for (int i = 0; i < fields.length; i++)
			{
				if (!atts.containsKey(fields[i]))
				{
					System.out.println("Attribute " + fields[i] + " doesn't exist");
					return false;
				}
			}
		}
		else selectAll = true;
		/**
		 * 1. Iterate tuple by tuple
		 * 2. parse it to 
		 */
		
		// System.out.println("creating Iterator");
		Iterator iterator = new Iterator(bufman, relObj, relObj.getId(), Integer.parseInt(relObj.getNumDataBlocks().trim()));
		Tuple tuple;
		for (int i = 0; i < Integer.parseInt(relObj.getNumTuples().trim()); i++)
		{
			tuple = iterator.getNext();
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] data = block.getTupleContent(offset, tupleLength);
			String [] results = Utility.convertTupleToArray(atts, data);
			// test condition before printing the results
			/**
			 * 1. get the array of attribute names
			 * 2. 
			 */
			String [] attNames = Utility.getAttributeNames(atts);
			for (int j = 0; j < attNames.length; j++)
			{
				System.out.println(attNames[j] + "\t\t");
			}
			System.out.println("==============================================");
			if (where != null)
			{
				
				boolean condition = true;
				for (int j=0; j < where.length; j++)
				{
					int ind = Utility.searchStringArray(where[j][0], attNames);
					condition = condition && (results[ind].equals(where[j][1]));
					if (condition)
					{
						for(int k = 0; k < results.length; k++)
						{
							System.out.print(results[k] + "\t\t");
						}
					}
				}
			}
			else
			{
				for(int j = 0; j < results.length; j++)
				{
					System.out.print(results[j] + "\t\t");
				}
			}
			System.out.println("");
		}
		return true;
	}
	
	/**
	 * select database that user want to work on
	 * @param db_name
	 */
	public void useDatabase(String db_name)
	{
		syscat = new SystemCatalog(db_name); 
		this.readDBRelations(db_name);
		bufman.setDBName(db_name);
		bufman.getTableNames(syscat.getRelationCatalog());
	}
	
	/**
	 * write system catalog back to file because some of info may have chanaged: 
	 *  - add more table
	 *  - change number of block 
	 */
	public void exit()
	{
		this.writeSystemCataglog();
		System.exit(0);
	}
	
	public void writeSystemCataglog()
	{
		Enumeration e = syscat.getRelationCatalog().elements();
		try {
			File file = new File(syscat.getDBName() + "_relations.xml");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("<relations>\n");
			while(e.hasMoreElements())
			{
				RelationInfo relObj = (RelationInfo) e.nextElement();
				output.write("<relation>\n");
				output.write("<name>" + relObj.getName() + "</name>\n");
				output.write("<date_created>" + relObj.getDateCreated() + "</date_created>\n");
				output.write("<date_modified>" + relObj.getDateModified() + "</date_modified>\n");
				output.write("<num_tuple>" + relObj.getNumTuples() + " </num_tuple>\n");
				output.write("<id>" + Integer.toString(relObj.getId()) + "</id>\n");
				output.write("<cols_indexed>" + relObj.getColsIndexed() + "</cols_indexed>\n");
				output.write("<num_block>" + relObj.getNumDataBlocks() + "</num_block>\n");
	      		output.write("</relation>\n");
			}
			output.write("</relations>");
			output.close();
		}
		catch(IOException err)
		{
			System.out.print(err.getMessage());
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	{
		Main mydb = new Main();
		String db_name = "db1";
		System.out.println("create database db1");
		mydb.createDB(db_name);
		System.out.println("Use db1");
		mydb.useDatabase(db_name);
		System.out.println("***********************************************");
		System.out.println("create table student");
		String [][] student_attributes = {{"first_name", "string", "20", "no", "0", "0"},
                					     {"last_name",   "string", "20", "no", "1", "0"},
                					     {"dob",         "string", "10", "no", "2", "2"}};
		mydb.createTable(db_name, "student", student_attributes);
		String [][] course_attributes = {{"course_name",   "string", "20", "no", "0", "0"},
                					{"course_number", "string", "10", "no", "1", "0"},
                					{"location",      "string", "10", "no", "2", "2"}};
		mydb.createTable(db_name, "course", course_attributes);
		String [][] insert_student = {{"first_name", "last_name", "dob"}, 
									  {"john", "smith", "01/01/2000"}};
		String [][] insert_student1 = {{"first_name", "last_name"},
				                       {"bill", "joe"}};
		System.out.println("**********************************************");
		mydb.insertQuery("student", insert_student);
		mydb.insertQuery("student", insert_student1);
		mydb.selectCatalogQuery();

	}
}

