import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;


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
		for (File file: fileList) {
			try {
				CompilationUnit cu = JavaParser.parse(file);
				//System.out.println(cu.toString());
		        ParseClass classes = new ParseClass(cu);
		        classes.getVariableName();
		        //classes.constructClassAttribute();
		        System.out.println("");
		        
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}
