package Jorge.MavenArtifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Common_Util {

	public int getOrderId() throws IOException {
		
		File file = new File("src/test/resources/TestAutomation.properties");
		FileInputStream fis = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fis);
		int orderId = Integer.parseInt(prop.getProperty("order_id"))+1;
		//Update in the properties file
		prop.setProperty("order_id", Integer.toString(orderId));
		
		return orderId;
	}
	
	
}
