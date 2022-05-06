package pro.eng.yui_kitamura.autoBkMng.batch;

import static pro.eng.yui_kitamura.autoBkMng.AutoBackupManager.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.nio.file.Path;

public class Job{
	
	private Path bkFilePath;
	
	public void run() {
		
		logger.log(Level.INFO, "batch Job START running -->");
		
		findPath();
	
		
		logger.log(Level.INFO, "batch Job END running <--");
		
	}
	
	private void findPath() {
		
		String bkPathProp = getProp("backupDir");
		
		File dir = new File(bkPathProp);
		if(!dir.exists()) {
			String warnMsg = "prop backupDir does not exists";
			logger.log(Level.WARNING, warnMsg);
			throw new RuntimeException(new FileNotFoundException(warnMsg));
		}
		if(!dir.isDirectory()) {
			String warnMsg = "prop backupDir is not a directory";
			logger.log(Level.WARNING, warnMsg);
			throw new RuntimeException(new IOException(warnMsg));
		}
		
		bkFilePath = dir.toPath();
		logger.log(Level.INFO, bkFilePath);
	}
	
}