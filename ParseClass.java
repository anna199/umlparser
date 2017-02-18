import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

public class ParseClass {
	
	private CompilationUnit cu;
	private Map<ClassOrInterfaceDeclaration, ClassAttribute> map = new HashMap<>(); 
	public ParseClass(CompilationUnit cu) {
		this.cu = cu;
	}
	public void getClassName() {
		 List<Node> nodes = cu.getChildNodes();
	        if (nodes != null) {
	            for (Node n : nodes) {
	                if (n instanceof ClassOrInterfaceDeclaration) {
	                	//map.put((ClassOrInterfaceDeclaration) n, new ClassAttribute());
	                    System.out.println(((ClassOrInterfaceDeclaration) n).getName().toString());
	                }
	            }
	        }
	}
	
	public void getVariableName() {
		cu.getNodesByType(FieldDeclaration.class).stream().
        filter(f -> f.getModifiers().contains(Modifier.PRIVATE)). //&& 
               // !f.getModifiers().contains(Modifier.STATIC)).
        forEach(f -> System.out.println(f.toString()));
	}
	
	public void constructClassAttribute() {
		List<Node> nodes = cu.getChildNodes();
        if (nodes != null) {
        	
            for (Node n : nodes) {
                if (n instanceof ClassOrInterfaceDeclaration) {
                	List<String> private_var = new ArrayList<>();
                	List<String> public_var = new ArrayList<>();
                	n.getNodesByType(FieldDeclaration.class).stream().
                    filter(f -> f.getModifiers().contains(Modifier.PRIVATE)). //&& 
                    forEach(f -> private_var.add(f.toString()));
                	
                	n.getNodesByType(FieldDeclaration.class).stream().
                    filter(f -> f.getModifiers().contains(Modifier.PUBLIC)).
                    forEach(f -> public_var.add(f.toString()));
                	map.put((ClassOrInterfaceDeclaration) n, new ClassAttribute(private_var,public_var,new ArrayList<String>()));
                }
            }
        }
		
	}

}
