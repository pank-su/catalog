# Transport Route Management System

A desktop application for managing transport routes and depots built with JavaFX and SQLite.

## Features

- Manage routes and route points
- Add and view depots
- Linked list implementation for route management
- SQLite database for data persistence

## Prerequisites

- Java 17 or higher
- Gradle (wrapper included)

## Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd catalog
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

## Running the Application

To run the application:
```bash
./gradlew run
```

## Build Commands

- Build project: `./gradlew build`
- Run application: `./gradlew run`
- Clean build: `./gradlew clean`
- Run tests: `./gradlew test` (no tests currently exist)
- Check/verify: `./gradlew check`

## Project Structure

```
src/
├── main/
│   └── java/
│       └── su/
│           └── pank/
│               └── transport/
│                   ├── data/
│                   │   ├── models/          # Data models (Route, RoutePoint, etc.)
│                   │   └── repository/     # Data access layer
│                   ├── domain/             # Domain logic (RouteLinkedList)
│                   └── ui/                 # User interface (views and viewmodels)
└── test/                                  # Test directory (empty for now)
```

## Technologies Used

- Java 17
- JavaFX for GUI
- SQLite for database
- Gradle for build management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run `./gradlew build` to ensure everything works
5. Submit a pull request

## License

This project is licensed under the MIT License.