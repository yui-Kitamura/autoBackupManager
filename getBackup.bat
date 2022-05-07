set yyyyMMdd=%date:/=%
set backupDir=\\MyServer\pcBk\dell3650\%yyyyMMdd%\
mkdir %backupDir%
wbadmin start backup -backupTarget:%backupDir% -include:C:,D:,E: -quiet
