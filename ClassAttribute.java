import java.util.ArrayList;
import java.util.List;

public class ClassAttribute {
	public List<String> var;
	public List<String> method;
	public List<String> otherClass;
	public ClassAttribute(List<String> var, 
							List<String> method, List<String> otherClass){
		this.var = var;
		this.method = method;
		this.otherClass = otherClass;
	}
	public List<String> getvar () {
		return var;
	}
	public List<String> getmethod () {
		return method;
	}
	public List<String> getOtherClass () {
		return otherClass;
	}
}
