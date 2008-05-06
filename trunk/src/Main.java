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
	private int lastID = 0;
	
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
	
	/**
	 * read indexs from xml file 
	 * @param dbname
	 * @param table_name
	 * @return
	 */
	private Hashtable readIndexs(String tableName)
	{
		Hashtable<String, IndexInfo> indexInfos = new Hashtable<String, IndexInfo>();
		String filename = syscat.getDBName() + "_" + tableName + "_index.xml";
		try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(filename));

            doc.getDocumentElement ().normalize ();

			NodeList listOfIndexs = doc.getElementsByTagName("index");
			
			for(int s=0; s < listOfIndexs.getLength(); s++)
			{				
                Node firstIndexNode = listOfIndexs.item(s);
                if(firstIndexNode.getNodeType() == Node.ELEMENT_NODE)
                {
					Element firstIndexElement = (Element)firstIndexNode;

					NodeList node = firstIndexElement.getElementsByTagName("index_name");
					Element firstElement = (Element)node.item(0);
					NodeList nodeList = firstElement.getChildNodes();
					String indexName = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					node = firstIndexElement.getElementsByTagName("att_name");
					firstElement = (Element)node.item(0);
					nodeList = firstElement.getChildNodes();
					String attName = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					node = firstIndexElement.getElementsByTagName("is_duplicate");
					firstElement = (Element)node.item(0);
					nodeList = firstElement.getChildNodes();
					String isDuplicate = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					IndexInfo indexInfo = new IndexInfo(indexName, attName, isDuplicate.equals("yes"));
					indexInfos.put(attName, indexInfo);
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
		return indexInfos;
	}
	
	/**
	 * read attributes from xml file that belong to relation tablename 
	 * @param dbname name of database 
	 * @param tablename name of relation 
	 * @return
	 */
	private Hashtable readAttributes(String tableName)
	{
		Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();
		String filename = syscat.getDBName() + "_" + tableName + ".xml";
		try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filename));

            doc.getDocumentElement ().normalize ();

			NodeList listOfAttributes = doc.getElementsByTagName("attribute");

			for(int s=0; s < listOfAttributes.getLength(); s++)
			{				
                Node firstAttributeNode = listOfAttributes.item(s);
                if(firstAttributeNode.getNodeType() == Node.ELEMENT_NODE)
                {
					Element firstAttributeElement = (Element)firstAttributeNode;

					NodeList node = firstAttributeElement.getElementsByTagName("name");
					Element firstElement = (Element)node.item(0);
					NodeList nodeList = firstElement.getChildNodes();
					String name = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					node = firstAttributeElement.getElementsByTagName("type");
					firstElement = (Element)node.item(0);
					nodeList = firstElement.getChildNodes();
					String type = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					node = firstAttributeElement.getElementsByTagName("length");
					firstElement = (Element)node.item(0);
					nodeList = firstElement.getChildNodes();
					String length = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					node = firstAttributeElement.getElementsByTagName("isnullable");
					firstElement = (Element)node.item(0);
					nodeList = firstElement.getChildNodes();
					String isnullable = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					node = firstAttributeElement.getElementsByTagName("id");
					firstElement = (Element)node.item(0);
					nodeList = firstElement.getChildNodes();
					String id = (String)((Node)nodeList.item(0)).getNodeValue().trim();

					node = firstAttributeElement.getElementsByTagName("num_values");
					firstElement = (Element)node.item(0);
					nodeList = firstElement.getChildNodes();
					String num_values = (String)((Node)nodeList.item(0)).getNodeValue().trim();
					
					Attribute attObj = new Attribute(name, type, length, isnullable, tableName, id, Integer.parseInt(num_values));
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
	public void readDBRelations()
	{
		String filename = syscat.getDBName() + "_relations.xml";
		
    	try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filename));
            doc.getDocumentElement ().normalize ();
			NodeList listOfRelations = doc.getElementsByTagName("relation");
			
			for(int s=0; s< listOfRelations.getLength(); s++)
			{
                Node firstRelationNode = listOfRelations.item(s);
                if(firstRelationNode.getNodeType() == Node.ELEMENT_NODE)
                {
					Element firstRelationElement = (Element)firstRelationNode;

					NodeList node = firstRelationElement.getElementsByTagName("name");
					Element firstNameElement = (Element)node.item(0);
					NodeList textFNList = firstNameElement.getChildNodes();
					String tableName = (String)((Node)textFNList.item(0)).getNodeValue().trim();

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
					
					Hashtable attributes = readAttributes(tableName);
					RelationInfo relation = new RelationInfo(tableName, dateCreated, dateModified, numTuple, Integer.parseInt(id), Integer.parseInt(colsIndexed), tableName + "_" + syscat.getDBName() + "index.dat", numBlock, attributes);
					// test to see if there is index for that relation, set indexinfo
					if (!colsIndexed.equals("0"))
					{
						Hashtable indexInfos = this.readIndexs(tableName);
						relation.setIndexInfos(indexInfos);
					}
					this.syscat.addRelationCatalog(tableName, relation);
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
	 * @param dbname
	 */
	public void createDB(String dbName)
	{
		try {	
		File file = new File(dbName + "_relations.xml");
	    BufferedWriter output = new BufferedWriter(new FileWriter(file));
	    output.write("<relations>\n");
	    output.write("</relations>");
	    output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		this.useDatabase(dbName);
	}
	
	/**
	 * When CREATED TABLE command is entered, it will open file relations file for that database
	 * Then, it append table info to the file. Then it create a file containing all the attribute
	 * info for that table.  
	 * @param tablename: name of table
	 * @param attributes: list of attributes for that table
	 * @param dbname: the name of the database
	 */
	public boolean createTable(String tableName, String [][] attributes, boolean isTempRelation)
	{
		if (syscat.getRelationCatalog().get(tableName) != null) return false;
		
		// get current date
		Calendar cal = Calendar.getInstance();
		// set date format
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		// get current date
		String cur_date = (String)sdf.format(cal.getTime());
		String line = "";
		// temp array for xml data
        ArrayList<String> data = new ArrayList<String>();
 
        Hashtable<String, Attribute> atts = new Hashtable<String, Attribute>();
        int id = 0;
		try {
			
			FileReader fr = new FileReader(this.syscat.getDBName() + "_relations.xml");
       		BufferedReader br = new BufferedReader(fr);	// Can also use a Scanner to read the file.
       		while((line = br.readLine()) != null)
       		{
       	 		data.add(line);
       		}
       		// id = (data.size() - 2) / 7;
       		id = lastID;
       		lastID++;
       		
			// if it's not temporary relation, write to XML files
			if (!isTempRelation)
			{
				// System.out.println();
	       		int ind = data.size() - 1;
				data.add(ind++, "<relation>\n");
				data.add(ind++, "<name>" + tableName + "</name>\n");
				data.add(ind++, "<date_created>" + cur_date + "</date_created>\n");
				data.add(ind++, "<date_modified>" + cur_date + "</date_modified>\n");
				data.add(ind++, "<num_tuple>0</num_tuple>\n");
				data.add(ind++, "<id>" + id + "</id>\n");
				data.add(ind++, "<cols_indexed>0</cols_indexed>\n");
				data.add(ind++, "<num_block>1</num_block>\n");
	      		data.add(ind++, "</relation>\n");
	      		//data.add(ind++, "</relations>");
	      		
				File file = new File(syscat.getDBName() + "_relations.xml");
			    BufferedWriter output = new BufferedWriter(new FileWriter(file));
			    
			    for (int i = 0; i < data.size(); i++)
			    {
			    	output.write(data.get(i));
			    }
			    output.close();
			    
			    // write attributes for that relation into table
			    file = new File(syscat.getDBName() + "_" + tableName + ".xml");
				output = new BufferedWriter(new FileWriter(file));
				output.write("<attributes>\n");
				for (int i = 0; i < attributes.length; i++)
				{
					output.write("<attribute>\n");
					output.write("<name>" + attributes[i][0] + "</name>\n");
					output.write("<type>" + attributes[i][1] + "</type>\n");
					output.write("<length>" + attributes[i][2] + "</length>\n");
					output.write("<isnullable>" + attributes[i][3] + "</isnullable>\n");
					output.write("<relation_name>" + tableName + "</relation_name>\n");
					output.write("<id>" + i + "</id>\n");
					output.write("<num_values>0</num_values>\n");
					output.write("</attribute>\n");
					// how to get attribute id for the relation
					Attribute attObj = new Attribute(attributes[i][0], attributes[i][1], attributes[i][2], attributes[i][3], tableName, Integer.toString(i), 0);
					atts.put(attributes[i][0], attObj);
				}
				output.write("</attributes>");
				output.close();
			}
			// if it's temporary solution, get atts
			else
			{
				for (int i = 0; i < attributes.length; i++)
				{
					Attribute attObj = new Attribute(attributes[i][0], attributes[i][1], attributes[i][2], attributes[i][3], tableName, Integer.toString(i), 0);
					atts.put(attributes[i][0], attObj);
				}
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		RelationInfo relObj = new RelationInfo(tableName, cur_date, cur_date, "0", id, 0, "", "1", atts);
		
		if (!isTempRelation) this.syscat.addRelationCatalog(tableName, relObj);
		else this.syscat.addTempRelation(tableName, relObj);
		// update filename Hashtable in BufferManager 
		this.bufman.getTableNames(syscat.getRelationCatalog());
		
		// create a blank data file
		try {
			File file = new File(syscat.getDBName() + "_" + tableName + "_data.dat");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
	    
		// create a blank index xml file
		try {
			File file = new File(syscat.getDBName() + "_" + tableName + "_index.xml");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("<indexs>\n");
		    output.write("</indexs>");
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		
		// create a blank block and insert it into buffer and file
		long blockID = Utility.combine(id, 0);
		Block block = new Block(blockID, "".getBytes());
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

	public boolean insertQuery(String tablename, String [][] query)
	{
		RelationInfo relObj;
		if (syscat.getRelationCatalog().containsKey(tablename))
		{
			relObj = (RelationInfo)syscat.getRelationCatalog().get(tablename);
		}
		else relObj = (RelationInfo)syscat.getTempRelation().get(tablename);
		
		if (relObj == null) {
		    System.out.println("ERROR: Table does not exist");
		    return false;
		}
		
		//System.out.println("INFO: num tuples in table: " + relObj.getNumTuples());
		
		Hashtable atts = relObj.getAttributes();
		
		for (int i = 0; i < query[1].length; i++)
		{
			// verify fields name against attribute list
			if (!atts.containsKey(query[0][i])) 
			{
				System.out.println("Attribute " + query[0][i] + " doesn't exist");
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
		
		// Before insert record, we need to check to see if there is index and we need to verify duplicate constraints
		if (relObj.getColsIndexed() > 0)
		{
			// get index info hashtable
			Hashtable indexInfos = relObj.getIndexInfos();
		
			// loop through all the inserted attributes and search if they are indexes
			for (int i = 0; i < query[0].length; i++)
			{
				// if inserted attribute is an index
				if (indexInfos.containsKey(query[0][i]))
				{
					IndexInfo indexInfo = (IndexInfo)relObj.getIndexInfos().get(query[0][i]);
					TreeMap index = indexInfo.getIndex();
					int key = Integer.parseInt(query[1][i]);
					if (index.containsKey(key) && !indexInfo.getIsDuplicate())
					{
						System.out.println(key + " already exists in index. Duplicate is not allowed");
						return false;
					}
				}
			}
		}
		
		// convert data to array of byte to write to the block and file
		byte [] dataToWrite = Utility.dataToByte(query[0], query[1], atts); 
		
		// The length of the attributes in bytes
	    int attLength = Utility.getTotalLength(atts);
	    
	    // The length of a tuple in bytes
	    int tupLength = attLength + Parameters.TUPLE_HEADER_SIZE;
	    
		// The block to write the data to (the last block)
		int blockNum = Integer.parseInt(relObj.getNumDataBlocks().trim());
		//System.out.println("INFO: blockNum:" + blockNum);
		int lastOffset = Parameters.BLOCK_SIZE * (blockNum - 1);
		
		// Using relation ID for file ID
		int fileId = relObj.getId();
		long blockID = Utility.combine(fileId, lastOffset);
		//System.out.println("INFO: fileId: " + fileId + "/Offset: " + lastOffset);
		
		Block block = bufman.getBlock(blockID);
		
		int maxRecNum = (Parameters.BLOCK_SIZE - Parameters.BLOCK_HEADER_SIZE) / attLength;
		int recNum = block.getRecordNumber();
		//System.out.println("INFO: RecordNumber: " + block.getRecordNumber());		
		//System.out.println("MAX: " + maxRecNum + "/ACT:" + recNum);
		
		if (recNum < maxRecNum)
		{
			//System.out.println("using old block");
			block.writeToBlock(dataToWrite);
			//System.out.println(block.blockID);
			bufman.writeBlock(blockID);
			relObj.updateDateModified();
			relObj.updateTupleNumber(1);
			block.setUpdated(false);
		}
		else
		{
			//System.out.println("making new block");

			lastOffset = Parameters.BLOCK_SIZE * blockNum;
			blockID = Utility.combine(fileId, lastOffset);
			
			// Make the block, give it ID and data
			block = new Block(blockID, dataToWrite);
			bufman.addBlockToBuffer(block);
			bufman.writeBlock(blockID);
			relObj.updateDateModified();
			
			// Increment the number of tuples in this relation
			relObj.updateTupleNumber(1);
			
			// Mark as not "dirty"
			block.setUpdated(false);
			
			// Increment the number of blocks holding this relation
			relObj.updateBlockNumber(1);
		}
		
		// test if there is index, insert index
		//System.out.println("Searching for index in table");
		if (relObj.getColsIndexed() > 0)
		{
			// get index info hashtable
			Hashtable indexInfos = relObj.getIndexInfos();
		
			// loop through all the inserted attributes and search if they are indexes
			for (int i = 0; i < query[0].length; i++)
			{
				if (indexInfos.containsKey(query[0][i]))
				{
					//if (Debug.get().debug()) System.out.println("Found index " + query[0][i]);
					//System.out.println("Found index " + query[0][i]);
					IndexInfo indexInfo = (IndexInfo)relObj.getIndexInfos().get(query[0][i]);
					TreeMap index = indexInfo.getIndex();
					int key = Integer.parseInt(query[1][i]);
					
					// TODO: convert back to as value of index
					//index.put(key, lastOffset);
					
					if (index.containsKey(key))
					{
						// if (Debug.get().debug()) 
						//System.out.println("Key exists and append to array list " + key);
						// add to array list of offset
						ArrayList<Integer> offsets = (ArrayList)index.get(key);
						// update array list of offset for that key
						offsets.add(lastOffset);
					}
					else
					{
						// if (Debug.get().debug()) 
						//System.out.println("Key doesn't exist " + key);
						ArrayList<Integer> offset = new ArrayList<Integer>();
						offset.add(lastOffset);
						// insert key and values into index
					    index.put(key, offset);
					}
					
					// write index to file
					//System.out.println("About to write key: " + key + "to file");
					try {
						File file = new File(tablename + "_" + indexInfo.getIdexName() + "_index.txt");
						FileOutputStream output = new FileOutputStream(file, true);
						String data = key + "\t" + lastOffset + "\n";
						output.write(data.getBytes());
						output.close();
					}
					catch (IOException e)
					{
						
					}
				}
				else
				{
					//System.out.println("Inserted field is not in index " + query[0][i]);
				}
			}
		}
		
		// update distinct values for each inserted attributes
		for(int i = 0; i < atts.size(); i++)
		{
			Attribute att = (Attribute)relObj.getAttributes().get(query[0][i]);
			att.updateDistinctValues(query[1][i]);
		}
		
		this.writeSystemCataglog();
		return true;
	}
	
	/**
	 * @param index_name: name of the index to be created
	 * @param tablename: name of table
	 * @param field_name: the name of the field we're creating an index on
	 */
	public boolean createIndexQuery(String indexName, String tableName, String attName, boolean isDuplicate) 
	{
		// boolean
		RelationInfo relInfo = null;
		if (syscat.getRelationCatalog().containsKey(tableName))
		{
			relInfo = (RelationInfo)syscat.getRelationCatalog().get(tableName);
		}
		else
		{
			relInfo = (RelationInfo)syscat.getTempRelation().get(tableName);
		}
		// check if index already exists
		if (relInfo.getIndexInfos().containsKey(attName))
		{
			System.out.println("Index for field " + attName + " already exists.");
			return false;
		}
		
		IndexInfo indexInfo = new IndexInfo(indexName, attName, isDuplicate);
		relInfo.getIndexInfos().put(attName, indexInfo);
		// increment number of index by 1
		relInfo.updateIndexNumber(1);
		ArrayList<String> data = new ArrayList<String>();
		String line;
		try {	
			FileReader fr = new FileReader(this.syscat.getDBName() + "_" + tableName + "_index.xml");
       		BufferedReader br = new BufferedReader(fr);	// Can also use a Scanner to read the file.
       		while((line = br.readLine()) != null)
       		{
       	 		data.add(line);
       		}
       		
	       	int ind = data.size() - 1;
	       
	       	data.add(ind++, "<index>\n");
	       	data.add(ind++, "<index_name>" + indexName + "</index_name>\n");
	       	data.add(ind++, "<att_name>" + attName + "</att_name>\n");
	       	if (isDuplicate) data.add(ind++, "<is_duplicate>yes</is_duplicate>\n");
	       	else data.add(ind++, "<is_duplicate>no</is_duplicate>\n");
	       	data.add(ind++, "</index>\n");
	       	// data.add(ind++, "</indexs>");
	       	
	       	File file = new File(this.syscat.getDBName() + "_" + tableName + "_index.xml");
		    BufferedWriter output = new BufferedWriter(new FileWriter(file));
		    
		    for (int i = 0; i < data.size(); i++)
		    {
		    	output.write(data.get(i));
		    }
		    //output.write("</indexs>");
		    output.close();
		    
	       	// create a blank file to store index
			File blank_file = new File(tableName + "_" + indexName + "_index.txt");
		    BufferedWriter blank_output = new BufferedWriter(new FileWriter(blank_file));
		    blank_output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		return true;
	}
	
	/**
	 * Performs a selection on an index
	 * @param tableName the table
	 * @param index_name the index
	 */
	public boolean selectIndexQuery(String tableName, String indexName) 
	{
		System.out.println("Printing index " + indexName + " for table " + tableName);
		RelationInfo rel = (RelationInfo)syscat.getRelationCatalog().get(tableName);
		Hashtable indexInfos = rel.getIndexInfos();
		TreeMap index = null;
		Enumeration e = indexInfos.elements();
		while (e.hasMoreElements())
		{
			IndexInfo indexInfo = (IndexInfo)e.nextElement();
			if (indexInfo.getIdexName().equals(indexName))
			{
				index = indexInfo.getIndex();
			}
		}
		if (index == null) 
		{
			System.out.println("Index named " + indexName + " doesn't exists.");
			return false;
		}
		
		int lastKey = (Integer)index.lastKey();
		SortedMap sm = index.headMap(lastKey);
		System.out.println("Number of key " + sm.size());
		System.out.println();
		System.out.println("Key\t\tBlock offset");
		System.out.println("===========================================");
		
		for (int i = 0; i < sm.size(); i++)
		{
			int key = (Integer)sm.firstKey();
			ArrayList<Integer> values = (ArrayList)sm.get(key);
			for (int j = 0; j < values.size(); j++)
			{
				System.out.println(key + "\t\t" + values.get(j));
			}
			sm.remove(key);
		}
		return true;
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
			System.out.println("=====================================================");
			System.out.println("Relation name:\t\t" + relObj.getName());
			System.out.println("Date created:\t\t" + relObj.getDateCreated());
			System.out.println("Date modified:\t\t" + relObj.getDateModified());
			System.out.println("Tuple numbers:\t\t" + relObj.getNumTuples());
			System.out.println("Block numbers:\t\t" + relObj.getNumDataBlocks());
			System.out.println("=====================================================");
	
			Enumeration e1 = relObj.getAttribute().elements();
			System.out.println("*************** Attribute Information ***************");
			while (e1.hasMoreElements())
			{
				System.out.println("");
				Attribute att = (Attribute)e1.nextElement();
				System.out.println("Name:\t\t" + att.getName());
				System.out.println("Type:\t\t" + att.getType());
				System.out.println("Length:\t\t" + att.getLength());
				System.out.println("Nullable:\t" + att.getIsNullable());
				System.out.println("Numbe of distinct value " + att.getDistinctValues());
			}
			Enumeration e2 = relObj.getIndexInfos().elements();
			System.out.println("**************** Index Information *****************");
			while (e2.hasMoreElements())
			{
				System.out.println("");
				IndexInfo index = (IndexInfo)e2.nextElement();
				System.out.println("Index name:\t\t" + index.getIdexName());
				System.out.println("Attriute name:\t\t" + index.getAttName());
				if (index.getIsDuplicate()) System.out.println("Duplicate:\t\tYes");
				else System.out.println("Duplicate:\t\tNo");
				System.out.println("Number of keys " + index.getIndex().size());
			}
		}
		return true;
	}
	
	private void printOutRelation(RelationInfo R)
	{
		Iterator iterator = new Iterator(bufman, R, Integer.parseInt(R.getNumDataBlocks().trim()));
		Hashtable atts = R.getAttributes();
		int tupleSize = Utility.getTotalLength(atts);
		Tuple tuple;
		String [] attNames = Utility.getAttributeNames(atts);
		for (int j = 0; j < attNames.length; j++)
		{
			System.out.print(attNames[j] + "\t");
		}
		System.out.println("");
		System.out.println("==============================================");
		
		for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
		{
			if (Debug.get().debug()) System.out.println("INFO: (main) index " + i);
			tuple = iterator.getNext();
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] data = block.getTupleContent(offset, tupleSize);
			String [] results = Utility.convertTupleToArray(atts, data);
			for (int j = 0; j < attNames.length; j++)
			{
				System.out.print(results[j] + "\t");
			}
			System.out.println("");
		}
	}
	
	public boolean selectQuery(String[] tableNames, String [] fields, String [][] where)
	{
		OpTree ot = new OpTree(this.syscat, tableNames, fields, where);
		int opNumber = ot.getNumOps();
		Op op = ot.nextOp();
		Op currentOp = op; 
		int lastIndex;
		if (op == null) System.out.println("Tree is null");
		while(op != null)
		{
			currentOp = op;
			lastIndex = op.getID();
			if (op instanceof OpSelect)
			{
				if (Debug.get().debug()) System.out.println("Calling select");
				
				// call select class here
				// RelationInfo R = op.getInfo();
				if (Debug.get().debug()) System.out.println("INFO: In select");
				RelationInfo R = op.left().getInfo();
				String [] conditions = (String [])op.getContents();
				
				// figure out if it's index or not
				boolean hasIndex = false;
				int indexPos = 0;
				RelationInfo result = null;
				if (R.getColsIndexed() > 0)
				{
					//for (int i = 0; i < conditions.length; i++)
					//{
						// loop through indexes to find the match
						if (R.getIndexInfos().containsKey(conditions[0])) 
						{
							//indexPos = i;
							hasIndex = true;
							break;
						}
					//}
				}
				if (hasIndex)
				{
					System.out.println("It's index-based select and sort-based filter");
					Select myselect = new Select(this, R, conditions, true);
					//RelationInfo temp = myselect.open();
					//conditions[indexPos] = null;
					// Filter myfilter = new Filter(this, temp, conditions);
					result = myselect.open();
				}
				else
				{
					Select myselect = new Select(this, R, conditions, false);
					//RelationInfo temp = myselect.open();
					//conditions[indexPos] = null;
					// Filter myfilter = new Filter(this, temp, conditions);
					result = myselect.open();
				}
				// THE COMMENTED FOLLOWING CODE IS FOR BUNDLED SELECT
//				else if (hasIndex && conditions.length == 1)
//				{
//					System.out.println("It's sort-based select");
//					Select myselect = new Select(this, R, conditions[0], true);
//					result = myselect.open();
//				}
//				else if (!hasIndex && conditions.length > 1)
//				{
//					System.out.println("It's sort-based filter");
//					Filter myfilter = new Filter(this, R, conditions);
//					result = myfilter.open();
//				}
//				else if (!hasIndex && conditions.length == 1)
//				{
//					System.out.println("It's sort-based select");
//					Select myselect = new Select(this, R, conditions[0], true);
//					result = myselect.open();
//				}
				
				op.info = result;
				//optable.put(new Integer(op.getID()), op);
				
				/* PRINTING OUT TABLE 
				Iterator iterator = new Iterator(bufman, result, Integer.parseInt(result.getNumDataBlocks().trim()));
				Hashtable atts = result.getAttributes();
				int tupleSize = Utility.getTotalLength(atts);
				Tuple tuple;
				String [] attNames = Utility.getAttributeNames(atts);
				for (int j = 0; j < attNames.length; j++)
				{
					System.out.print(attNames[j] + "\t");
				}
				System.out.println("");
				System.out.println("==============================================");
				
				for (int i = 0; i < Integer.parseInt(result.getNumTuples().trim()); i++)
				{
					if (Debug.get().debug()) System.out.println("INFO: (main) index " + i);
					tuple = iterator.getNext();
					Block block = tuple.getBlock();
					int offset = tuple.getOffset();
					byte [] data = block.getTupleContent(offset, tupleSize);
					String [] results = Utility.convertTupleToArray(atts, data);
					for (int j = 0; j < attNames.length; j++)
					{
						System.out.print(results[j] + "\t");
					}
					System.out.println("");
				}
				*/ 
			}
			else if (op instanceof OpProject)
			{
				//if (Debug.get().debug()) System.out.println("Calling project");
				
				if (Debug.get().debug()) System.out.println("INFO: entered project");
				
				// RelationInfo R = op.getInfo();
				RelationInfo R = op.left().getInfo();
				if (Debug.get().debug()) System.out.println("TYPE: " + op.getType() + "/CHILDTYPE" + op.left().getType() + "/INFO" + op.left().getInfo());
				String [] attList = (String [])op.getContents();
				
				/* PRINTING OUT TABLE
				Iterator iterator = new Iterator(bufman, R, Integer.parseInt(R.getNumDataBlocks().trim()));
				Hashtable atts = R.getAttributes();
				int tupleSize = Utility.getTotalLength(atts);
				Tuple tuple;
				String [] attNames = Utility.getAttributeNames(atts);
				for (int j = 0; j < attNames.length; j++)
				{
					System.out.print(attNames[j] + "\t");
				}
				System.out.println("");
				System.out.println("==============================================");
				
				for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
				{
					if (Debug.get().debug()) System.out.println("INFO: (main) index " + i);
					tuple = iterator.getNext();
					Block block = tuple.getBlock();
					int offset = tuple.getOffset();
					byte [] data = block.getTupleContent(offset, tupleSize);
					String [] results = Utility.convertTupleToArray(atts, data);
					for (int j = 0; j < attNames.length; j++)
					{
						System.out.print(results[j] + "\t");
					}
					System.out.println("");
				}
				*/
				
				// call project class here
				Project myproject = new Project(this, R, attList);
				RelationInfo result = myproject.open();
				op.info = result;
				
				//optable.put(new Integer(op.getID()), op);
				 
				 
			}
			else if (op instanceof OpCrossProduct)
			{
				/**
				 * Optimization plan: figure out the order of loop
				 * 1. Check the numbers of tuples in both relations
				 * 2. Outer loop: relation    
				 */
			    System.out.println("INFO: xprod ri: " + op.getInfo());
			    // get relations R and S
				Op leftOp = op.left();
				RelationInfo leftR = leftOp.getInfo();
				Op rightOp = op.right();
				RelationInfo rightR = rightOp.getInfo();
				// find out which one is inner loop, and outer loop, which one has index, number of distinct value
				CrossProduct mycp = null;
				if (Integer.parseInt(leftR.getNumTuples()) > Integer.parseInt(rightR.getNumTuples()))
				{
					mycp = new CrossProduct(this, leftR, rightR);
				}
				else 
				{
					mycp = new CrossProduct(this, rightR, leftR);
				}
				RelationInfo result = mycp.open();
				op.info = result;
				
			}
			else if (op instanceof OpJoin)
			{
				/**
				 * check to see if there is index
				 * 1. if one of them has index, it's inner loop
				 * 2. if both has index, one
				 * 3. If both don't have index
				 */
				// get relations R and S
				Op leftOp = op.left();
				RelationInfo leftR = leftOp.getInfo();
				Op rightOp = op.right();
				RelationInfo rightR = rightOp.getInfo();
				String [] condition = (String [])op.getContents();
				// find out which one is inner loop, and outer loop, which one has index, number of distinct value
				Join myjoin = null;
				// they both have indexes
				if (leftR.getIndexInfos().containsKey(Utility.getField(condition[0])) && rightR.getIndexInfos().containsKey(Utility.getField(condition[1])))
				{
					// check the number of tuple in each relation
					if (Integer.parseInt(leftR.getNumTuples()) > Integer.parseInt(rightR.getNumTuples()))
					{
						myjoin = new Join(this, leftR, rightR, condition, true, 1);
					}
					else
					{
						myjoin = new Join(this, rightR, leftR, condition, true, 1);
					}
				}
				// if left has index
				else if (leftR.getIndexInfos().containsKey(Utility.getField(condition[0])))
				{
					myjoin = new Join(this, rightR, leftR, condition, true, 1);
				}
				// if right has index
				else if (rightR.getIndexInfos().containsKey(Utility.getField(condition[1])))
				{
					myjoin = new Join(this, leftR, rightR, condition, true, 1);
				}
				// if both has no index
				else 
				{
					// check the number of tuple in each relation
					if (Integer.parseInt(leftR.getNumTuples()) > Integer.parseInt(rightR.getNumTuples()))
					{
						myjoin = new Join(this, leftR, rightR, condition, false, 1);
					}
					else
					{
						myjoin = new Join(this, rightR, leftR, condition, false, 1);
					}
				}
				RelationInfo result = myjoin.open();
				op.info = result;
			}
			op = ot.nextOp();
		}
		// print out results in currentOp here

		/*
		if (Debug.get().debug()) System.out.println("INFO: printing out results");
		RelationInfo relObj = currentOp.getInfo();
		
		System.out.println("--------------------------------------");
		System.out.println("Printing out relation " + relObj.getName());
		System.out.println("Number of tuple " + relObj.getNumTuples());
		System.out.println("--------------------------------------");
		
		Hashtable atts = relObj.getAttribute();
		int tupleSize = Utility.getTotalLength(atts);
		Iterator iterator = new Iterator(bufman, relObj, Integer.parseInt(relObj.getNumDataBlocks().trim()));
		Tuple tuple;
		String [] attNames = Utility.getAttributeNames(atts);
		for (int j = 0; j < attNames.length; j++)
		{
			System.out.print(attNames[j] + "\t");
		}
		System.out.println("");
		System.out.println("==============================================");
		
//		while((tuple = iterator.getNext()) != null) {
//			
//			tuple = iterator.getNext();
//			Block block = tuple.getBlock();
//			int offset = tuple.getOffset();
//			byte [] data = block.getTupleContent(offset, tupleSize);
//			String [] results = Utility.convertTupleToArray(atts, data);
//			for (int j = 0; j < attNames.length; j++)
//			{
//				System.out.print(results[j]);
//			}
//			System.out.println("");
//			
//		}
		
		if (Debug.get().debug()) System.out.println("INFO: (main) tuples: " + Integer.parseInt(relObj.getNumTuples().trim()));
		
		for (int i = 0; i < Integer.parseInt(relObj.getNumTuples().trim()); i++)
		{
			if (Debug.get().debug()) System.out.println("INFO: (main) index " + i);
			tuple = iterator.getNext();
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] data = block.getTupleContent(offset, tupleSize);
			String [] results = Utility.convertTupleToArray(atts, data);
			for (int j = 0; j < attNames.length; j++)
			{
				System.out.print(results[j] + "\t");
			}
			System.out.println("");
		}
		
		// clear temp relation in system catalog
		syscat.getTempRelation().clear();
		*/
		return true;
	}
	
	/**
	 * select database that user want to work on
	 * @param dbname
	 */
	public void useDatabase(String dbName)
	{
		syscat = new SystemCatalog(dbName); 
		this.readDBRelations();
		bufman.setDBName(dbName);
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
		//System.exit(0);
	}
	
	void writeAttribute(RelationInfo relInfo)
	{
		Enumeration e = relInfo.getAttributes().elements();
		try {
			// write relations xml files
			File file = new File(syscat.getDBName() + "_" + relInfo.getName() + ".xml");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("<attributes>\n");
			while(e.hasMoreElements())
			{
				Attribute att = (Attribute)e.nextElement();
				output.write("<attribute>\n");
				output.write("<name>" + att.getName() + "</name>\n");
				output.write("<type>" + att.getType() + "</type>\n");
				output.write("<length>" + att.getLength() + "</length>\n");
				output.write("<isnullable>" + att.getIsNullable() + "</isnullable>\n");
				output.write("<relation_name>" + relInfo.getName() + "</relation_name>\n");
				output.write("<id>" + att.getId() + "</id>\n");
				output.write("<num_values>" + att.getNumValues() + "</num_values>\n");
	      		output.write("</attribute>\n");
			}
			output.write("</attributes>");
			output.close();
		}
		catch(IOException err)
		{
			System.out.print(err.getMessage());
		}
	}
	
	void writeSystemCataglog()
	{
		Enumeration e = syscat.getRelationCatalog().elements();
		try {
			// write relations xml files
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
	      		
	      		// write attribute to file to update number of distinct values
	      		this.writeAttribute(relObj);
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
		// start main object
		Main mydb = new Main();
		
		String dbName = "db1";
		System.out.println("create database db1");
		// create database db1
		mydb.createDB(dbName);
		System.out.println("Use db1");
		// use database
		mydb.useDatabase(dbName);
		System.out.println("***********************************************");
		// create table student
		System.out.println("create table student");
		
		// create table student with first_name, last_name, and dob attributes
		String [][] student_attributes = {{"student_id", "int", "4", "no", "no", "0", "0"},
											{"first_name", "string", "20", "no", "1", "0"},
											{"last_name", "string", "20", "no", "2", "0"},
											{"dob", "string", "10", "no", "2", "3"}};
		mydb.createTable("student", student_attributes, false);
		
		// create another table with coure_name, course_number, and professor
		String [][] course_attributes = {{"course_id",   "int", "4", "no", "0", "0"},
                					{"course_number", "int", "4", "no", "1", "0"},
                					{"course_name", "string", "50", "no", "2", "2"}};
		mydb.createTable("course", course_attributes, false);
		
		// create indexes for course id of 
		mydb.createIndexQuery("course_id_index", "course", "course_id", false);
		// create indexes
		mydb.createIndexQuery("course_name_index", "course", "course_number", true);
		
		// insert tuples into student
		String [][] insert_student = {{"student_id","first_name", "last_name", "dob"}, 
									 	{"0","john", "smith", "01/01/2000"}};
		String [][] insert_student1 = {{"student_id","first_name", "last_name", "dob"},
				                      	{"1","bill", "joe", "01/01/1998"}};
		String [][] insert_student2 = {{"student_id","first_name", "last_name", "dob"},
										{"2","sally", "may", "01/01/1985"}};
		String [][] insert_student3 = {{"student_id","first_name", "last_name", "dob"},
                					  	{"3","brittney", "spear", "01/01/2001"}};
		String [][] insert_student4 = {{"student_id","first_name", "last_name", "dob"}, 
				 						{"4","Tim", "joe", "01/01/1978"}};
		String [][] insert_student5 = {{"student_id","first_name", "last_name", "dob"}, 
				 						{"5","jimmy", "lou", "01/01/1947"}};
		String [][] insert_student6 = {{"student_id","first_name", "last_name", "dob"}, 
			 							{"6","deborah", "lin", "01/01/1987"}};
		
		mydb.insertQuery("student", insert_student);
		mydb.insertQuery("student", insert_student1);
		mydb.insertQuery("student", insert_student2);
		mydb.insertQuery("student", insert_student3);
		mydb.insertQuery("student", insert_student4);
		mydb.insertQuery("student", insert_student5);
		mydb.insertQuery("student", insert_student6);

		// insert tuples into course
		String [][] insert_course = {{"course_id", "course_number", "course_name"},
										{"1", "3039", "quality control"}};
		String [][] insert_course1 = {{"course_id", "course_number", "course_name"},
										{"2", "3103", "logistics"}};
		String [][] insert_course2 = {{"course_id", "course_number", "course_name"},
										{"3", "3232", "stochastic"}};
		String [][] insert_course3 = {{"course_id", "course_number", "course_name"},
										{"4", "3133", "optimization"}};
		String [][] insert_course4 = {{"course_id", "course_number", "course_name"},
									  	{"5", "3025", "simulation"}};
		String [][] insert_course5 = {{"course_id", "course_number", "course_name"},
										{"6", "4803", "financial engineering"}};
		String [][] insert_course6 = {{"course_id", "course_number", "course_name"},
										{"7", "4803", "advanced regression"}};
		String [][] insert_course7 = {{"course_id", "course_number", "course_name"},
										{"8", "2028", "statisitics"}};

		mydb.insertQuery("course", insert_course);
		mydb.insertQuery("course", insert_course1);
		mydb.insertQuery("course", insert_course2);
		mydb.insertQuery("course", insert_course3);
		mydb.insertQuery("course", insert_course4);
		mydb.insertQuery("course", insert_course5);
		mydb.insertQuery("course", insert_course6);
		mydb.insertQuery("course", insert_course7);
		
		RelationInfo R = (RelationInfo)mydb.syscat.getRelationCatalog().get("course");
		
		/*
		String [] condition = {"course_id", "3", ">"};
		System.out.println("The number of blocks in R " + R.getNumDataBlocks());
		System.out.println("The number of tuples in R " + R.getNumTuples());
		IndexScan myindexscan = new IndexScan(mydb, R, condition);
		RelationInfo rel = myindexscan.open();
		System.out.println("Printing select course_id > 3 using index scan");
		mydb.printOutRelation(rel);
		*/
		
		/*
		String [] condition = {"course_id", "3", ">"};
		Select myselect = new Select(mydb, R, condition, true);
		System.out.println("Printing select course_id > 3");
		mydb.printOutRelation(myselect.open());
		
		String [] condition1 = {"course_number", "4000", "<"};
		myselect = new Select(mydb, R, condition1, true);
		System.out.println("Printing select course_number < 4000");
		mydb.printOutRelation(myselect.open());
		*/
		/*
		String [] condition2 = {"course_name", "simulation", "="};
		Select myselect = new Select(mydb, R, condition2, false);
		System.out.println("Printing select course_name = simulation");
		mydb.printOutRelation(myselect.open());
		*/
		/*
		String [] attList = {"course_id", "course_name"};
		Project myproject = new Project(mydb, R, attList);
		System.out.println("Printing project course_id, course_name");
		RelationInfo rel = myproject.open();
		mydb.printOutRelation(rel);
		*/
		
		RelationInfo R1 = (RelationInfo)mydb.syscat.getRelationCatalog().get("student");
		/*
		String [] condition3 = {"first_name", "john", "="};
		myselect = new Select(mydb, R1, condition3, false);
		System.out.println("Printing select first_name = john");
		mydb.printOutRelation(myselect.open());
		
		String [] attList1 = {"dob"};
		myproject = new Project(mydb, R1, attList1);
		System.out.println("Printing project dob from student");
		mydb.printOutRelation(myproject.open());
		*/
		/*
		CrossProduct cp = new CrossProduct(mydb, R, R1);
		RelationInfo rel = cp.open();
		System.out.println("Printing cross product of course and student");
		mydb.printOutRelation(rel);
		*/
		
		/*
		String [][] conditions = {{"course_id", "3", "<"},{"course_number", "3000", ">"}};
		Filter myfilter = new Filter(mydb, R, conditions);
		System.out.println("Printing filter course_id and course_name");
		RelationInfo rel = myfilter.open();
		mydb.printOutRelation(rel);
		*/
		
		String [] condition = {"course_id", "student_id", "="};
		Join myjoin = new Join(mydb, R, R1, condition, false, 0);
		RelationInfo rel = myjoin.open();
		mydb.printOutRelation(myjoin.open());
		
		// select catalog from database db1
		//mydb.selectCatalogQuery();
		// select index for course id
		//mydb.selectIndexQuery("course", "course_id_index");
		// select index for course number
		//mydb.selectIndexQuery("course", "course_number_index");
		
	}
}

