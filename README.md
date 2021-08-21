# Run the application locally

### 1. Local DB installation
1. Install mysql
```
CREATE USER 'evochia_developer'@'localhost' IDENTIFIED BY 'apassword';
CREATE DATABASE evochia_dev;
CREATE DATABASE evochia_test;
GRANT ALL ON evochia_dev.* TO 'evochia_developer'@'localhost';
GRANT ALL ON evochia_test.* TO 'evochia_developer'@'localhost';
  ```
3. clean flyway schema `./gradlew flywayClean`
4. run schema migration `./gradlew flywayMigrate --stacktrace`

### 2. Run the application  

```
./gradlew bootRun -Dspring.profiles.active=dev
```

-----------------
## how to query the db and check for uuid values in human readable format:

example:
```sql
SELECT
  LOWER(CONCAT(
    SUBSTR(HEX(client_id), 1, 8), '-',
    SUBSTR(HEX(client_id), 9, 4), '-',
    SUBSTR(HEX(client_id), 13, 4), '-',
    SUBSTR(HEX(client_id), 17, 4), '-',
    SUBSTR(HEX(client_id), 21)
  ))
FROM client_credentials;

```