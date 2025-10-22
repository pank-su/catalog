# Agent Guidelines for Transport Route Management System

## Build Commands
- Build project: `./gradlew build`
- Run application: `./gradlew run`
- Clean build: `./gradlew clean`
- Run tests: `./gradlew test` (no tests currently exist)
- Run single test: `./gradlew test --tests "*TestClass.testMethod"` (when tests are added)
- Check/verify: `./gradlew check`

## Code Style Guidelines

### Language & Framework
- Java 17 with JavaFX for desktop GUI
- Gradle build system
- SQLite database with JDBC

### Package Structure
- `su.pank.transport` - main package
- `domain` - domain models and business logic (RouteLinkedList)
- `data/models` - data models (Route, RoutePoint, etc.)
- `data/repository` - data access layer
- `ui` - user interface layer (views and viewmodels)

### Naming Conventions
- Classes: PascalCase (Route, DatabaseManager)
- Methods/Variables: camelCase (getRouteNumber, startPoint)
- Constants: UPPER_SNAKE_CASE
- Packages: lowercase with dots

### Imports
- JavaFX imports first
- Standard library imports second
- Custom package imports last
- No wildcard imports

### Error Handling
- Use try-catch for database operations
- Log exceptions with printStackTrace() for debugging
- Return boolean success flags from database methods
- Show user-friendly alerts for GUI errors

### Code Formatting
- 4-space indentation
- Opening braces on same line
- Descriptive variable names
- Single responsibility methods
- Use JavaFX properties for data binding

### Database
- Use PreparedStatement for SQL queries
- Foreign key constraints for data integrity
- AUTOINCREMENT for primary keys
- NULL handling for optional fields