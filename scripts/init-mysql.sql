-- Run as MySQL root once if you use the local MySQL80 service instead of Docker:
--   mysql -u root -p < scripts/init-mysql.sql

CREATE DATABASE IF NOT EXISTS lanka_ride CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'lankaride'@'localhost' IDENTIFIED BY 'lankaride';
CREATE USER IF NOT EXISTS 'lankaride'@'%' IDENTIFIED BY 'lankaride';
GRANT ALL PRIVILEGES ON lanka_ride.* TO 'lankaride'@'localhost';
GRANT ALL PRIVILEGES ON lanka_ride.* TO 'lankaride'@'%';
FLUSH PRIVILEGES;
