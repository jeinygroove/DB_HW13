# Simple app with Exposed

Created using Kotlin+Exposed for Postgres DB

## Usage

1. Create SQLCourseDatabase.sql at first using 
script from the repository.

2. Run with (example)

`./gradlew run --args="jdbc:postgresql://localhost:5432/sqlcourse org.postgresql.Driver postgres" --console=plain -q`

   `--args` should be a string from next parameters: url, driver, username and password(optional, if you don't have it will be set to 
empty string by default).
 
### Commands

Use `;` as separator (couldn't find smth better)

| Command       | Description  |
| ---------------- |:-------------:|
| c     | show all course names and their ids in asc order by name |
| s      | show all student names |
| g ; Course Name | show all student names who passed this course and their grades|
|g ; Course Name ; Last Name| show student grade for the course exam|
|u ; Course Name; Last Name; Grade| Set/Update grade for this student and course|