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
./gradlew bootRun --spring.profiles.active=dev
```