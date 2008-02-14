/**
*	CS 4420: Database System Implementation
*	Class Project - Code for Implementation of Indexing using B+ Trees
*	AUTHOR: Tushar Sugandhi	
*	Last Updated:02/18/2007
*
*
*	NOTE: You may use the public functions in this class for implementing 
*	indexing in your database system. 

*	Also note that each object of BTree class is capable of handleing ATMOST
*	20 indices. (That is, it can handle indexing on 20 attributes.)
*
*	READ the comments from main() function carefully to understand how to use the public functions in this class
*	
*	READ the code for ReadBlock() and MapBlock() to understand the usage of 
*	java.nio classes.	
*
*/

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;


interface c 
{
	public static final int ORDER = 64;
	public static final int BLOCKSIZE = ORDER*2 * 8;
	public static final int BUCKETPTR = ORDER*2  -1;
	public static final int NLEN = 40;
	public static final int NIDX = 20;
}


class Node
{
	short leafNode ;
	short keyCount;
	long []keys;
	long []pointers;
	
	public Node()
	{
		keys = new long[c.ORDER -1];
		pointers=new long [c.ORDER];
		
	}
}

class InsertKeyVal
{
	public int returnVal;
	public long key;
	public long ptr;
	public InsertKeyVal()
	{
		returnVal = 0;
		key = 0;
		ptr = 0;
	}
}

class RecursiveInsertVal
{
	public boolean returnVal;
	public long key;
	public long ptr;
	
	public RecursiveInsertVal()
	{
		returnVal = false;
		key = 0;
		ptr = 0;
	}
}

class Head
{
	long rootBlock;
	long allocBlock;
	byte [] junk;
	
	public Head()
	{
		junk = new byte[c.BLOCKSIZE - 2*8];
	}
}

class Bucket
{
	long [] pointers;
	long nextBucket;
	
	public Bucket()
	{
		pointers = new long [c.BUCKETPTR];	
	}
}

class IndexInfo {
	boolean isUsed;
	FileChannel fileChannel;
	String indexFilename;
	boolean isdupKeyPresent;
	long rootBlock;
	long allocBlock;
	long currentBlock;
	short currentKey;
	short currentPtr;
	public IndexInfo()
	{
		isUsed = false;

		indexFilename = "";
		isdupKeyPresent = false;
		rootBlock = 0;
		allocBlock = 0;
		currentBlock = 0;
		currentKey = 0;
		currentPtr = 0;
	}
}

public class BTree {
    
    	private IndexInfo [] indexTable;
	public Bucket bucket;
	
	public BTree ()
	{ 
		
		indexTable = new IndexInfo[c.NIDX];
		for(int k =0;k<c.NIDX;k++)
		{
			indexTable[k] = new IndexInfo();
		}
                bucket = new Bucket();
                
                for (int k=0;k<c.BUCKETPTR;k++)
                {
                    bucket.pointers[k]=-1;
                }
	}
	
