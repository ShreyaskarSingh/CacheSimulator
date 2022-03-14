import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Methods {
	int miss = 0;
	int hit = 0;
	Cache l1;
	int writeback = 0;
	
	Methods(Cache cache)
	{
		this.l1 = cache;
		l1.cache = new ArrayList<>();
		insrt(l1);
	}
	
	public  void insrt(Cache l) {
		
		int n_rows = l.N_SETS;

		
		for (int i = 0; i < n_rows; i++) 
		{	ArrayList<DS> singleList = new ArrayList<DS>();
			l.cache.add(singleList);
		}
		
	}



	
	
	public  String hextobinary(String hexcode){
		String binary = "";
		hexcode = hexcode.toLowerCase();

		HashMap<Character, String> hashMap = new HashMap<Character, String>();

		hashMap.put('0', "0000");
		hashMap.put('1', "0001");
		hashMap.put('2', "0010");
		hashMap.put('3', "0011");
		hashMap.put('4', "0100");
		hashMap.put('5', "0101");
		hashMap.put('6', "0110");
		hashMap.put('7', "0111");
		hashMap.put('8', "1000");
		hashMap.put('9', "1001");
		hashMap.put('a', "1010");
		hashMap.put('b', "1011");
		hashMap.put('c', "1100");
		hashMap.put('d', "1101");
		hashMap.put('e', "1110");
		hashMap.put('f', "1111");

		int i;
		char ch;
		
		for (i = 0; i < hexcode.length(); i++) {
			ch = hexcode.charAt(i);
			binary += hashMap.get(ch);
		}

		return binary;
		
	}

	public  int get_nindex_bits(Cache cache) {
		int n_index_bits = (int) (Math.log(cache.N_SETS) / Math.log(2));
		return n_index_bits;
	}

	public  int get_noffset_bits(Cache cache) {
		int n_offset_bits = (int) (Math.log(cache.BLOCKSIZE) / Math.log(2));
		return n_offset_bits;
	}

	public  int get_ntag_bits(Cache cache) {
		int n_offset_bits = 32 - get_nindex_bits(cache) - get_noffset_bits(cache);
		return n_offset_bits;
	}
	
	
	public   String format(String input) {
		while (input.length() < 8){
			input = "0" + input;
		}
		return input;
	}
	

	
	public  int get_index_bits(String address, Cache cache ) {
			address = hextobinary(address);
			int lower = get_ntag_bits(cache);
			int higher = get_nindex_bits(cache);
			String index_bits = address.substring(lower,lower+higher);
			int index = Integer.parseInt(index_bits,2);
			return index;
				
			
		}
	
	public  String get_tag_bits(String address, Cache cache ) {
			address = hextobinary(address);
			return address.substring( 0,get_ntag_bits(cache));
					
		}

    public  void read(String data, Cache l) {
    	String action =  data.split(" ")[0];
    	String address = data.split(" ")[1];
    	int index_bit = get_index_bits(address,l );
    	
    	List<DS> block = l.cache.get(index_bit);
    	String tag = get_tag_bits(address,l);
    	for(DS ds:block)
    	{
    		if(ds.data.equals(tag))
    		{ 	hit++;
    			//READ HIT
    			int value = ds.getLastaccess();
    			
    			for(DS cb: block)
    			{
    				if(cb.data.equals(tag)) {
    					
    					cb.setLastaccess(l.ASSOC-1);;
    				}
    				else if(cb.getLastaccess() > value)
    				{
    					cb.setLastaccess(cb.getLastaccess()-1);
    				}
    				
    			
    		}
    			return;
    			
    	}
    		
    }
    	miss++;
    	// MISS
    	// IF SPACE AVAILABLE
    	if(l.cache.size()<l.ASSOC)
		{	
			for(DS d: l.cache.get(index_bit))
				d.setLastaccess(d.getLastaccess()-1);
			
			l.cache.get(index_bit).add(new DS(address, tag, l.ASSOC -1 , true));
		}
    	else // REPLACEMENT
    	{
    		updateCache(address, tag,l.cache.get(index_bit), read );
    	}
 }
    public void write(String tag, List<CacheBlock> li, CacheBlock c) {
		// TODO Auto-generated method stub
		int value = c.getLRUaccessCounter();
		
		for(CacheBlock cb: li)
		{
			if(cb.tag.equals(tag)) {
				
				cb.setLRUaccessCounter(cache.l1Set-1);
			}
			else if(cb.getLRUaccessCounter() > value)
			{
				cb.setLRUaccessCounter(cb.getLRUaccessCounter()-1);
			}
		}
		
	}

    
    
    
    private void updateCache(String address,String tag, ArrayList<DS> l, boolean read) {
		// TODO Auto-generated method stub
    	int idx = 0;
    	
    	
    	
		for(int i=0; i<l.size(); i++)
		{
			if(l.get(i).getLastaccess() == 0)
			{	idx = i;
				DS temp = l.remove(i);
				
				if(temp.getDirty())
				{
					writeback++;
				}
				
				break;
			}
		}
		
		for(DS d: l)
		{
			d.setLastaccess(d.getLastaccess()-1);
		}
		
		l.add(new DS(address, tag, l1.ASSOC -1 , true));
		
		 if(read) {
				l.get(idx).setDirty(false);
		 }
	}

   
	
	
}