import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 
 */

/**
 * @author Sovandy
 *
 */



public class Main {

	/**
	 * read relation info from xml file, for instance, student_relation.xml for student 
	 * table. Then create a relation object and return it
	 */
	public RelationInfo readRelationXML(String relationName)
	{
		RelationInfo relation = new RelationInfo();
		return relation;
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
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		System.out.println("just test");
		System.out.println("craate database: doing nothing");
		// System.out.println("create table student");
        // FileWriter student = new FileWriter(new File("student.dat"),true);
		
		// CREATE TABLE: CREATE A FILE 
		// USE DATABASE: CREATE OBJECT FOR SYSTEM CATALOG(FOR EACH RELATION AND ATTRIBUTE), AND LOAD THEM INTO BUFFER OR MAYBE NOT 
		// INSERT INTO TABLE: look into the buffer to see if there is an empty block, update it and flush it to the file
		// If it doesn't exist, go to file, get the last block, load to buffer, write to buffer, then flush it to the file (we can write to the file directly??
		// SELECT: load 
	}

}