	//refer this function to understand usage of java.nio classes
	//this function maps the perticular portion of file into buffer for READ_ONLY purpose
	private MappedByteBuffer ReadBlock(final FileChannel fileChannel,final long block,final int size ) 
        throws FileNotFoundException,IOException
	{
		MappedByteBuffer mappedBuffer;
		mappedBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, (int) block * size, size);
		return mappedBuffer;
	}
	
	
	//refer this function to understand usage of java.nio classes
	//this function maps the perticular portion of file into buffer for READ_WRITE purpose
	private MappedByteBuffer MapBlock(final FileChannel fileChannel, final long block, final int size)
	throws FileNotFoundException,IOException
	{
		MappedByteBuffer mappedBuffer;
		mappedBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, block * size, size);
		return mappedBuffer;
	}
	

	private void ReadHead(final int index)
	{
		try
		{
			Head head = new Head();
			MappedByteBuffer mbb = ReadBlock(indexTable[index].fileChannel, 0, c.BLOCKSIZE);
			head.rootBlock = mbb.getLong();
			head.allocBlock = mbb.getLong();
			
			setRootBlock(index,head.rootBlock);
			setAllocBlock(index,head.allocBlock);
		}catch (Exception e)
		{
			System.out.println("Error Reading Head.");
			e.printStackTrace();
		}
	}
	

	
	private void WriteHead(final int index)
	{
		try
		{
			Head head = new Head();
			head.rootBlock = getRootBlock(index);
			head.allocBlock = getAllocBlock(index);
			MappedByteBuffer mbb = MapBlock(indexTable[index].fileChannel, 0, c.BLOCKSIZE);
	
			mbb.putLong(0,head.rootBlock);
			mbb.putLong(8,head.allocBlock);
				
		}catch (Exception ex)
		{
			System.out.println("Can not write head.");
			ex.printStackTrace();
		}

	}
	

	private int FindKey(final Node node,final long key)
	{ 
		int i;
		for(i = 0; i < node.keyCount; i++)
			if(node.keys[i] > key) break;
		return i;
	}


	private	long CheckBucket(final int index, Node node,long key, long ptr)
	{
                
		try
		{
			ptr = -1;
		
			if(getCurrentBlock(index) >= 0) 
			{
				long nextPtr=-1;
			
				key = node.keys[getCurrentKey(index)-1];
				ptr = node.pointers[getCurrentKey(index)];
				
				if(getDupKeys(index)) 
				{
					//bucket = new Bucket();
					MappedByteBuffer mbb = ReadBlock(indexTable[index].fileChannel,ptr, c.BLOCKSIZE);
					for(int i =0; i<c.BUCKETPTR;i++)
					{
						bucket.pointers[i] = mbb.getLong();
					}
				
					bucket.nextBucket = mbb.getLong();
					
					ptr = bucket.pointers[getCurrentPtr(index)];
				
					indexTable[index].currentPtr++;
					
					nextPtr = bucket.pointers[getCurrentPtr(index)];
				}
				if(!getDupKeys(index) || nextPtr < 0) 
				{
					setCurrentPtr(index,(short)0);
					indexTable[index].currentKey++;
				
					if(getCurrentKey(index) > node.keyCount) 
					{
						setCurrentBlock(index, node.pointers[0]);
						setCurrentKey(index,(short)1);
					}
				}
			}
		}catch (Exception ex)
		{
			System.out.println("Error Checking Bucket.");
			ex.printStackTrace();
		}

		return ptr;
	}
	

	private	InsertKeyVal InsertKey(final int index, Node node, final int keyIndex, long key, long ptr)
	{
		InsertKeyVal returnObject = new InsertKeyVal();
		
		long [] keys;
		keys = new long [c.ORDER];
		
		long [] pointers;
		pointers = new long[c.ORDER+1];
		
		int count, count1, count2; 
		int k;
               
		count = node.keyCount + 1;
		count1 = count < c.ORDER ? count : c.ORDER/2;
		count2 = count - count1;

		for(k = c.ORDER/2; k < keyIndex; k++) 
		{
			keys[k] = node.keys[k];
			pointers[k+1] = node.pointers[k+1];
		}

		keys[keyIndex] = key;
		pointers[keyIndex+1] = ptr;

		for(k = keyIndex; k < node.keyCount; k++) 
		{
			keys[k+1] = node.keys[k];
			pointers[k+2] = node.pointers[k+1];
		}

		for(k = keyIndex; k < count1; k++) 
		{
			node.keys[k] = keys[k];
			node.pointers[k+1] = pointers[k+1];
		}

		node.keyCount = (short)count1;
		
		if(count2>0) 
		{
			int s, d;
			Node nnode = new Node();
			nnode.leafNode = node.leafNode;
			s=0;
			if(node.leafNode==0)
			{
				count2 -= 1;
				s=1;
			}

			
			for(s += c.ORDER/2, d = 0; d < count2; s++, d++) 
			{
				nnode.keys[d] = keys[s];
				nnode.pointers[d] = pointers[s];
			}

			nnode.pointers[d] = pointers[s];
			nnode.keyCount = (short)count2;

			key = keys[c.ORDER/2];
			ptr = indexTable[index].allocBlock++;
			
			if(node.leafNode>0) 
			{
				nnode.pointers[0] = node.pointers[0];
				node.pointers[0] = ptr;
			}

			MappedByteBuffer mbb;
                try {
			mbb = MapBlock(indexTable[index].fileChannel, ptr, c.BLOCKSIZE);
			mbb.putShort(nnode.leafNode);
			mbb.putShort(nnode.keyCount);
		
			for (int ii =0;ii<nnode.keys.length;ii++)
			{
				mbb.putLong(nnode.keys[ii]);
			}
				
			for(int jj=0;jj<nnode.pointers.length;jj++)
			{
				mbb.putLong(nnode.pointers[jj]);
			}
				

		} catch (FileNotFoundException e) 
                {
			System.out.println("Can not map block. The file not found.");	
			e.printStackTrace();
		} 
                catch (IOException e) 
                {
                    System.out.println("Can not map block. Can not read from the file. IO exception. Check if you have permissions to write.");
                    e.printStackTrace();
		}
			WriteHead(index);
	}
		returnObject.returnVal = count2;
		returnObject.key = key;
		returnObject.ptr = ptr;

		return returnObject;
        }


	private	long NewBucket(final int index,final long ptr,final long next)
	{
		long bblock;

		Bucket bucket=new Bucket();
		bucket.pointers[0] = ptr;
                bucket.pointers[1] = -1;  
		bucket.nextBucket = next;

		bblock = indexTable[index].allocBlock++;

		MappedByteBuffer mbb;
		try {
			mbb = MapBlock(indexTable[index].fileChannel, bblock, c.BLOCKSIZE);
			
			for(int i =0; i < bucket.pointers.length;i++)
			{
				mbb.putLong(bucket.pointers[i]);	
			}
			mbb.putLong(bucket.nextBucket);
			
			WriteHead(index);
		} catch (FileNotFoundException e) {
			System.out.println("Error creating bucket. File not found. Check that you have proper permissions");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error creating bucket. IO exceptions. Check that you have proper permissions");
			e.printStackTrace();
		}
		return bblock;
	}
	

	private	RecursiveInsertVal RecInsert(final int index,final long block, long key, long ptr) throws FileNotFoundException, IOException
	{
                
		RecursiveInsertVal returnVal = new RecursiveInsertVal();
		int keyIndex;
		boolean split = false;
		boolean equalKey;
		
		MappedByteBuffer mbb;
		
			mbb = MapBlock(indexTable[index].fileChannel, block, c.BLOCKSIZE);
		
		Node node = new Node();
		
		node.leafNode = mbb.getShort();
		node.keyCount = mbb.getShort();
		
		for(int i=0;i<node.keys.length;i++)
		{
			node.keys[i] = mbb.getLong();
		}
		
		for(int j=0;j<node.pointers.length;j++)
		{
			node.pointers[j] = mbb.getLong();
		}
		
		keyIndex = FindKey(node, key);
				
		equalKey = (keyIndex>0) && node.keys[keyIndex-1] == key;

		RecursiveInsertVal tempReturn = new RecursiveInsertVal();
		
		if(node.leafNode==0)
		{
		
			 tempReturn = RecInsert(index, node.pointers[keyIndex], key, ptr);
			 split = tempReturn.returnVal;
			 key = tempReturn.key;
			 ptr = tempReturn.ptr;
		}

		if(split || node.leafNode==1 && !equalKey) 
		{
						
			if(node.leafNode==1 && getDupKeys(index))
				ptr = NewBucket(index, ptr, -1);

			InsertKeyVal retObj = InsertKey(index, node, keyIndex, key, ptr);
			key = retObj.key;
			ptr = retObj.ptr;
			int split1 = retObj.returnVal;
			
		
			if(split1 > 0){split = true;}else {split=false;}
			
			MappedByteBuffer mbb3 =  MapBlock(indexTable[index].fileChannel, block, c.BLOCKSIZE);
			mbb3.putShort(node.leafNode);
			mbb3.putShort(node.keyCount);
			for(int len=0;len<node.keys.length;len++ )
			{
				mbb3.putLong(node.keys[len]);
			}
			for(int len2=0;len2<node.pointers.length;len2++)
			{
				mbb3.putLong(node.pointers[len2]);
			}
			

		} else if(node.leafNode==1 && getDupKeys(index)) 
		{ /* put in existing bucket */
			Bucket bucket = new Bucket();
			int i;
						
			MappedByteBuffer mbb2 = MapBlock(indexTable[index].fileChannel, node.pointers[keyIndex], c.BLOCKSIZE);
			
			for(i=0;i<bucket.pointers.length;i++)
			{
				bucket.pointers[i]=mbb2.getLong();
			}
			bucket.nextBucket = mbb2.getLong();
			
			for(i = 0; i < c.BUCKETPTR && bucket.pointers[i] >= 0; i++);

			if(i < c.BUCKETPTR) 
			{
			    bucket.pointers[i] = ptr;
			    if(i < c.BUCKETPTR-1) bucket.pointers[i+1] = -1;
			   

			    mbb2.position(i*8);
			    mbb2.putLong(ptr);
			    mbb2.putLong(-1);
		
			} 
			else 
			{
				System.out.println("ERROR: Bucket Overflow.");
			}
		} else if(node.leafNode==1) 
		{
			insertionErr = -1;
		}
		returnVal.returnVal = split;
		returnVal.key = key;
		returnVal.ptr = ptr;
		
		return returnVal;
		
	} 
	
	private	int insertionErr;

	private	boolean getUsed (int i ){return indexTable[i].isUsed;};
	private	boolean getDupKeys (int i ) {return indexTable[i].isdupKeyPresent;};
	private	long getRootBlock (int i ) {return indexTable[i].rootBlock;};
	private	long getAllocBlock (int i ) {return indexTable[i].allocBlock;};
	private	long getCurrentBlock (int i ) {return indexTable[i].currentBlock;};
	private	short getCurrentKey (int i ) {return indexTable[i].currentKey;};
	private	short getCurrentPtr (int i ) {return indexTable[i].currentPtr;};

	private	void setUsed (int i, boolean used )			{ indexTable[i].isUsed = used;};
	private	void setDupKeys (int i, boolean DupKey )	{ indexTable[i].isdupKeyPresent = DupKey;};
	private	void setRootBlock (int i, long RB  )	{ indexTable[i].rootBlock = RB;};
	private	void setAllocBlock (int i, long ALB)	{ indexTable[i].allocBlock = ALB;};
	private	void setCurrentBlock (int i, long CB)	{ indexTable[i].currentBlock = CB;};
	private	void setCurrentKey (int i, short CK )	{ indexTable[i].currentKey = CK;};
	private	void setCurrentPtr (int i, short CP )	{ indexTable[i].currentPtr = CP;};


