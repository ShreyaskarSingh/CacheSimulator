import java.io.*;
import java.util.*;


public class Cache {
	int BLOCKSIZE, SIZE, ASSOC, REPLACEMENT_POLICY, INCLUSION_PROPERTY, N_SETS,READ, WRITE, READ_MISS,WRITE_MISS,WRITE_BACK;
	String Trace_file;
	List<String> input_data;
	List<List<DS>> cache;
	List<String> opt;
	
	int plru [][];
	

	public Cache(int bLOCKSIZE, int sIZE, int aSSOC, int rEPLACEMENT_POLICY, int iNCLUSION_PROPERTY,
			String trace_file) {

		BLOCKSIZE = bLOCKSIZE;
		SIZE = sIZE;
		ASSOC = aSSOC;
		REPLACEMENT_POLICY = rEPLACEMENT_POLICY;
		INCLUSION_PROPERTY = iNCLUSION_PROPERTY;
		if (ASSOC != 0) {
		N_SETS = ((SIZE) /( ASSOC * BLOCKSIZE));}
		Trace_file = trace_file;
		input_data = input_read();
		cache = new ArrayList<List<DS>>();
		READ = 0;
		WRITE = 0;
		READ_MISS = 0;
		WRITE_MISS = 0;
		WRITE_BACK= 0;
		
		int tempAssoc =ASSOC;
		if(ASSOC <2)
			tempAssoc =2;
			
		plru = new int [N_SETS][tempAssoc-1];
		
		
	} 

	
List<String> input_read() {
		

		List<String> data = new ArrayList<>();
		
		try {
			File file = new File(Trace_file);

			BufferedReader br = new BufferedReader(new FileReader(file));

			String compress;

			while ((compress = br.readLine()) != null) {
				
				data.add(compress);
				
			}

		} catch (Exception ignored) {
			System.out.println(ignored);
		}

		return data;
	}
}
