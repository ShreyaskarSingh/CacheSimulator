import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Methods {
	int read = 0;
	int read_miss = 0;
	int write = 0;
	int write_miss = 0;
	int write_back = 0;
	Cache l1;
	Methods(Cache cache)
	{
		this.l1 = cache;
		l1.cache = new ArrayList<>();
		insrt(l1);
		insert(l1);
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
    	
    	String address = data;
    	read++;
    	
    	int index_bit = get_index_bits(address,l );
    	
    	List<DS> block = l.cache.get(index_bit);
    	String tag = get_tag_bits(address,l);
    	for(DS ds:block)
    	{
    		if(ds.tag.equals(tag))
    		{ 	
    			//READ HIT
    			int value = ds.getLastaccess();
    			
    			for(DS cb: block)
    			{
    				if(cb.tag.equals(tag)) {
    					
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
    	read_miss++;
    	// MISS
    	// IF SPACE AVAILABLE
    	if(l.cache.size()<l.ASSOC)
		{	
			for(DS d: l.cache.get(index_bit))
				d.setLastaccess(d.getLastaccess()-1);
			
			l.cache.get(index_bit).add(new DS(address, tag, l.ASSOC -1 , false));
		}
    	else // REPLACEMENT
    	{	
    		updateCache(address, tag,l.cache.get(index_bit), false );
    	}
 }
   
    public void write(String data, Cache l ) {
    	data = format(data);
    	
    	
    	String address = data;
    	int index_bit = get_index_bits(address,l);
    	List<DS> block = l.cache.get(index_bit);
    	String tag = get_tag_bits(address,l);
    	write++;
    	for(DS ds:block)
    	{
    		if(ds.tag.equals(tag))
    		{ 	
    			int value = ds.getLastaccess();
    			
    			for(DS cb: block)
    			{
    				if(cb.tag.equals(tag)) {
    					cb.setDirty(true);
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
    	write_miss++;
    	
    	if(l.cache.size()<l.ASSOC)
		{	
			for(DS d: l.cache.get(index_bit))
				d.setLastaccess(d.getLastaccess()-1);
			
			l.cache.get(index_bit).add(new DS(address, tag, l.ASSOC -1 , true));
		}
    	else // REPLACEMENT
    	{	
    		updateCache(address, tag,l.cache.get(index_bit), true );
    	}
    	
    }

    
    
    
    private void updateCache(String address,String tag, List<DS> l, boolean dirty) {
	
    	
    
		for(int i=0; i<l.size(); i++)
		{
			if(l.get(i).getLastaccess() == 0)
			{	
				DS temp = l.remove(i);
				
				if(temp.getDirty())
				{
					write_back++;
				}
				
				break;
			}
		}
		
		for(DS d: l)
		{
			d.setLastaccess(d.getLastaccess()-1);
		}
		
		l.add(new DS(address, tag, l1.ASSOC -1 , dirty));
		
		
	}
    
    public void printCache() {
		
		System.out.println("===== L1 contents =====");
		
		for(int i=0; i < l1.N_SETS; i++)
		{
		
			System.out.print("Set	"+i+": ");
			List<DS> row = l1.cache.get(i);
			for(int j = 0; j <= row.size()-1 ; j++) {
				System.out.print(binary_to_hex(row.get(j).getTag())+" "+(row.get(j).getDirty()?"D":"")+"	");
			}
			System.out.println();
		}
		
	}

    public void insert(Cache l) {
		// TODO Auto-generated method stub
    	
		for(String str:l.input_data)
		{	
			try {
			boolean read = str.split(" ")[0].toLowerCase().contains("r"); //true for read
			System.out.println(str.split(" ")[1]);
			str = str.split(" ")[1];
			str = format(str);
		
			if(read)
				read(str,l1);
			else
				write(str, l1);
			} catch (Exception e) {
				continue;
			}
			
		}
		
		
		
		System.out.println("L1 reads: "	+	read);
		System.out.println("L1 read misses: "	+ read_miss	);
		System.out.println("L1 writes: "	+	write);
		System.out.println("L1 write misses: "	+	write_miss);
		System.out.println("L1 writebacks: "	+	write_back);
		printCache();
		 
		 
		
	}
    
    
    public String binary_to_hex(String binary) {
    	int decimal = Integer.parseInt(binary,2);
    	String hexStr = Integer.toString(decimal,16);
    	
    	return hexStr;
    }
    
	
}