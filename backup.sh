#!/bin/sh -e

location=/home/syncms/SyncMSea/backups//`date +%Y%m%d_%H%M%S`.db

mysqldump -u root -plkk19940326 twms > $location

gzip $location
