import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.imageio.ImageIO;

public class generateUML {
	public static void start(String yuml_string, String output) throws IOException {
		String imageUrl = "https://yuml.me/diagram/plain/class/"+yuml_string+".png";
		String destinationFile = output+".png";  
		URL url = new URL(imageUrl);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
			//System.out.println(length);
		}

		is.close();
		os.close();
		//ref: http://www.avajava.com/tutorials/lessons/how-do-i-save-an-image-from-a-url-to-a-file.html
	}

}
