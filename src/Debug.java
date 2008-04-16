/**
 * 
 */

/**
 * @author chrisb
 * This singleton class handles debug information generation
 * to find out if debugging is enabled, call:
 * Debug.getSingletonObject().debug()
 *
 */
public class Debug {

    private static Debug ref;
    
    /**
     * debug parameter 
     */
    private boolean debug = false;
	
    /**
     * blank constructor
     */
    private Debug() {}

    /**
     * Makes or gets the object
     * @return
     */
    public static Debug get() {
    	
      if (ref == null) ref = new Debug();
      
      return ref;
      
    }
    
    /**
     * Set the debug param
     * @param debug
     */
    public void setDebug(boolean debug) {
    	
    	this.debug = debug;
    	
    }
    
    /**
     * 
     * @return
     */
    public boolean debug() {
    	
    	return this.debug;
    	
    }

}
