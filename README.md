# Run the application locally

### 1. Local DB installation
1. Install mysql
2. Create database `evochia_dev`
3. clean flyway schema `./gradlew flywayClean`
4. run schema migration `./gradlew flywayMigrate --stacktrace`

### 2. Run the application  

```
./gradlew bootRun --spring.profiles.active=dev
```