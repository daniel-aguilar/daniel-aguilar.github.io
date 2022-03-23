#!/bin/bash
SIZE=10G
RCV_AREA=/opt/oracle/oradata/rcv_area
mkdir -m 750 $RCV_AREA
sqlplus / as sysdba << EOF
  SHUTDOWN IMMEDIATE;
  STARTUP MOUNT;
  ALTER DATABASE ARCHIVELOG;
  ALTER SYSTEM SET DB_RECOVERY_FILE_DEST_SIZE=$SIZE SCOPE=BOTH SID='*';
  ALTER SYSTEM SET DB_RECOVERY_FILE_DEST='$RCV_AREA' SCOPE=BOTH SID='*';
  ALTER DATABASE FLASHBACK ON;
  ALTER DATABASE OPEN;
  exit;
EOF