import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Methods {
	
	Cache l1, l2;
	Methods(Cache l1, Cache l2)
	{
		this.l1 = l1;
		this.l2 = l2;
		l2.cache = new ArrayList<>();
		l1.cache = new ArrayList<>();
		insrt(l1);
		insrt(l2);
		insert(l1);
//		insert(l2);
	}
	
	public  void insrt(Cache l) {
		
		
		int n_rows = l.N_SETS;

		
		for (int i = 0; i < n_rows; i++) 
		{	
			l.cache.add(new ArrayList<DS>());
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

    public  void read_l1(String data, Cache l1) {
    	
    	String address = data;
    	l1.READ++;
    	
    	int index_bit = get_index_bits(address,l1 );
    	
    	List<DS> block = l1.cache.get(index_bit);
    	String tag = get_tag_bits(address,l1);
    	
    	for(DS ds:block)
    	{
    		if(ds.tag.equals(tag))
    		{ 	
    			//READ HIT
    			int value = ds.getLastaccess();
    			
    			for(DS cb: block)
    			{
    				if(cb.tag.equals(tag)) {
    					
    					cb.setLastaccess(l1.ASSOC-1);;
    				}
    				else if(cb.getLastaccess() > value)
    				{
    					cb.setLastaccess(cb.getLastaccess()-1);
    				}
    				
    			
    		}
    			return;
    			
    	}
    		
    }
    	l1.READ_MISS++;
    	
    	// MISS IF SPACE AVAILABLE
    	if(block.size()<l1.ASSOC)
		{	
			for(DS d: block)
				d.setLastaccess(d.getLastaccess()-1);
			
			block.add(new DS(address, tag, l1.ASSOC -1 , false));
			
			if(l2.SIZE != 0)
				read_l2(data, l2);
		}
    	else // REPLACEMENT
    	{	
    		updateCache(address, tag,block, false  );
    	}
 }
    
public  void read_l2(String data, Cache l2) {
    	
    	String address = data;
    	l2.READ++;
    	
    	int index_bit = get_index_bits(address,l2 );
    	
    	List<DS> block = l2.cache.get(index_bit);
    	String tag = get_tag_bits(address,l2);
    	
    	for(DS ds:block)
    	{
    		if(ds.tag.equals(tag))
    		{ 	
    			//READ HIT
    			int value = ds.getLastaccess();
    			
    			for(DS cb: block)
    			{
    				if(cb.tag.equals(tag)) {
    					
    					cb.setLastaccess(l2.ASSOC-1);;
    				}
    				else if(cb.getLastaccess() > value)
    				{
    					cb.setLastaccess(cb.getLastaccess()-1);
    				}
    				
    			
    		}
    			return;
    			
    	}
    		
    }
    	l2.READ_MISS++;
    	
    	
    	
    	
    	// MISS IF SPACE AVAILABLE
    	if(block.size()<l2.ASSOC)
		{	
			for(DS d: block)
				d.setLastaccess(d.getLastaccess()-1);
			
			block.add(new DS(address, tag, l2.ASSOC -1 , false));
		}
    	else // REPLACEMENT
    	{	
    		updateCache2(address, tag,block, false  );
    	}
 }
    
    public void write_l1(String data, Cache l1 ) {
    	data = format(data);
    	
    	
    	String address = data;
    	int index_bit = get_index_bits(address,l1);
    	List<DS> block = l1.cache.get(index_bit);
    	String tag = get_tag_bits(address,l1);
    	l1.WRITE++;
    	for(DS ds:block)
    	{
    		if(ds.tag.equals(tag))
    		{ 	
    			int value = ds.getLastaccess();
    			
    			for(DS cb: block)
    			{
    				if(cb.tag.equals(tag)) {
    					cb.setDirty(true);
    					cb.setLastaccess(l1.ASSOC-1);;
    				}
    				else if(cb.getLastaccess() > value)
    				{
    					cb.setLastaccess(cb.getLastaccess()-1);
    				}
    				
    			
    		}
    			return;
    			
    	}
    		
    }
    	l1.WRITE_MISS++;
    	
    	if(block.size()<l1.ASSOC)
		{	
			for(DS d: block)
				d.setLastaccess(d.getLastaccess()-1);
			
			block.add(new DS(address, tag, l1.ASSOC -1 , true));
			
			if(l2.SIZE != 0)
				read_l2(data, l2);
		}
    	else // REPLACEMENT
    	{	
    		updateCache(address, tag,block, true );
    	}
    	
    }
    public void write_l2(String data, Cache l2 ) {
    	data = format(data);
    	
    	
    	String address = data;
    	int index_bit = get_index_bits(address,l2);
    	List<DS> block = l2.cache.get(index_bit);
    	String tag = get_tag_bits(address,l2);
    	l2.WRITE++;
    	for(DS ds:block)
    	{
    		if(ds.tag.equals(tag))
    		{ 	
    			int value = ds.getLastaccess();
    			
    			for(DS cb: block)
    			{
    				if(cb.tag.equals(tag)) {
    					cb.setDirty(true);
    					cb.setLastaccess(l2.ASSOC-1);;
    				}
    				else if(cb.getLastaccess() > value)
    				{
    					cb.setLastaccess(cb.getLastaccess()-1);
    				}
    				
    			
    		}
    			return;
    			
    	}
    		
    }
    	l2.WRITE_MISS++;
    	
    	if(block.size()<l2.ASSOC)
		{	
			for(DS d: block)
				d.setLastaccess(d.getLastaccess()-1);
			
			block.add(new DS(address, tag, l2.ASSOC -1 , true));
		}
    	else // REPLACEMENT
    	{	
    		updateCache2(address, tag,	block, true );
    	}
    	
    }


    
    
    
    public void updateCache(String address,String tag, List<DS> l, boolean dirty) {
	
    	int index = 0;
    
		for(int i=0; i<l.size(); i++)
		{
			if(l.get(i).getLastaccess() == 0)
			{	
				
				DS temp = l.remove(i);
				index = i;
				
				if(temp.getDirty())
				{
					if(l2.SIZE != 0) {
						
						write_l2(temp.getData(),l2);
					}
					l1.WRITE_BACK++;
				}
				
				break;
			}
		}
		
		for(DS d: l)
		{
			d.setLastaccess(d.getLastaccess()-1);
		}
		
		l.add(index, new DS(address, tag, l1.ASSOC -1 , dirty));
		
		if(l2.SIZE != 0 )
			read_l2(address,l2);
		
	}
    
    public void updateCache2(String address,String tag, List<DS> l, boolean dirty) {
    	
    	int index = 0;
    
		for(int i=0; i<l.size(); i++)
		{
			if(l.get(i).getLastaccess() == 0)
			{	
				
				DS temp = l.remove(i);
				index = i;
				
				if(temp.getDirty())
				{
					l2.WRITE_BACK++;
				}
				
				break;
			}
		}
		
		for(DS d: l)
		{
			d.setLastaccess(d.getLastaccess()-1);
		}
		
		l.add(index, new DS(address, tag, l2.ASSOC -1 , dirty));
		
		
		
	}
    
    
    public void printCache() {
		
		System.out.println("===== L1 contents =====");
		
		for(int i=0; i < l1.N_SETS; i++)
		{
		
			System.out.print("Set	"+i+": ");
			List<DS> row = l1.cache.get(i);
		
			for(int j = 0; j <row.size() ; j++) {
				System.out.print(binary_to_hex(row.get(j).getTag())+" "+(row.get(j).getDirty()?"D":"")+"	");
			}
			System.out.println();
		}
		
	}
 public void printCache2() {
		
		System.out.println("===== L2 contents =====");
		
		for(int i=0; i < l2.N_SETS; i++)
		{
		
			System.out.print("Set	"+i+": ");
			List<DS> row = l2.cache.get(i);
		
			for(int j = 0; j <row.size() ; j++) {
				System.out.print(binary_to_hex(row.get(j).getTag())+" "+(row.get(j).getDirty()?"D":"")+"	");
			}
			System.out.println();
		}
		
	}
    public void insert(Cache l1) {
		// TODO Auto-generated method stub
    	
		for(String str:l1.input_data)
		{	
			try {
			boolean read = str.split(" ")[0].toLowerCase().contains("r"); //true for read

			str = str.split(" ")[1];
			str = format(str);
		
			if(read)
				read_l1(str,l1);
			else
				write_l1(str, l1);
			} catch (Exception e) {
				continue;
			}
			
		}
		
		
		
		System.out.println("L1 reads: "	+	l1.READ);
		System.out.println("L1 read misses: "	+ l1.READ_MISS	);
		System.out.println("L1 writes: "	+	l1.WRITE);
		System.out.println("L1 write misses: "	+	l1.WRITE_MISS);
		System.out.println("L1 writebacks: "	+	l1.WRITE_BACK);
		
		

		System.out.println("L2 reads: "	+	l2.READ);
		System.out.println("L2 read misses: "	+ l2.READ_MISS	);
		System.out.println("L2 writes: "	+	l2.WRITE);
		System.out.println("L2 write misses: "	+	l2.WRITE_MISS);
		System.out.println("L2 writebacks: "	+	l2.WRITE_BACK);
		
		
		printCache();
		printCache2();
		 
		 
		
	}
    
    
    public String binary_to_hex(String binary) {
    	int decimal = Integer.parseInt(binary,2);
    	String hexStr = Integer.toString(decimal,16);
    	
    	return hexStr;
    }
    
	
}