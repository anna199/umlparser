import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;


public class umlparser {
	public static void main(String args[]) {
		String dirpath = args[0];
		String output = args[1];
		File dir = new File(dirpath);
		List<CompilationUnit> cuArray = new ArrayList<>();
		if (!dir.exists()) {
			System.out.println("Directory not exist!");
		}
		File[] fileList = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		});
		try {
		for (File file: fileList) {
				CompilationUnit cu = JavaParser.parse(file);
				cuArray.add(cu);
		}
		        
			ParseClass classes = new ParseClass(cuArray,output);
	        classes.start();
	       // System.out.println(classes.parseCA());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
