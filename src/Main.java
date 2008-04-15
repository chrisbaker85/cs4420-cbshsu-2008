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
	
	/**
	 * read indexs from xml file 
	 * @param dbname
	 * @param table_name
	 * @return
	 */
	private Hashtable readIndexs(String tableName)
	{
		Hashtable<String, IndexInfo> indexInfos = new Hashtable<String, IndexInfo>();
		String filename = syscat.getDBName() + "_" + tableName + "_index.txt";
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
					
					Attribute attObj = new Attribute(name, type, length, isnullable, tableName, id, num_values);
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
					String tablename = (String)((Node)textFNList.item(0)).getNodeValue().trim();

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
					
					Hashtable attributes = readAttributes(tablename);
					RelationInfo relation = new RelationInfo(tablename, dateCreated, dateModified, numTuple, Integer.parseInt(id), colsIndexed, tablename + "_" + syscat.getDBName() + "index.dat", numBlock, attributes);
					// test to see if there is index for that relation, set indexinfo
					if (!colsIndexed.equals("0"))
					{
						Hashtable indexInfos = this.readIndexs(tablename);
						relation.setIndexInfos(indexInfos);
					}
					this.syscat.addRelationCatalog(tablename, relation);
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
	public void createDB(String dbname)
	{
		try {	
		File file = new File(dbname+"_relations.xml");
	    BufferedWriter output = new BufferedWriter(new FileWriter(file));
	    output.write("<relations>\n");
	    output.write("</relations>");
	    output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		this.useDatabase(dbname);
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
       		id = (data.size() - 2) / 7;
       		
			// if it's not temporary relation, write to XML files
			if (!isTempRelation)
			{
				
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
	      		data.add(ind++, "</relations>");
	      		
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
					Attribute attObj = new Attribute(attributes[i][0], attributes[i][1], attributes[i][2], attributes[i][3], tableName, Integer.toString(i), "0");
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
					Attribute attObj = new Attribute(attributes[i][0], attributes[i][1], attributes[i][2], attributes[i][3], tableName, Integer.toString(i), "0");
					atts.put(attributes[i][0], attObj);
				}
			}
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		RelationInfo relObj = new RelationInfo(tableName, cur_date, cur_date, "0", id, "-1", "", "1", atts);
		
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
			File file = new File(syscat.getDBName() + "_" + tableName + "_index.txt");
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
		
		RelationInfo relObj = (RelationInfo)syscat.getRelationCatalog().get(tablename);
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
		
		// extract the key and insert into index in relation info using relObj
		String[] indexNames = relObj.getColsIndexed().split(",");
		
		//if (!indexName.equals("-1")) {
		if (indexNames.length > 0 && !indexNames[0].equals("-1")) {

		    for (int i = 0; i < indexNames.length; i++) {
		                
		        String indexName = indexNames[i];
		        
		        System.out.println("INFO: indexName " + indexName);
		        
		        // search in array for the key
	            int ind;
	            for (ind = 0; ind < query[0].length; ind++)
	            {
	                if (query[0][ind].equals(indexName)) break;
	            }
	            // get key to insert into Index
	            int key = Integer.parseInt(query[1][ind]);
	            relObj.getIndexInfos().put(key, lastOffset);
	            // write it to file.
	            String filename = indexName + "_index.txt";
	            String line = key + "\t" + lastOffset + "\n";
	            Utility.appendToFile(filename, line);
   
		    }
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
		RelationInfo relInfo = (RelationInfo)syscat.getRelationCatalog().get(tableName);
		IndexInfo indexInfo = new IndexInfo(indexName, attName, isDuplicate);
		relInfo.getIndexInfos().put(attName, indexInfo);
		// update index info
		relInfo.setColsIndexed(attName);
		// create a blank index file
		try {	
			File file = new File(attName + "_index.txt");
		    BufferedWriter output = new BufferedWriter(new FileWriter(file));
		    output.close();
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
	public boolean selectIndexQuery(String tableName, String index_name) 
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
	
	public boolean selectQuery(String[] tableNames, String [] fields, String [][] where)
	{
		
		OpTree ot = new OpTree(this.syscat, tableNames, fields, where);
		
		int opNumber = ot.getNumOps();
		// optable: hashtable to store all temporary relations
		//Hashtable<Integer, Op> optable = new Hashtable<Integer, Op>();
		Op op = ot.nextOp();
		Op currentOp = op; 
		int lastIndex;
		while(op != null)
		{
			currentOp = op;
			lastIndex = op.getID();
			if (op instanceof OpSelect)
			{
				// call select class here
				// RelationInfo R = op.getInfo();
				System.out.println("INFO: In select");
				RelationInfo R = op.left().getInfo();
				String [] conditions = (String [])op.getContents();
				// figure out if it's index or not
				boolean hasIndex = false;
				for (int i = 0; i < conditions.length; i++)
				{
					if (conditions[i].equals(R.getColsIndexed())) 
					{
						hasIndex = true;
						break;
					}
				}
				Select myselect = new Select(this, R, conditions, false);
				RelationInfo result = myselect.open();
				op.info = result;
				//optable.put(new Integer(op.getID()), op);
			}
			else if (op instanceof OpProject)
			{
				System.out.println("INFO: entered project");
				
				// RelationInfo R = op.getInfo();
				RelationInfo R = op.left().getInfo();
				System.out.println("TYPE: " + op.getType() + "/CHILDTYPE" + op.left().getType() + "/INFO" + op.left().getInfo());
				String [] attList = (String [])op.getContents();
				
				// call project class here
				Project myproject = new Project(this, R, attList);
				RelationInfo result = myproject.open();
				op.info = result;
				
				//optable.put(new Integer(op.getID()), op);
			}
			else if (op instanceof OpCrossProduct)
			{
				// call  class CrossProduct here
				Op leftOp = op.left();
				RelationInfo leftR = leftOp.getInfo();
				Op rightOp = op.right();
				RelationInfo rightR = rightOp.getInfo();
				CrossProduct mycp = new CrossProduct(this, leftR, rightR);
				RelationInfo result = mycp.open();
				op.info = result;
				//optable.put(new Integer(op.getID()), op);
			}
			else if (op instanceof OpJoin)
			{
				// call join class here
			}
			op = ot.nextOp();
		}
		// print out results in currentOp here

		System.out.println("INFO: printing out results");
		RelationInfo relObj = currentOp.getInfo();
		Hashtable atts = relObj.getAttribute();
		int tupleSize = Utility.getTotalLength(atts);
		Iterator iterator = new Iterator(bufman, relObj, Integer.parseInt(relObj.getNumDataBlocks().trim()));
		Tuple tuple;
		String [] attNames = Utility.getAttributeNames(atts);
		for (int j = 0; j < attNames.length; j++)
		{
			System.out.print(attNames[j] + "\t\t");
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
		
		System.out.println("INFO: (main) tuples: " + Integer.parseInt(relObj.getNumTuples().trim()));
		
		for (int i = 0; i < Integer.parseInt(relObj.getNumTuples().trim()); i++)
		{
			System.out.println("INFO: index " + i);
			tuple = iterator.getNext();
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] data = block.getTupleContent(offset, tupleSize);
			String [] results = Utility.convertTupleToArray(atts, data);
			for (int j = 0; j < attNames.length; j++)
			{
				System.out.print(">>" + results[j]);
			}
			System.out.println("");
		}
		return true;
	}
	
	/**
	 * select database that user want to work on
	 * @param dbname
	 */
	public void useDatabase(String dbname)
	{
		syscat = new SystemCatalog(dbname); 
		this.readDBRelations();
		bufman.setDBName(dbname);
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
		String dbName = "db1";
		System.out.println("create database db1");
		mydb.createDB(dbName);
		System.out.println("Use db1");
		mydb.useDatabase(dbName);
		System.out.println("***********************************************");
		System.out.println("create table student");
		String [][] student_attributes = {{"first_name", "string", "20", "no", "0", "0"},
                					     {"last_name",   "string", "20", "no", "1", "0"},
                					     {"dob",         "string", "10", "no", "2", "2"}};
		mydb.createTable("student", student_attributes, false);
		String [][] course_attributes = {{"course_name",   "string", "20", "no", "0", "0"},
                					{"course_number", "string", "10", "no", "1", "0"},
                					{"location",      "string", "10", "no", "2", "2"}};
		mydb.createTable("course", course_attributes, false);
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

