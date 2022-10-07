import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Methods {
	
	Cache l1, l2;
	int occurence = 0;
	List<String> opt;
	int traffic_counter = 0;
	Methods(Cache l1, Cache l2)
	{
		this.l1 = l1;
		this.l2 = l2;
		l2.cache = new ArrayList<>();
		l1.cache = new ArrayList<>();
		insrt(l1);
		insrt(l2);
		insert(l1);

	}
	
	public  void insrt(Cache l) {
		
		
		int n_rows = l.N_SETS;

		
		for (int i = 0; i < n_rows; i++) 
		{	
			l.cache.add(new ArrayList<DS>());
		}
		
		
		
	}
	
	void allocate(int ar[], int middle, int index, int lvl, int direction)
	{
		if(lvl == 0)
		{
			ar[index] = direction;
			return;
		}
		else if(middle > index)
		{
			ar[middle] = 0;
			allocate(ar,middle+lvl, index, lvl/2, direction);
		}
		else
		{
			ar[middle] = 1;
			allocate(ar,middle-lvl, index, lvl/2, direction);
		}
	}
	
	void PLRU(int ar[], int index)
	{
		int temp = index;
		int direction = 0;
		if(temp%2 != 0)
		{
			direction = 1;
			temp--;
		}
		int middle = (ar.length-1)/2;
		allocate(ar, middle, temp, (middle+1)/2, direction);
		
	}


	private int deallocation(int middle, int lvl, int[] ar) {
		// TODO Auto-generated method stub
		if(lvl == 0)
		{
			if(ar[middle] == 0)
			{
				ar[middle] = 1;
				return middle+1;
			}
			else
			{
				ar[middle] = 0;
				return middle;
			}
		}
		else if(ar[middle] == 0)
		{
			ar[middle] = 1;
			return deallocation(middle + lvl, lvl/2, ar);
		}
		else
		{
			ar[middle] = 0;
			return deallocation(middle - lvl, lvl/2, ar);
		}
		
	}


	private int evictionPLRU(int[] ar) {
		// TODO Auto-generated method stub
		int mid = (ar.length-1)/2;
		int levelValue = (mid+1)/2;
		
		return deallocation(mid,levelValue,ar);
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
		try {
			address = hextobinary(address);
			int lower = get_ntag_bits(cache);
			int higher = get_nindex_bits(cache);
			String index_bits = address.substring(lower,lower+higher);
			int index = Integer.parseInt(index_bits,2);
			return index;
		}catch(Exception e) {
			return 0;
		}
			
		}
	
	public  String get_tag_bits(String address, Cache cache ) {
			address = hextobinary(address);
			return address.substring( 0,get_ntag_bits(cache));
					
		}
	
	List<Integer> blankIndices = new ArrayList<>();

	int glblRowIndex = 0;
    public  void read_l1(String data, Cache l1, int counter) {
    	
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
    			PLRU(l1.plru[index_bit], block.indexOf(ds));
    			
    			return;
    			
    	}
    		
    }
    	l1.READ_MISS++;
    	glblRowIndex = index_bit;
    	
    	// MISS IF SPACE AVAILABLE
    	if(block.size()<l1.ASSOC)
		{	
			for(DS d: block)
				{
				d.setLastaccess(d.getLastaccess()-1);
				d.setOPTCounter(d.getOPTCounter()+1);
				}
			
			if(blankIndices.size() != 0)
			{
				block.add(blankIndices.get(0),new DS(address, tag, l1.ASSOC -1 , false));
				
				PLRU(l1.plru[index_bit], blankIndices.remove(0));
				
				
			}else {
				block.add(new DS(address, tag, l1.ASSOC -1 , false));
				
				PLRU(l1.plru[index_bit], block.size()-1);
				
			}
			
			if(l2.SIZE != 0)
				read_l2(data, l2);
		}
    	else // REPLACEMENT
    	{	
    		updateCache(address, tag,block, false  );
    		//OPT(address, tag,block, false, counter  );
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
    			
    			PLRU(l2.plru[index_bit], block.indexOf(ds));
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
			
			PLRU(l2.plru[index_bit], block.size()-1);
		}
    	else // REPLACEMENT
    	{	
    		updateCache2(address, tag,block, false  );
    	}
 }
    
    public void write_l1(String data, Cache l1, int counter ) {
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
    			
    			PLRU(l1.plru[index_bit], block.indexOf(ds));
    			
    			return;
    			
    	}
    		
    }
    	l1.WRITE_MISS++;
    	glblRowIndex = index_bit;
    	if(block.size()<l1.ASSOC)
		{	
			for(DS d: block)
			{
				d.setLastaccess(d.getLastaccess()-1);
				d.setOPTCounter(d.getOPTCounter()+1);
				}
			
			if(blankIndices.size() != 0)
			{
				block.add(blankIndices.get(0),new DS(address, tag, l1.ASSOC -1 , true));
				
				PLRU(l1.plru[index_bit], blankIndices.remove(0));
				
				
			}else {
				block.add(new DS(address, tag, l1.ASSOC -1 , true));
				
				PLRU(l1.plru[index_bit], block.size()-1);
				
			}
			
			
			
			if(l2.SIZE != 0)
				read_l2(data, l2);
		}
    	else // REPLACEMENT
    	{	updateCache(address, tag,block, true  );
    		//OPT(address, tag,block, true , counter );
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
    			PLRU(l2.plru[index_bit], block.indexOf(ds));
    			return;
    			
    	}
    		
    }
    	l2.WRITE_MISS++;
    	
    	if(block.size()<l2.ASSOC)
		{	
			for(DS d: block)
				d.setLastaccess(d.getLastaccess()-1);
			
			block.add(new DS(address, tag, l2.ASSOC -1 , true));
			PLRU(l2.plru[index_bit], block.size()-1);
		}
    	else // REPLACEMENT
    	{	
    		updateCache2(address, tag,	block, true );
    	}
    	
    }


    
    
    
    public void updateCache(String address,String tag, List<DS> l, boolean dirty) {
	
    	int index = 0;
    	
    	
    	switch (l1.REPLACEMENT_POLICY)
    	{
	    	case 1:{
	    		int index_bit = get_index_bits(address,l1);
	    		
	    		index = evictionPLRU(l1.plru[index_bit]);
	    		
	    		break;
	    	}
	    	case 2:{
	    		index = getEvictedBlockUsingOPT(address,l);
	    		
	    		break;
	    	}
	    	default:{
	    		
	    		for(int i=0; i<l.size(); i++)
	    		{
	    			DS d = l.get(i);
	    			
	    			if(d.getLastaccess() == 0)
	    			{	
	    				
	    				index = i;
	    			
	    				
	    			}
	    			else
	    			{
	    				d.setLastaccess(d.getLastaccess()-1);
	    			}
	    		}
	    		
	    		break;
	    	}
    	}
    
		
		
		DS temp = l.remove(index);
		
		if(temp.getDirty())
		{
			if(l2.SIZE != 0) {
				
				write_l2(temp.getData(),l2);
			}
			l1.WRITE_BACK++;
		}
		
		l.add(index, new DS(address, tag, l1.ASSOC -1 , dirty));
		
		if(l2.SIZE != 0 )
			read_l2(address,l2);
		
	}
    
    private int getEvictedBlockUsingOPT(String address,  List<DS> l) {
		// TODO Auto-generated method stub
    	
    	int returnIndex = 0;
    	
    	int arr[]=new int [l.size()];
    	
    	Arrays.fill(arr, Integer.MAX_VALUE);
    	
    	for(int i=0; i<arr.length; i++)
    	{
    		DS d= l.get(i);
    		
    		List<OPTBlock> li = dataTable.get(glblRowIndex);
    		
    		for(int j=0; j<li.size(); j++)
    		{
    			OPTBlock temp = li.get(j);
    			
    			if(temp.getIndex() > globalIndex)
    			{
    				String tag = get_tag_bits(temp.getData(), l1);
    				if(d.getTag().equals(tag))
    				{
    					arr[i] = temp.getIndex() - globalIndex;
    					break;
    				}
    			}
    		}
    		
    	}
    	
    	int max = -1;
		for(int i:arr)
			max= Math.max(max, i);
		
		for(int i=0;i<l.size();i++)
		{
			if(arr[i] == max)
				return i;
		}
		
		return 0;
	}

	public void updateCache2(String address,String tag, List<DS> l, boolean dirty) {
    	
    	int index = 0;
    
    	switch(l2.REPLACEMENT_POLICY)
    	{
    	case 1:{
    		int index_bit = get_index_bits(address,l2);
    		
    		index = evictionPLRU(l2.plru[index_bit]);
    		break;
    	}
    	case 2:{
    		break;
    	}
    	default:{
    		for(int i=0; i<l.size(); i++)
    		{
    			DS d = l.get(i);
    			if(d.getLastaccess() == 0)
    			{	

    				index = i;
    				
    				
    			}
    			else
    				d.setLastaccess(d.getLastaccess()-1);
    		}
    		
    		break;
    	}
    		
    	}
		
		
	
		
		DS temp = l.remove(index);
		
		if(temp.getDirty())
		{
			l2.WRITE_BACK++;
		}
		
		
		l.add(index, new DS(address, tag, l2.ASSOC -1 , dirty));
		
		if(l2.INCLUSION_PROPERTY == 1)
		{
			evictFromL1Cache(temp);
		}
		
	}
	
    public void evictFromL1Cache(DS temp) {
		// TODO Auto-generated method stub
    	int index_bit = get_index_bits(temp.getData(),l1 );
    	String tag = get_tag_bits(temp.getData(), l1);
    	
    	List<DS> li = l1.cache.get(index_bit);
    	
    	for(DS ds: li)
    	{
    		if(ds.getTag().equals(tag)) {
    			
    			int index = li.indexOf(ds);
    			blankIndices.add(index);
    			DS traffic = li.remove(index);
    			if (traffic.getDirty()) {
    				traffic_counter++;
    			}
    			break;
    		}
    	}
	}

	public void eviction(String address){
    	System.out.println("EVICXTION");
    	int index_bit = get_index_bits(address,l1 );
    	String tag = get_tag_bits(address, l1);
    	List<DS> block = l1.cache.get(index_bit);
    	
    	
    	for(int r = 0; r < block.size(); r++)
    	{
    		if(block.get(r).getTag().equals(tag))
    		{ 	System.out.println("HOIIIIITTT");
    			// HIT
    		
    			l1.cache.remove(r);
    			//l1.WRITE_BACK++;
    			return;
    			
    	}
    		
    }
	}
    public  void OPT(String address, String tag, List<DS> l, boolean dirty, int counter) {
    	int [] temp = new int[l.size()];
    	int count = 0;
    	for (int j = 0; j < l.size(); j++) {
    		for (int i = counter-1; i < opt.size(); i++ ) {
    			if (l.get(j).equals(opt.get(i)) ){
    				temp[j] = i;
    				count++;
    				break;
    			}
    		}
    		if (count == j) {
    			DS temp2 = l.remove(j);
    			if(temp2.getDirty())
    			{
    				l1.WRITE_BACK++;
    			}
    			l.add(j, new DS(address, tag, l1.ASSOC -1 , dirty));
    			return; 
    		}
    	}
    	int max_idx = 0;
        for (int k=0; k<l.size(); k++) {
            if (temp[k] > temp[max_idx]) {
                max_idx = k;
            }
        }
        DS temp2 = l.remove(max_idx);
		if(temp2.getDirty())
		{
			l1.WRITE_BACK++;
		}
		l.add(max_idx, new DS(address, tag, l1.ASSOC -1 , dirty));
		return; 
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
 
 class OPTBlock{
	 String data;
	 int index;
	 
	 
	 
	public OPTBlock(String data, int index) {
		
		this.data = data;
		this.index = index;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	 
	 
 }
 
 Map<Integer, List<OPTBlock>> dataTable = new HashMap<>();
 int globalIndex = 0;
    public void insert(Cache l1) {
		// TODO Auto-generated method stub
    	opt = new ArrayList<String>();
    	int i=0;
    	for(String str: l1.input_data)
    	{

			
			try {
				str = str.split(" ")[1];
				str = format(str);
				int index = get_index_bits(str, l1);
				if(!dataTable.containsKey(index))
					dataTable.put(index, new ArrayList<>());
				dataTable.get(index).add(new OPTBlock(str,i++));
			}catch(Exception ignored) {}
    	}
    	
    	for(String str:l1.input_data)
		{	
			try {
			str = str.split(" ")[1];
			str = format(str);
			str = get_tag_bits(str,l1);
			opt.add(str);
			} catch (Exception e) {
				continue;
			}
			
		}
		for(String str:l1.input_data)
		{	
			try {
				
			boolean read = str.split(" ")[0].toLowerCase().contains("r"); //true for read
			
			str = str.split(" ")[1];
			str = format(str);
			occurence++;

			if(read)
				read_l1(str,l1, occurence);
			else
				write_l1(str, l1, occurence);
			
			globalIndex++;
			
			} catch (Exception e) {
				continue;
			}
			
			
		}
		double l1_miss_rate = 0.0;
		double l2_miss_rate = 0.0;
		
		if (l1.SIZE != 0) {
			l1_miss_rate = ((double)(l1.READ_MISS+l1.WRITE_MISS)/(double)(l1.READ+l1.WRITE));
		}
		
		if (l2.SIZE != 0) {
			l2_miss_rate = ((double)(l2.READ_MISS)/(double)(l2.READ));
		}
//		System.out.println(opt);
		
		
		System.out.println("===== Simulator configuration =====");
		System.out.println("BLOCKSIZE:             "	+	l1.BLOCKSIZE);
		System.out.println("L1_SIZE:               "	+	l1.SIZE);
		System.out.println("L1_ASSOC:              "	+	l1.ASSOC);
		System.out.println("L2_SIZE:               "	+	l2.SIZE);
		System.out.println("L2_ASSOC:              "	+	l2.ASSOC);
		System.out.println("REPLACEMENT POLICY:    "	+	(l1.REPLACEMENT_POLICY == 0?"LRU":(l1.REPLACEMENT_POLICY == 1?"Pseudo-LRU":"Optimal")));
		System.out.println("INCLUSION PROPERTY:    "	+	(l1.INCLUSION_PROPERTY == 0?"non-inclusive":"inclusive"));
		System.out.println("trace_file:            "	+	l1.Trace_file);
		
		
		
		printCache();
		
		if(l2.SIZE != 0 )
			printCache2();
		
		System.out.println("===== Simulation results (raw) =====");
		
		System.out.println("a. number of L1 reads:        "	+	l1.READ);
		System.out.println("b. number of L1 read misses:  "	+	l1.READ_MISS);
		System.out.println("c. number of L1 writes:       "	+	l1.WRITE);
		System.out.println("d. number of L1 write misses: "	+	l1.WRITE_MISS);
		System.out.println("e. L1 miss rate:              "	+	String.format("%.6f",l1_miss_rate));
		System.out.println("f. number of L1 writebacks:   "	+	l1.WRITE_BACK);
		System.out.println("g. number of L2 reads:        "	+	l2.READ);
		System.out.println("h. number of L2 read misses:  "	+	l2.READ_MISS);
		System.out.println("i. number of L2 writes:       "	+	l2.WRITE);
		System.out.println("j. number of L2 write misses: "	+	l2.WRITE_MISS);
		System.out.println("k. L2 miss rate:              "	+	String.format("%.6f",l2_miss_rate));
		System.out.println("l. number of L2 writebacks:   "	+	l2.WRITE_BACK);
		
		int total_traffic = (l1.READ_MISS + l1.WRITE_MISS + l1.WRITE_BACK);
		if (l2.SIZE!=0) {
			total_traffic =   l2.READ_MISS + l2.WRITE_MISS + l2.WRITE_BACK + traffic_counter;
			}
		System.out.println("m. total memory traffic:      "	+	total_traffic);
		
		/*
		printCache();
		if (l2.SIZE != 0) {
			printCache2();}
		System.out.println("===== Simulation results (raw) =====");
		System.out.print("number of L1 reads:     "	);
		System.out.format("%11d", l1.READ);
		System.out.println();
		System.out.print("number of L1 read misses:     "	);
		System.out.format("%4d", l1.READ_MISS);
		System.out.println();
		System.out.println("number of L1 writes:          "	+	l1.WRITE);
		System.out.println("number of L1 write misses:    "	+	l1.WRITE_MISS);
		System.out.println("L1 miss rate:                 " + l1_miss_rate);
		System.out.println("number of L1 writebacks:      "	+	l1.WRITE_BACK);
		
		
		
		
		
		
		System.out.print("number of L2 reads:     "	);
		System.out.format("%9d", l2.READ);
		System.out.println();
		System.out.print("number of L2 read misses:      "	);
		System.out.format("%2d", l2.READ_MISS);
		System.out.println();
		System.out.println("number of L2 writes:            "	+	l2.WRITE);
		System.out.println("number of L2 write misses:      "	+	l2.WRITE_MISS);
		System.out.println("L2 miss rate:                   " + l2_miss_rate);
		System.out.println("number of L2 writebacks:        "	+	l2.WRITE_BACK);
		
				
		
		
		
		
		
//		System.out.format("%4d", i); System.out.format("%3d", j); System.out.format("%3d", k); System.out.println();


		System.out.println("total memory traffic:         " + total_traffic);
		
		*/
		
		 
		 
		
	}
    
    
    public String binary_to_hex(String binary) {
    	int decimal = Integer.parseInt(binary,2);
    	String hexStr = Integer.toString(decimal,16);
    	
    	return hexStr;
    }
    
	
}