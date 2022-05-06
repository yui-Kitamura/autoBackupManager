package pro.eng.yui_kitamura.autoBkMng;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Properties;

import pro.eng.yui_kitamura.autoBkMng.batch.Job;

public class AutoBackupManager{

	public static Logger logger = System.getLogger("autoBkMng");
	
	private static Properties props;
	
	/** init */
	static {
		loadProps();
	}
	
	public static void main(String[] args) {
		
		logger.log(Level.INFO, "Auto Backup Manager START running -->");
		
		Job job = new Job();
		try {
			job.run();
		}catch(RuntimeException e) {
			logger.log(Level.ERROR, e);
			logger.log(Level.ERROR, "job ends with the ERROR");
		}
		logger.log(Level.INFO, "Auto Backup Manager END running <--");
	}
	
	public static String getProp(String key) {
		String val = props.getProperty(key);
		logger.log(Level.DEBUG, "PROP:"+ key + "="+ val);
		return val;
	}
	
	private static void loadProps() {
		
		//resourceフォルダ直下の絶対パスを指定する「/」
		InputStream fileStream = AutoBackupManager.class.getResourceAsStream("/autoBkMng.properties");
		props = new Properties();
		try {
			props.load(fileStream);
		} catch (IOException e) {
			logger.log(Level.ERROR, "can not load propertie file");
			logger.log(Level.ERROR, e);
		}
	}
	
}