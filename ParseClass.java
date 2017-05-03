import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class ParseClass {
	
	private List<CompilationUnit> cuArray;
	private Map<String, ClassAttribute> map = new HashMap<>();
	ClassOrInterfaceDeclaration CI;
	ClassAttribute ca;
	String output;
	List<String> results = new ArrayList<>();
	List<String> extend_class = new ArrayList<>();
	
	public ParseClass(List<CompilationUnit> cuArray, String output) {
		this.cuArray = cuArray;
		this.output= output;
	}
	
	public void start() throws IOException {
		constructClassAttribute();
		parseField();
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
						List<String> method_list = new ArrayList<>();
						
						CI=(ClassOrInterfaceDeclaration) n;
						
						//parse field without comments
						List<FieldDeclaration> types = ((ClassOrInterfaceDeclaration) n).getFields();
						for (FieldDeclaration type : types)
						{
						        if (type instanceof FieldDeclaration && (type.getModifiers().contains(Modifier.PRIVATE) ||(type.getModifiers().contains(Modifier.PUBLIC))))
						        {
						        	String variable_string = "";
						        	if (type.getComment() != null) {
						        		variable_string= type.toString().replace(type.getComment().toString(),"");
						        	}
						        	else {
						        		variable_string = type.toString();
						        	}
						        	//System.out.print(variable_string);
						        	var.add(variable_string);
						        }
						}
						String className= ((ClassOrInterfaceDeclaration) n).getName().toString();
						
						//parse constructor
						 List<TypeDeclaration<?>> typeDeclarations = cu.getTypes();
						    for (TypeDeclaration typeDec : typeDeclarations) {
						        List<BodyDeclaration> members = typeDec.getMembers();
						        if(members != null) {
						            for (BodyDeclaration member : members) {
						                if (member instanceof ConstructorDeclaration) {
						                	String constructor_line = "";
						                    ConstructorDeclaration constructor = (ConstructorDeclaration) member;
						                    //System.out.println(constructor.getDeclarationAsString()); 
						                    if (constructor.getModifiers().toString().contains("PUBLIC")) {
						                    	constructor_line = "+" + constructor.getNameAsString() +"(";
						                    	for (int i = 0; i < constructor.getParameters().size() ; i ++) {
													String[] parameter_string = constructor.getParameter(i).toString().split(" ");
													constructor_line += parameter_string[1]+":"+parameter_string[0];
												}
						                    	constructor_line +=")";
						                    	method_list.add(constructor_line);
						                    }
						                }
						            }
						        }
						    }
						//parse methods
						if (!((ClassOrInterfaceDeclaration) n).getMethods().isEmpty()) {
							for (MethodDeclaration methods: ((ClassOrInterfaceDeclaration) n).getMethods()) {
								String method_name =methods.getNameAsString();
								if (!(method_name.startsWith("set")|| method_name.startsWith("get"))) {
									if (methods.getModifiers().toString().contains("PUBLIC"))
									{
											String method ="";
											method = "+"+methods.getNameAsString() + "(";
											for (int i = 0; i < methods.getParameters().size() ; i ++) {
												String[] parameter_string = methods.getParameter(i).toString().split(" ");
												method += parameter_string[1]+":"+parameter_string[0];
											}
											method +="):"+methods.getType().toString();
											method = method.replace('[', '(');
											method = method.replace(']', ')');
											method_list.add(method);
											//System.out.println(method);
											List<Node> child = methods.getChildNodes();
											for (Node a : child) {
												String body[] = a.toString().split(" ");
												for (String current : body) {
													if (map.containsKey("<<interface>>;"+current) && !((ClassOrInterfaceDeclaration) n).isInterface()) {
														String addition = "["+className+"]"+"use-.->"+ "[<<interface>>;"+current +"]";
														if (!results.contains(addition)) {
															results.add(addition);
														}
													}
												}
												
											}
											
									}
								}
							}
							
						}
						//ref:http://stackoverflow.com/questions/9955859/is-there-a-way-to-get-information-about-the-constructor-using-javaparser-or-some
						ca = new ClassAttribute(var, method_list);
						
						
						
						//parse extends, implements
						if (!((ClassOrInterfaceDeclaration) n).getExtendedTypes().isEmpty()) {
								for (int i = 0; i< (((ClassOrInterfaceDeclaration) n).getExtendedTypes().size()); i++ ) {
									String extends_name = ((ClassOrInterfaceDeclaration) n).getExtendedTypes(i).toString();
									extends_name ="["+extends_name+"]^-["+className+"]";
									results.add(extends_name);
									extend_class.add(className);
									
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
							className="<<interface>>;"+ className;
						}
						//System.out.println(className);
						map.put(className,ca);
						
					}
				}
			}
		}
		
	}
	public void parseField() {
		for (String a: map.keySet()) {
			//System.out.println(a);
			if (!a.contains("interface")) {
			StringBuilder ca_string= new StringBuilder();
			String addtion ="";
			ca_string.append('[');
			ca_string.append(a);
			if (map.get(a).getvar() != null && map.get(a).getvar().size() !=0) {
				ca_string.append('|');
				for (String s: map.get(a).getvar()){
						s = s.substring(0, s.length() - 1);
						String[] s_private= s.split(" ");
						String current = s_private[1];
						String current_interface = "<<interface>>;" + current;
						if (current.contains("<")) {
								results.add(parseCollection(current, a));
							}
						else if (map.containsKey(current)) {
							results.add(parseDep(current, a));
						}
						else if (map.containsKey(current_interface)) {
							results.add(parseDep(current_interface, a));
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
			}
			if (map.get(a).getmethod() != null && map.get(a).getmethod().size() !=0) {
				ca_string.append("|");
				for (String s: map.get(a).getmethod()){
						ca_string.append(s);
						int start = s.indexOf(':', 0);
						int end = s.indexOf(')',0);
						if (end >start) {
							String otherClass = s.substring((start) + 1, end);
					
							if (map.containsKey("<<interface>>;"+otherClass) && !a.contains("<<interface>>") ) {
								String addition_add ="["+a+"]"+"use-.->"+ "[<<interface>>;"+otherClass +"]";
								if (!results.contains(addition_add)) {
									addtion = addition_add;
								}
							}
							if (map.containsKey(otherClass) && !a.contains("<<interface>>")  &&!extend_class.contains(a)) {
								addtion ="["+a+"]"+"-"+ "["+otherClass +"]";
							}
						}
						ca_string.append(";");
				}
			}
			
			ca_string.append(']');
			results.add(0,ca_string.toString());
			results.add(addtion);
			}
		}
		
	}
	private String checkModifier(String s) {
		if (s.contains("private")){
			return "-";	
		}
		else if (s.contains("public")){
			return "+";
		}
		return "";	
	}
	private String parseCollection(String s_private, String a) {
		StringBuilder ca_string= new StringBuilder();
		String next_class_name = s_private.substring(s_private.indexOf('<') + 1, s_private.indexOf('>'));
		String possible_interface = "<<interface>>;" + next_class_name;
		if (map.containsKey(next_class_name)) {
			if (map.get(next_class_name).getvar().size() == 0) {
				ca_string.append('[');
				ca_string.append(a);
				ca_string.append("]-*[");
				ca_string.append(next_class_name);
				ca_string.append(']');
				return (ca_string.toString());
			}
			for (String next: map.get(next_class_name).getvar()) {
				if ((next.contains(a) && !next.contains("<")) || !next.contains(a)) {
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
		if (map.containsKey(possible_interface)) {
			if (map.get(possible_interface).getvar().size() == 0) {
				ca_string.append('[');
				ca_string.append(a);
				ca_string.append("]-*[");
				ca_string.append(possible_interface);
				ca_string.append(']');
				return (ca_string.toString());
			}
			for (String next: map.get(possible_interface).getvar()) {
				if ((next.contains(a) && !next.contains("<"))|| !next.contains(a)) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]-*[");
					ca_string.append(possible_interface);
					ca_string.append(']');
					map.get(possible_interface).getvar().remove(next);
					return (ca_string.toString());
				}
				if (next.contains(a) && next.contains("<")) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]*-*[");
					ca_string.append(possible_interface);
					ca_string.append(']');
					map.get(possible_interface).getvar().remove(next);
					return (ca_string.toString());
				}
			}
		}
		return "";
	}
	private String parseDep(String s_private, String a) {
		StringBuilder ca_string= new StringBuilder();
		String next_class_name = s_private;
		String possible_interface = "<<interface>>;" + next_class_name;
		if (map.containsKey(next_class_name)) {
			if (map.get(next_class_name).getvar().size() == 0) {
				ca_string.append('[');
				ca_string.append(a);
				ca_string.append("]-[");
				ca_string.append(next_class_name);
				ca_string.append(']');
				return (ca_string.toString());
			}
			for (String next: map.get(next_class_name).getvar()) {
				if ((next.contains(a) && !next.contains("<"))|| !next.contains(a)) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]-[");
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
		if (map.containsKey(possible_interface)) {
			
			if (map.get(possible_interface).getvar().size() == 0) {
				ca_string.append('[');
				ca_string.append(a);
				ca_string.append("]-1[");
				ca_string.append(possible_interface);
				ca_string.append(']');
				return (ca_string.toString());
			}
			for (String next: map.get(possible_interface).getvar()) {
				if ((next.contains(a) && !next.contains("<"))|| !next.contains(a)) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]-1[");
					ca_string.append(possible_interface);
					ca_string.append(']');
					map.get(possible_interface).getvar().remove(next);
					return (ca_string.toString());
				}
				if (next.contains(a) && next.contains("<")) {
					ca_string.append('[');
					ca_string.append(a);
					ca_string.append("]*-1[");
					ca_string.append(possible_interface);
					ca_string.append(']');
					map.get(possible_interface).getvar().remove(next);
					return (ca_string.toString());
				}
			}
		}
		return "";
	}

}
