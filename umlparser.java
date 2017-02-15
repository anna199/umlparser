import java.io.File;
import java.io.FilenameFilter;

public class umlparser {
	public static void main(String args[]) {
		String dirpath = args[0];
		String output = args[1];
		//System.out.println(dirpath);
		//System.out.println(output);
		File dir = new File(dirpath); 
		if (!dir.exists()) {
			System.out.println("Directory not exist!");
		}
		File[] fileList = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		});
		for (File a: fileList) {
			System.out.println(a.getName());
		}
		
		
	}

}
