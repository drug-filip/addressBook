package address.book.util;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetPropertyValue {
 
	public String getPropValues( String key ) throws IOException {
 
		String result = "";
		Properties prop = new Properties();
		String propFileName = "config.properties";
 
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		prop.load(inputStream);
		if (inputStream == null) {
			throw new IOException("property file '" + propFileName + "' not found in the classpath");
		}
  
		// get the property value
		result = prop.getProperty( key );
		return result;
	}
}