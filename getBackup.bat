set yyyyMMdd=%date:/=%
set backupDir=\\MyServer\pcBk\MyMachine\%yyyyMMdd%\
mkdir %backupDir%
wbadmin start backup -backupTarget:%backupDir% -include:C:,D:,E: -quiet
