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
 * @author Sovandy
 *
 */

public class Main 
{

	private SystemCatalog syscat;
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public SystemCatalog getSysCat()
	{
		return this.syscat;
	}
	
	public void setSysCat(SystemCatalog obj)
	{
		this.syscat = obj;
	}
	
	public ArrayList readAttributes(String dbname, String table_name)
	{
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		String filename = dbname + "_" + table_name + ".xml";
		try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filename));

            doc.getDocumentElement ().normalize ();
            // System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

			NodeList listOfRelations = doc.getElementsByTagName("attribute");
			int totalRelations = listOfRelations.getLength();
            // System.out.println("Total no of people : " + totalRelations);

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
					attributes.add(attObj);
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
	 * @param dbname
	 */
	public void readDBRelations(String db_name)
	{
		String filename = db_name + "_relation.xml";
		
    	try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filename));

            doc.getDocumentElement ().normalize ();
            // System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

			NodeList listOfRelations = doc.getElementsByTagName("relation");
			int totalRelations = listOfRelations.getLength();
            // System.out.println("Total no of people : " + totalRelations);

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
					
					ArrayList attributes = readAttributes(db_name, table_name);
					RelationInfo relation = new RelationInfo(table_name, dateCreated, dateModified, numTuple, Integer.parseInt(id), colsIndexed, table_name + "_" + db_name + "index.dat", numBlock, attributes);
					this.syscat.getRelationCatalog().put(id, relation);
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
	    output.write("</relations>\n");
	    output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * When CREATED TABLE command is entered, it will open file relations file for that database
	 * Then, it append table info to the file. Then it create a file containing all the attribute
	 * info for that table.  
	 * @param tablename: name of table
	 * @param attributes: list of attributes for that table
	 */
	public void createTable(String dbname, String table_name, String [][] attributes)
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String cur_date = (String)sdf.format(cal.getTime());
		String line = "";
        ArrayList<String> data = new ArrayList<String>();		
		try {	
			FileReader fr = new FileReader(dbname + "_relations.xml");
       		BufferedReader br = new BufferedReader(fr);	// Can also use a Scanner to read the file.
       		while((line = br.readLine()) != null)
       		{
       	 		data.add(line);
       		}

       		int id = (data.size() - 2) / 7;
       		int ind = data.size() - 1;
			data.add(ind++, "<relation>");
			data.add(ind++, "<name>" + table_name + "</name>\n");
			data.add(ind++, "<date_created>" + cur_date + "</date_created>\n");
			data.add(ind++, "<date_modified>" + cur_date + "</date_modified>\n");
			data.add(ind++, "<num_tuple>0</num_tuple>\n");
			data.add(ind++, "<id>" + id + "</id>\n");
			data.add(ind++, "<cols_indexed>-1</cols_indexed>\n");
			data.add(ind++, "<num_block>0</num_block>\n");
      		data.add(ind++, "</relation>\n");
      		data.add(ind++, "</relations>\n");
      		
			File file = new File(dbname+"_relations.xml");
		    BufferedWriter output = new BufferedWriter(new FileWriter(file));
		    
		    for (int i = 0; i < data.size(); i++)
		    {
		    	output.write(data.get(i));
		    }
		    output.close();
		    
		    // write attributes for that relation into table
		    file = new File(dbname + "_" + table_name + ".xml");
			output = new BufferedWriter(new FileWriter(file));
			output.write("<attributes>");
			for (int i = 0; i < attributes.length; i++)
			{
				output.write("<attribute>");
				output.write("<name>" + attributes[i][0] + "</name>\n");
				output.write("<type>" + attributes[i][1] + "</type>\n");
				output.write("<length>" + attributes[i][2] + "</length>\n");
				output.write("<isnullable>" + attributes[i][3] + "</isnullable>\n");
				output.write("<relation_name>" + table_name + "</relation_name>\n");
				output.write("<id>" + i + "</id>\n");
				output.write("<num_values>0</num_values>\n");
				output.write("</attribute>");
			}
			output.write("</attributes>");
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void insertQuery(String table_name, String [][] query)
	{
		// TODO: Scan through all the blocks in buffer to look for buffer that belongs to that relation and it has free space
	}
	
	public void useDatabase(String db_name)
	{
		syscat = new SystemCatalog(db_name); 
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	{
		Main mydb = new Main();
		String db_name = "db1";
		System.out.println("just test");
		System.out.println("create database");
		mydb.createDB(db_name);
		System.out.println("Use db1");
		mydb.useDatabase(db_name);
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

	}
}

