import java.util.ArrayList;
import java.util.List;

public class ClassAttribute {
	public List<String> private_var;
	public List<String> public_var;
	public List<String> method;
	
	public ClassAttribute(List<String> private_var, 
							List<String> public_var, 
							List<String> method){
		private_var = this.private_var;
		public_var = this.public_var;
		method = this.method;
	}
	

}
