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

	
	/**
	 * when create table is executed, write xml file for relation, for instance, student_relation.xml
	 */
	public void writeRelationXML(String relationName)
	{
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException
	{
		String dbname = "db1";
		System.out.println("just test");
		System.out.println("craate database: doing nothing");
		System.out.println("Use database db1");
		
		// CREATE TABLE: CREATE A FILE 
		// USE DATABASE: CREATE OBJECT FOR SYSTEM CATALOG(FOR EACH RELATION AND ATTRIBUTE), AND LOAD THEM INTO BUFFER OR MAYBE NOT 
		// INSERT INTO TABLE: look into the buffer to see if there is an empty block, update it and flush it to the file
		// If it doesn't exist, go to file, get the last block, load to buffer, write to buffer, then flush it to the file (we can write to the file directly??
		// SELECT: load 
	}

}