//////////////////Public Functions Implementation starts here////////////////////////////////////////////

	//open or create index file, find free entry in index information table,
	//fill in entry, dupKeys is true if duplicate keys are allowed, false if not,
    	//return number of entry in table 
	public int OpenIndex (final String name,final boolean dupKeys)
	{
	
		int i;
		int j;
		for(i = 0; i < c.NIDX; i++)
			if(!getUsed(i)) break;
                if(i==c.NIDX)
                {
                    System.out.println("All indices are used. Use another BTree object or close unused indices.");
                    return -1;
                } 
		setUsed(i,true);
		indexTable[i].indexFilename = name;
	
		setDupKeys(i,dupKeys);

		try{ 
			RandomAccessFile rand = new RandomAccessFile(indexTable[i].indexFilename, "rw");
			indexTable[i].fileChannel = rand.getChannel();
			if(rand.length()==0)
			{
				Node node = new Node();
				setRootBlock(i, 1);
				setAllocBlock(i,2);
				WriteHead(i);
			
				node.leafNode = 1;
				node.keyCount = 0;
		        
				for(j = 0; j < c.ORDER-1; j++)
				{ 
				
					node.pointers[j] = -1; 
					node.keys[j] = 0; 
				}  

				node.pointers[c.ORDER - 1] = -1;
		
				MappedByteBuffer mbb = MapBlock(indexTable[i].fileChannel, 1, c.BLOCKSIZE);

				mbb.putShort(node.leafNode);
				mbb.putShort(node.keyCount);
				
				for (int c=0;c<node.keys.length;c++)
				{
					mbb.putLong(node.keys[c]);
				}
				
				for (int c=0;c<node.pointers.length;c++)
				{
					mbb.putLong(node.pointers[c]);
				}
				
	
			}else
			{
				ReadHead(i);
			}
		}catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
		}catch(IOException iox)
		{
			iox.printStackTrace();
		}
		return i;
	}
	
	// close Index file, free entry in index information table 
	public void CloseIndex(final int index)
	{
		try
		{
                    WriteHead(index);
                    indexTable[index].fileChannel.close();
                    setUsed(index, false);
		}
		catch (IOException ex)
		{
			System.out.println("Can not close the file");
		}
		return;
	}
	
	//insert key & ptr in index file
	//'index' is the entry in index table where information about index file is stored	// You should not worry about what are the contents of index table	// Only make sure that you are doing operations on the 'right' index.
	//return 0 for success, nonzero for error
	public int Insert(final int index,long key,long ptr)
	{
		boolean split;
		RecursiveInsertVal recReturnVal = new RecursiveInsertVal();
		insertionErr = 0;
		try
                {
		
                    recReturnVal = RecInsert(index, getRootBlock(index), key, ptr);
		
                    split = recReturnVal.returnVal;
                    key = recReturnVal.key;
                    ptr = recReturnVal.ptr;
		
                    if(split) 
                    {
			Node node = new Node();
			node.leafNode = 0;
			node.keyCount = 1;
			node.keys[0] = key;
			node.pointers[1] = ptr;
			node.pointers[0] = getRootBlock(index);
			setRootBlock(index, indexTable[index].allocBlock++);
			
			MappedByteBuffer mbb = MapBlock(indexTable[index].fileChannel,getRootBlock(index),c.BLOCKSIZE);
			mbb.putShort(node.leafNode);
			mbb.putShort(node.keyCount);
			
			for(int i = 0; i<node.keys.length;i++)
			{
				mbb.putLong(node.keys[i]);
			}
			for(int j=0;j<node.pointers.length;j++)
			{
				mbb.putLong(node.pointers[j]);
			}
		
			WriteHead(index);
                    }

                    setCurrentBlock(index,-1);
		}catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
		}catch(IOException iox)
		{
			iox.printStackTrace();
		}
		return insertionErr;		
	}
	//returns pointer for entry in index file <=key; 
	//returns -1 if no pointer is present for the given key
	//if duplicate values are present then pointers are 
	//placed in bucket object of this class.
	public long Lookup(final int index,final long key) throws FileNotFoundException, IOException
	{
		Node node = new Node();
		long ptr = -1;
		setCurrentBlock(index, getRootBlock(index));
		while(true)
		{
			
			MappedByteBuffer mbb = ReadBlock(indexTable[index].fileChannel, getCurrentBlock(index), c.BLOCKSIZE);
			node.leafNode = mbb.getShort();
			node.keyCount = mbb.getShort();
			
			for (int c=0;c<node.keys.length;c++)
				node.keys[c] = mbb.getLong();
			
			for (int c=0;c<node.pointers.length;c++)
				node.pointers[c] = mbb.getLong();

			setCurrentKey(index, (short)FindKey(node, key));
			if(node.leafNode==1) break;
			setCurrentBlock(index, node.pointers[getCurrentKey(index)]);
		}
		
                setCurrentPtr(index, (short)0);
		if(getCurrentKey(index) == 0) setCurrentBlock(index, -1);
		ptr = CheckBucket(index, node, key, ptr);
		return ptr;
	}
	
