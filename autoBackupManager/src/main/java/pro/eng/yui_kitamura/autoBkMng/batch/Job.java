package pro.eng.yui_kitamura.autoBkMng.batch;

import static pro.eng.yui_kitamura.autoBkMng.AutoBackupManager.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Job{
	
	private Path bkFilePath;
	
	public void run() {
		
		logger.log(Level.INFO, "batch Job START running -->");
		
		findPath();
		
		deleteOld();
		
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
	
	private void deleteOld() {
		
		List<Path> machineList = new ArrayList<>();

		//get machine list
		File[] machines = bkFilePath.toFile().listFiles();
		logger.log(Level.INFO, machines.length + " machines dir has found.");
		logger.log(Level.INFO, "machine name list --> [");
		for(File machine : machines) {
			if(!machine.isDirectory()) {
				logger.log(Level.WARNING, "found file「"+ machine.getPath() +"」is not a directory");
				logger.log(Level.WARNING, "skip the file.");
				continue;
			}
			logger.log(Level.INFO, machine.getName());
			machineList.add(machine.toPath());
		}
		logger.log(Level.INFO, "] <-- machine name list");

		//delete on each machine
		for(Path machinePath : machineList) {
			deleteOnEachMachine(machinePath);
		}
	}
	
	private void deleteOnEachMachine(Path machinePath) {
		logger.log(Level.INFO, "start delete operation on machine「"+ machinePath.getFileName() +"」");
		
		File[] dateDirs = machinePath.toFile().listFiles();
		List<Path> existBkDatePath = new ArrayList<>();
		for(File dateDir : dateDirs) {
			File winImgDir = new File(dateDir.getPath() + File.separator + "WindowsImageBackup");
			if(!winImgDir.exists()) {
				logger.log(Level.INFO, "no winImgBk folder found under machine "+ machinePath.getFileName() 
							+", date "+ dateDir.getName());
				continue;
			}
			try {
				Integer.parseInt(dateDir.getName());
			}catch(NumberFormatException nfe) {
				String errMsg = "dir name format is not a date "+ nfe.getMessage();
				logger.log(Level.WARNING, errMsg);
				continue;
			}
			existBkDatePath.add(dateDir.toPath());
		}
		
		//check delete target
		existBkDatePath.sort((p1, p2) -> {
			int p1i = Integer.parseInt(p1.getFileName().toString());
			int p2i = Integer.parseInt(p2.getFileName().toString());
			return p1i - p2i;
		});
		
		/* 残す条件：
		 * 最新
		 * 各月最終
		 * 過去1週間すべて
		 */
		List<Path> deleteTargetList = new ArrayList<>();
		Calendar aWeekAgo = Calendar.getInstance();
		aWeekAgo.add(Calendar.DAY_OF_MONTH, -7);
		for(int idx = 0; idx < existBkDatePath.size()-1; idx++) {
			int curDate = Integer.parseInt(existBkDatePath.get(idx).getFileName().toString());
			int nextDate = Integer.parseInt(existBkDatePath.get(idx + 1).getFileName().toString());
			
			Calendar curCal = Calendar.getInstance();
			curCal.set(Calendar.YEAR, curDate/1_00_00);
			curCal.set(Calendar.MONTH, (curDate - curDate/1_00_00*1_00_00)/1_00 -1 );
			curCal.set(Calendar.DAY_OF_MONTH, (curDate - curDate/1_00*1_00));
			
			if(curCal.after(aWeekAgo)) {
				logger.log(Level.INFO, "save the data on "+ existBkDatePath.get(idx).getFileName());
				continue;
			}
			
			if(curDate / 100 != nextDate / 100) {
				logger.log(Level.INFO, "save the data on "+ existBkDatePath.get(idx).getFileName());
				continue;
			}
			deleteTargetList.add(existBkDatePath.get(idx));
		}
		logger.log(Level.INFO, "save the data on "+ existBkDatePath.get(existBkDatePath.size()-1).getFileName());
		
		for(Path deleteTarget : deleteTargetList) {
			logger.log(Level.INFO, "delete the dir "+ deleteTarget);
			try {
				deleteDir(deleteTarget);
			} catch (IOException ioe) {
				logger.log(Level.ERROR, ioe);
			}
		}
	
	}
	
	private void deleteDir(Path target) throws IOException {
		
		File targetDir = target.toFile();
		File[] childlen =  targetDir.listFiles();
		if(childlen == null) {
			return;
		}
		for(File f : childlen) {
			deleteDir(f.toPath());
			try {
				Files.delete(f.toPath());
			}catch(NoSuchFileException e) {
				continue;
			}
		}
		
		Files.delete(target);
	}
	
}