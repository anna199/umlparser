package SequenceDiagram;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.SourceStringReader;

public class SequenceUMLparser {
	private static String inputUML ="";
	public static void addString(String info){
		inputUML += info;
	}
	public static void run() throws IOException
	{
		String output = "UMLSequenceParser.png";
		OutputStream png = new FileOutputStream(output);
		String source = "@startuml\n";
		source += inputUML;
		source += "@enduml\n";

		SourceStringReader reader = new SourceStringReader(source);
		String desc = reader.generateImage(png);
	}
}
// Modified from http://plantuml.com/api PNG generation from String