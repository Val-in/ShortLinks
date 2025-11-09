# URL Shortener CLI

---

## Supported Commands

- `create <url> [maxClicks] [ttlMinutes]` — create a short link
Example: create https://example.com 5 60
- `list` — show all your links
- `open <code>` — open the original URL in the browser (increases the click counter)
- `delete <code>` — delete a link (owner only)
- `edit <code> <new_maxClicks> <new_ttl_minutes>` — change limit/TTL (owner only)
- `help` — list of commands
- `exit` — exit

---

 ## Project Architecture

`├── cli`<br>
`│ ├── App.java` // Entry point, loads UUID and launches the CLI<br>
`│ └── CommandProcessor.java` // Parser and handler for user commands<br>
`│`<br>
`├── core`<br>
`│ ├── Config.java` // Loads configuration from config.properties<br>
`│ ├── UrlShortener.java` // Main business logic for URL shortening<br>
`│ ├── LinkModel.java` // Data model for a single short link<br>
`│ ├── LinkCleaner.java` // Background thread that removes expired/used-up links<br>
`│ └── ValidationException.java` // Input validation exception class<br>
`│`<br>
`├── storage`<br>
`│ └── InMemoryStore.java` // In-memory link storage (indexed by users)<br>
`│`<br>
`├── util`<br>
`│ └── FileUtils.java` // File system operations (UUID, configs)<br>
`│`<br>
`└── resources`<br>
`└── config.properties` // Settings for TTL, limits, paths, notifications<br>

---

Testing
Coverage: ≥50% of code (15 simple tests, JUnit 5).<br>
Test files are located in `src/test/...`
