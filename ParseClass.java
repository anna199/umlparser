import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ParseClass {
	
	private List<CompilationUnit> cuArray;
	private Map<String, ClassAttribute> map = new HashMap<>();
	ClassOrInterfaceDeclaration CI;
	ClassAttribute ca;
	String output;
	List<String> results = new ArrayList<>();
	
	public ParseClass(List<CompilationUnit> cuArray, String output) {
		this.cuArray = cuArray;
		this.output= output;
	}
	
	public void start() throws IOException {
		constructClassAttribute();
		String yuml_string = results.toString();
		System.out.println(yuml_string);
		generateUML.start(yuml_string,output);
		
		
	}
	

	public void constructClassAttribute() {
		for (CompilationUnit cu: cuArray) {
			List<Node> nodes = cu.getChildNodes();
			if (nodes != null) {
        	
				for (Node n : nodes) {
					if (n instanceof ClassOrInterfaceDeclaration) {
						List<String> var = new ArrayList<>();
						CI=(ClassOrInterfaceDeclaration) n;
						n.getNodesByType(FieldDeclaration.class).stream().
						filter(f -> f.getModifiers().contains(Modifier.PRIVATE) ||f.getModifiers().contains(Modifier.PUBLIC)|| f.getModifiers().contains(Modifier.PROTECTED)). //&& 
						forEach(f -> var.add(f.toString()));
						String className= ((ClassOrInterfaceDeclaration) n).getName().toString();
						ca = new ClassAttribute(var,new ArrayList<String>(), new ArrayList<String>());
						
						//parse methods
						if (!((ClassOrInterfaceDeclaration) n).getMethods().isEmpty()) {
							for (MethodDeclaration methods: ((ClassOrInterfaceDeclaration) n).getMethods()) {
								String method_name =methods.getNameAsString();
								if (!(method_name.startsWith("set")|| method_name.startsWith("get"))) {
									System.out.println(methods.getNameAsString());
								}
							}
						}
						
						
						
						
						//parse extends, implements
						if (!((ClassOrInterfaceDeclaration) n).getExtendedTypes().isEmpty()) {
								for (int i = 0; i< (((ClassOrInterfaceDeclaration) n).getExtendedTypes().size()); i++ ) {
									String extends_name = ((ClassOrInterfaceDeclaration) n).getExtendedTypes(i).toString();
									extends_name ="["+extends_name+"]-^["+className+"]";
									results.add(extends_name);
								}
						}
						if (!((ClassOrInterfaceDeclaration) n).getImplementedTypes().isEmpty()) {
							for (int i = 0; i< (((ClassOrInterfaceDeclaration) n).getImplementedTypes().size()); i++ ) {
								String implements_name = ((ClassOrInterfaceDeclaration) n).getImplementedTypes(i).toString();
								implements_name ="[<<interface>>;"+implements_name+"]^-.-["+className+"]";
								results.add(implements_name);
							}
							
						}
						
						if (((ClassOrInterfaceDeclaration) n).isInterface()) {
							className="<<Interface>>" +className;
						}
						map.put(className,ca);
					}
				}
			}
		}
		
	}
	public void parseField() {
		for (String a: map.keySet()) {
			if (map.get(a).getvar() != null && map.get(a).getvar().size() !=0) {
			StringBuilder ca_string= new StringBuilder();
			ca_string.append('[');
			ca_string.append(a);
			ca_string.append('|');
				for (String s: map.get(a).getvar()){
						s = s.substring(0, s.length() - 1);
						String[] s_private= s.split(" ");
						String current = s_private[1];
						if (current.contains("<")) {
								results.add(parseCollection(current, a));
							}
						else if (map.containsKey(current)) {
							results.add(parseDep(current, a));
						}
						else {	
						ca_string.append(checkModifier(s));
						ca_string.append(s_private[2]);
						ca_string.append(":");
						
						if (s.contains("[")) {
							ca_string.append(s_private[1].substring(0, s_private[1].indexOf('[')));
							ca_string.append("(*)"); 
						}
						else {
								ca_string.append(s_private[1]);
							}
						ca_string.append(";");
						}
				}
				ca_string.append(']');
				//System.out.println(ca.toString());
				results.add(0,ca_string.toString());
			}
		}
		//for (String i: results) {
			//System.out.println(results.toString());
		//}
		
	}
	private String checkModifier(String s) {
		if (s.contains("private")){
			return "-";	
		}
		else if (s.contains("public")){
			return "+";
		}
		else if (s.contains("protected")){
			return "#";
		}
		return "";	
	}
	private String parseCollection(String s_private, String a) {
		StringBuilder ca_string= new StringBuilder();
		String next_class_name = s_private.substring(s_private.indexOf('<') + 1, s_private.indexOf('>'));
		if (map.containsKey(next_class_name)) {
			for (String next: map.get(next_class_name).getvar()) {
				if (next.contains(a) && !next.contains("<")) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]-*[");
					ca_string.append(next_class_name);
					ca_string.append(']');
					map.get(next_class_name).getvar().remove(next);
					return (ca_string.toString());
				}
				if (next.contains(a) && next.contains("<")) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]*-*[");
					ca_string.append(next_class_name);
					ca_string.append(']');
					map.get(next_class_name).getvar().remove(next);
					return (ca_string.toString());
				}
			}
		}
		return "";
	}
	private String parseDep(String s_private, String a) {
		StringBuilder ca_string= new StringBuilder();
		String next_class_name = s_private;
		if (map.containsKey(next_class_name)) {
			for (String next: map.get(next_class_name).getvar()) {
				if (next.contains(a) && !next.contains("<")) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]-1[");
					ca_string.append(next_class_name);
					ca_string.append(']');
					map.get(next_class_name).getvar().remove(next);
					return (ca_string.toString());
				}
				if (next.contains(a) && next.contains("<")) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]*-[");
					ca_string.append(next_class_name);
					ca_string.append(']');
					map.get(next_class_name).getvar().remove(next);
					return (ca_string.toString());
				}
			}
		}
		return "";
	}

}
