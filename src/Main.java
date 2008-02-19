import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.*;
import java.util.*;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Sovandy
 *
 */

public class Main {

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	/**
	 * read relation info from xml file, for instance, student_relation.xml for student 
	 * table. Then create a relation object and return it
	 */
	public void readDBRelations(String dbname)
	{
		String filename = dbname + "_relation.xml";
		String name = null;
		String dateCreated = null;
		String dateModified = null;
		String numTuple = null;
		String id = null;
		String colsIndexed = null;
		String numBlock = null;

    	try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filename));

            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

			NodeList listOfRelations = doc.getElementsByTagName("relation");
			int totalRelations = listOfRelations.getLength();
            System.out.println("Total no of people : " + totalRelations);

			for(int s=0; s< listOfRelations.getLength(); s++)
			{

                Node firstRelationNode = listOfRelations.item(s);
                if(firstRelationNode.getNodeType() == Node.ELEMENT_NODE)
                {
					Element firstRelationElement = (Element)firstRelationNode;

					// NodeList node = doc.getElementsByTagName("name");
					NodeList node = firstRelationElement.getElementsByTagName("name");
					Element firstNameElement = (Element)node.item(0);
					NodeList textFNList = firstNameElement.getChildNodes();
					name = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					System.out.println("Relation name : " + name);

					node = firstRelationElement.getElementsByTagName("date_created");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					dateCreated = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					System.out.println("Date created : " + dateCreated);

					node = firstRelationElement.getElementsByTagName("date_modified");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					dateModified = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					System.out.println("Date modified : " + dateModified);

					node = firstRelationElement.getElementsByTagName("num_tuple");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					numTuple = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					System.out.println("Number of tuple : " + numTuple);

					node = firstRelationElement.getElementsByTagName("id");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					id = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					System.out.println("id : " + id);

					node = firstRelationElement.getElementsByTagName("cols_indexed");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					colsIndexed = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					System.out.println("Columns indexed : " + colsIndexed);

					node = firstRelationElement.getElementsByTagName("num_block");
					firstNameElement = (Element)node.item(0);
					textFNList = firstNameElement.getChildNodes();
					numBlock = (String)((Node)textFNList.item(0)).getNodeValue().trim();
					System.out.println("Number of block : " + numBlock);
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
	
	public void createDB(String dbname)
	{
		try {	
		File file = new File(dbname+"_relations.xml");
	    BufferedWriter output = new BufferedWriter(new FileWriter(file));
	    output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void writeAttribute(String table_name, String [] attribute)
	{
		try {
			File file = new File(table_name+"_attributes.xml");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write("<attributes>");
			output.write("<attribute>");
			output.write("<name>" + attribute[0] + "</name>");
			output.write("<type></type>");
			output.write("</attribute>");
			output.write("</attributes>");
			output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	/**
	 * write to the file
	 * @param tablename
	 * @param attributes
	 */
	public void createTables(String dbname, String [] table_names, Object [] attributes)
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String cur_date = (String)sdf.format(cal.getTime());
		try {	
			File file = new File(dbname+"_relations.xml");
		    BufferedWriter output = new BufferedWriter(new FileWriter(file));
		    output.write("<relations>");
		    
		    output.write("<relation>");
		    output.write("<name>" + table_names[0] + "</name>");
		    output.write("<date_created>" + cur_date + "</date_created>\n");
			output.write("<date_modified>" + cur_date + "</date_modified>\n");
			output.write("<num_tuple>0</num_tuple>\n");
			output.write("<id>0</id>\n");
			output.write("<cols_indexed>-1</cols_indexed>\n");
			output.write("<num_block>0</num_block>\n");
			String [] attribute = (String [])attributes[0];
			writeAttribute(table_names[0], attribute);
			
			output.write("<relation>");
		    output.write("<name>" + table_names[1] + "</name>");
		    output.write("<date_created>" + cur_date + "</date_created>\n");
			output.write("<date_modified>" + cur_date + "</date_modified>\n");
			output.write("<num_tuple>0</num_tuple>\n");
			output.write("<id>1</id>\n");
			output.write("<cols_indexed>-1</cols_indexed>\n");
			output.write("<num_block>0</num_block>\n");
			attribute = (String [])attributes[1];
			writeAttribute(table_names[1], attribute);
			
			output.write("</relations");
		    output.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	{
		Main mydb = new Main();
		String dbname = "db1";
		System.out.println("just test");
		System.out.println("create database");
		mydb.createDB(dbname);
		System.out.println("Use database db1");
		System.out.println("create table student");
		String [][] student_table = {{"first_name", "string", "20", "no", "student", "0", "0"},
                					 {"last_name", "string", "20", "no", "student", "1", "0"},
                					 {"dob", "string", "10", "no", "student", "0", "2"}};
		String [][] course_table = {{"course_name", "string", "20", "no", "student", "0", "0"},
                					{"course_number", "string", "10", "no", "student", "1", "0"},
                					{"location", "string", "10", "no", "student", "0", "2"}};
		String [] table_names = {"student","course"}; 
		Object [] attributes= new Object[2];
		attributes[0] = student_table;
		attributes[1] = course_table;
		mydb.createTables("student", table_names, attributes);
		
	}

}
