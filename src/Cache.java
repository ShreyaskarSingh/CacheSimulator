import java.io.*;
import java.util.*;


public class Cache {
	int BLOCKSIZE, SIZE, ASSOC, REPLACEMENT_POLICY, INCLUSION_PROPERTY, N_SETS;
	String Trace_file;
	List<String> input_data;
	List<List<DS>> cache;

	public Cache(int bLOCKSIZE, int sIZE, int aSSOC, int rEPLACEMENT_POLICY, int iNCLUSION_PROPERTY,
			String trace_file) {

		BLOCKSIZE = bLOCKSIZE;
		SIZE = sIZE;
		ASSOC = aSSOC;
		REPLACEMENT_POLICY = rEPLACEMENT_POLICY;
		INCLUSION_PROPERTY = iNCLUSION_PROPERTY;
		N_SETS = ((SIZE) /( ASSOC * BLOCKSIZE));
		Trace_file = trace_file;
		input_data = input_read();
		cache = new ArrayList<List<DS>>();
		

	} 

	
List<String> input_read() {
		

		List<String> data = new ArrayList<>();
		
		try {
			File file = new File("src/" + Trace_file);

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
