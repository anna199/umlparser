
import java.util.List;

public class ClassAttribute {
	public List<String> var;
	public List<String> method;
	public ClassAttribute(List<String> var, 
							List<String> method){
		this.var = var;
		this.method = method;
	}
	public List<String> getvar () {
		return var;
	}
	public List<String> getmethod () {
		return method;
	}
}
