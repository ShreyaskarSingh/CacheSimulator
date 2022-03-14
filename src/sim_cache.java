import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class sim_cache {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
	    new Methods(new Cache(
				Integer.parseInt(args[0]),
				Integer.parseInt(args[1]),
				Integer.parseInt(args[2]),
				Integer.parseInt(args[3]),
				Integer.parseInt(args[4]),
//				Integer.parseInt(args[5]),
//				Integer.parseInt(args[6]),
				args[5]
				));
	    }

	
}