//This is sample code for you to explain how to use the public functions 
//in this class.
//Please read carefully the comments written in this function
//You can remove this function while integrating BTree class into your code.

	public static void main(String[] args) throws IOException 
        {
		BTree b = new BTree();

		int idx1 = b.OpenIndex("./Index1.txt", true);
		int idx3 = b.OpenIndex("./Index3.txt", false);

                long i;
                int k;
                long counter = 0;

                for(i=0;i<10;i++)
                {
		    counter = i*100;
                    for ( k=1;k<=127;k++)
                    {
			//take care to insert into proper index (idx)
			//If you are writing to index with Dupkeys == true;
			//The bucket in object "b" can store at max 127 duplicate values for single key
		     	//if this number is exceeded, bucket overflow occures	 

			b.Insert(idx1, i, counter);
			counter++;	
                    }
                }

                

		for(i=0;i<100;i++)
		{
                	b.Insert(idx3, i,i);

		}

		//Always remember to close the index after use	
				b.CloseIndex(idx1);
                b.CloseIndex(idx3);                 
                
		//Take special care to set dupkeys to proper value 
		//when you reopen the index.                 
		//The behavior is undefined if you create the index 
		//with one value of dupkeys and reopen it with other 
		//value
                idx1 = b.OpenIndex("./Index1.txt", true); //undefined, if set to false
                idx3 = b.OpenIndex("./Index3.txt", false); //undefined, if set to true
                long yahoo;
                
                
                for(i=-5;i<15;i++)
                {
		    //Always remember what you are reading from !
		    //If you are reading from index with Dupkeys == true,
		    //then don't forget to read the pointer values from bucket.			
		    //Bucket can store at max 127 duplicate values for single key
		    //if this number is exceeded, bucket overflow occures	 
                    yahoo = b.Lookup(idx1, i);
                    System.out.println("Yahoo1: " + yahoo);
                    for (k=0;k<127 && b.bucket.pointers[k] != -1;k++)
                    {
                        System.out.println("Index: " +idx1+" Key:"+i+" Values:" +b.bucket.pointers[k]);
                    }

		    //always clean the bucket after use
		    for(k=0;k<127;k++)	{b.bucket.pointers[k] = -1; }
                }
                    
		for(i=0;i<100;i++)
		{
	              	yahoo = b.Lookup(idx3, i);
                    System.out.println("Index: "+idx3+ "Key:" +i+" Values:" +yahoo);

		}

				b.CloseIndex(idx1);
                b.CloseIndex(idx3);

	}

}
