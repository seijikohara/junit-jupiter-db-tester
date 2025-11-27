# AI Agent Instructions: JUnit Jupiter DB Tester

> This file contains coding standards and project conventions for AI coding assistants.

---

## Project Overview

### Purpose
A JUnit Jupiter extension for database testing with CSV-based test data management, built on DbUnit. Simplifies database tests with annotation-driven data preparation and validation.

### Key Features
- **Convention over Configuration**: Automatic file resolution based on test class/method names
- **Declarative Testing**: `@Preparation` and `@Expectation` annotations for data setup and validation
- **Scenario-Based Testing**: Share CSV files across multiple tests using scenario filtering (`[Scenario]` column)
- **Partial Column Validation**: Validate only specific columns, ignore auto-generated fields
- **Multi-Database Support**: Register and test multiple data sources simultaneously
- **Programmatic Assertions**: Advanced validation via `DatabaseAssertion` API
- **Spring Boot Integration**: Auto-configuration with automatic DataSource discovery

### Project Modules

| Module | Description | Documentation |
|--------|-------------|---------------|
| [`junit-jupiter-db-tester`](junit-jupiter-db-tester/) | Core library | [README](junit-jupiter-db-tester/README.md) |
| [`junit-jupiter-db-tester-bom`](junit-jupiter-db-tester-bom/) | Bill of Materials | [README](junit-jupiter-db-tester-bom/README.md) |
| [`junit-jupiter-db-tester-spring-boot-starter`](junit-jupiter-db-tester-spring-boot-starter/) | Spring Boot Starter | [README](junit-jupiter-db-tester-spring-boot-starter/README.md) |
| [`junit-jupiter-db-tester-examples`](junit-jupiter-db-tester-examples/) | Core library examples | [README](junit-jupiter-db-tester-examples/README.md) |
| [`junit-jupiter-db-tester-spring-boot-starter-example`](junit-jupiter-db-tester-spring-boot-starter-example/) | Spring Boot examples | [README](junit-jupiter-db-tester-spring-boot-starter-example/README.md) |

### Technology Stack
- **Java**: 21 (via Gradle toolchain)
- **Build Tool**: Gradle wrapper
- **Testing**: JUnit Jupiter, DbUnit, Testcontainers (integration tests)
- **Databases**: Any JDBC-compatible database (tested: H2, MySQL, PostgreSQL, Derby, HSQLDB)
- **Spring Boot**: 4 (for Spring Boot Starter)

---

## Code Style Rules

**All rules in this section are mandatory unless explicitly marked as Optional.**

---

### Null Safety (NullAway enforced)

#### Package-Level Null Configuration

**NullAway Configuration**: This project uses `option("NullAway:AnnotatedPackages", "io.github.seijikohara,example")` in `build.gradle.kts` to enable null-safety checking for all packages under `io.github.seijikohara` and `example`.

**Effect**:
- All types in annotated packages are non-null by default
- NullAway enforces null safety at compile time
- No `@NullMarked` annotations required in `package-info.java` files

**JSpecify Mode**: JSpecify support is enabled with `option("NullAway:JSpecifyMode", "true")` for enhanced null-safety semantics.

#### Public API vs Internal Code Null Checking Rules

**Package Boundary Rule**:
- **`api.*` packages (PUBLIC API)**: Use `Objects.requireNonNull()` for non-null parameters in all public/protected methods
- **All other packages (INTERNAL)**: No null checks needed; NullAway enforces at compile time

**Reason**:
- **PUBLIC API** (`api.*`): External users may not use NullAway; runtime validation prevents null pointer exceptions
- **INTERNAL** (`internal.*`): Protected by compile-time NullAway; explicit checks are redundant even if class uses `public` visibility

**Key Insight**: A class being `public` does NOT make it public API. Only classes in `api.*` packages are user-facing.

#### PUBLIC API Examples (api/ package)

**Scenario**: A public registry API in the `api/` package that external users call directly.

```java
package com.example.api;  // In api/ package - PUBLIC API

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jspecify.annotations.Nullable;

/**
 * Public registry API in api/ package (user-facing).
 *
 * <p>This class is in the PUBLIC API boundary, so all public methods
 * MUST validate parameters with Objects.requireNonNull().
 */
public final class WidgetRegistry {

  private final ConcurrentMap<String, Widget> registry = new ConcurrentHashMap<>();
  private volatile @Nullable Widget defaultWidget;

  /**
   * Registers a widget.
   *
   * @param name the name (must not be null)
   * @param widget the widget (must not be null)
   * @throws NullPointerException if name or widget is null
   */
  public void register(final String name, final Widget widget) {
    // REQUIRED: api/ package public methods need runtime validation
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(widget, "widget must not be null");

    registry.put(name, widget);
  }

  /**
   * Registers the default widget.
   *
   * @param widget the default widget (must not be null)
   * @throws NullPointerException if widget is null
   */
  public void registerDefault(final Widget widget) {
    // REQUIRED: api/ package public methods need runtime validation
    Objects.requireNonNull(widget, "widget must not be null");

    this.defaultWidget = widget;
    registry.put("_default", widget);
  }

  /**
   * Gets a widget by name.
   *
   * @param name the widget name (nullable)
   * @return the widget, or default if name is null
   */
  public Widget get(final @Nullable String name) {
    // @Nullable parameter: defensive handling required
    return Optional.ofNullable(name)
      .map(this::findWidget)
      .orElse(getDefault());
  }

  /**
   * Finds a widget by name.
   *
   * @param name the widget name (must not be null)
   * @return Optional containing widget if found
   * @throws NullPointerException if name is null
   */
  public Optional<Widget> find(final String name) {
    // REQUIRED: api/ package public methods validate non-null parameters
    Objects.requireNonNull(name, "name must not be null");

    return Optional.ofNullable(registry.get(name));
  }

  /**
   * Finds a widget internally (private helper).
   *
   * @param name the widget name (non-null, guaranteed by NullAway)
   * @return the widget, or null if not found
   */
  private @Nullable Widget findWidget(final String name) {
    // Private method: NullAway guarantees non-null, no check needed
    return registry.get(name);
  }

  /**
   * Gets the default widget.
   *
   * @return the default widget
   * @throws IllegalStateException if no default registered
   */
  private Widget getDefault() {
    // Private method: NullAway context, no parameter check needed
    return Optional.ofNullable(this.defaultWidget)
      .orElseThrow(() -> new IllegalStateException("No default widget registered"));
  }
}

/**
 * Settings record in api/ package (public API - always requires validation).
 *
 * @param name the setting name (must not be blank)
 * @param timeout the timeout in seconds (must be positive)
 */
public record AppSettings(String name, int timeout) {

  /**
   * Compact constructor with validation.
   *
   * @throws NullPointerException if name is null
   * @throws IllegalArgumentException if validation fails
   */
  public AppSettings {
    // REQUIRED: Records in api/ are public API - explicit validation needed
    Objects.requireNonNull(name, "name must not be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    if (timeout <= 0) {
      throw new IllegalArgumentException("timeout must be positive");
    }
  }

  /**
   * Checks if settings have long timeout.
   *
   * @return true if timeout exceeds 60 seconds
   */
  public boolean isLongRunning() {
    return timeout > 60;
  }
}

/**
 * Fictional Widget class for examples.
 */
public record Widget(String id) {
  public Widget {
    Objects.requireNonNull(id, "id must not be null");
  }
}
```

#### INTERNAL Code Examples (internal.*)

**Scenario**: Internal implementation classes in `internal.*` packages that are NOT public API, even if they use `public` visibility.

```java
package com.example.core;  // In internal/ package - INTERNAL

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

/**
 * Internal data processor (may use 'public' for inter-package access, but NOT public API).
 *
 * <p>This class is in an INTERNAL package. Even though it uses 'public' visibility
 * for access from other internal packages, it does NOT need Objects.requireNonNull()
 * because NullAway enforces null safety at compile time.
 */
public final class DataProcessor {

  private final Path basePath;

  /**
   * Creates a processor.
   *
   * @param basePath the base directory path (non-null, guaranteed by NullAway)
   */
  DataProcessor(final Path basePath) {
    // NO null check: INTERNAL package, NullAway guarantees non-null
    this.basePath = basePath;
  }

  /**
   * Processes data from path.
   *
   * @param path the file path (non-null, guaranteed by NullAway)
   * @return the processed data
   */
  public DataContainer process(final Path path) {
    // NO null check: INTERNAL code, NullAway enforces at compile time
    return new DataContainer(path.toString());
  }

  /**
   * Processes from optional path.
   *
   * @param path the file path (nullable)
   * @return the processed data, or empty if path is null
   */
  public DataContainer processOptional(final @Nullable Path path) {
    // @Nullable: Use Optional chain for null handling
    return Optional.ofNullable(path)
        .map(this::process)
        .orElseGet(DataContainer::empty);
  }

  /**
   * Private helper method.
   *
   * @param name the data name (non-null, guaranteed by NullAway)
   * @return the file path
   */
  private Path resolveFilePath(final String name) {
    // NO null check: private method + NullAway guarantee
    return basePath.resolve(name.concat(".dat"));
  }
}

/**
 * Internal data container (internal.* package).
 */
public record DataContainer(String path) {
  // NO Objects.requireNonNull(): INTERNAL record, NullAway enforces

  public static DataContainer empty() {
    return new DataContainer("");
  }
}
```

#### Null Safety Rules Summary

| Scope | Package | Null Checks | Reason |
|-------|---------|-------------|---------|
| **api.* packages - ALL public/protected methods** | `api.*` | `Objects.requireNonNull()` required | External users may not use NullAway |
| **api.* packages - Records** | `api.*` | `Objects.requireNonNull()` required in compact constructor | Public API records must validate |
| **internal.* packages - ALL methods/constructors** | `internal.*` | Not needed (NullAway only) | INTERNAL - NullAway enforces |
| **INTERNAL package records** | `internal.*` | Not needed (NullAway only) | Internal records protected by NullAway |
| **Private methods (all packages)** | Any | Not needed (NullAway only) | NullAway guarantees non-null |
| **@Nullable parameters (all)** | Any | `@Nullable` annotation required | Documents nullability contract |
| **Return values (prefer)** | Any | Prefer `Optional<T>` over `@Nullable` | Better API clarity |

**Core Principles**:
- **PUBLIC API boundary**: `api.*` packages only
- **Class visibility ≠ API boundary**: Only classes in `api.*` packages are user-facing
- **PUBLIC API packages** (`api.*`) require `Objects.requireNonNull()` for runtime validation
- **Other packages** (`internal.*`) rely solely on NullAway compile-time checking

#### Null Handling with Optional Chains

Use Optional chains instead of explicit null checks.

```java
// Anti-pattern: explicit null check with branching
if (schemaName != null) {
  return new SchemaName(schemaName);
} else {
  return null;
}

// Recommended: Optional chain
return Optional.ofNullable(schemaName)
    .map(SchemaName::new)
    .orElse(null);

// Anti-pattern: ternary operator for null check
final var schema = (schemaName != null) ? new SchemaName(schemaName) : null;

// Recommended: Optional chain
final var schema = Optional.ofNullable(schemaName)
    .map(SchemaName::new)
    .orElse(null);
```

Optional chains provide:
- Functional composition style
- Elimination of conditional branching
- Clear intent expression
- Stream API integration
- Null safety guarantees

### Language Basics
- **`final`**: ALL parameters and local variables must be `final`
- **`final var`**: Use for local variables when right-hand side clearly indicates type
  - Constructor calls (`new Foo()`), factory methods (`Foo.create()`), stream terminals (`.toList()`), method chains with clear return types
  - Avoid for complex expressions, method returns requiring type knowledge, null literals
  - `final var dataSet = new CsvDataSet(path);` — type clear from constructor
  - `final var result = processor.process(input);` — type unclear, avoid
- **Unnamed variables `_`**: For unused lambda parameters or catch blocks

### Records and Immutability
- **Records**: Immutable data carriers, thread-safe by design
  - Use compact constructors for validation and normalization
  - PUBLIC API records require runtime null checks (see Null Safety section)
  - Internal records rely on NullAway compile-time checking
  - Throw `IllegalArgumentException` for invalid data
  - Can have instance methods beyond data accessors
- **Immutability requirements for all classes**:
  - **All fields `final`**: No mutable state after construction
  - **No setters**: State cannot be modified after creation
  - **Return immutable collections**: Use `.toList()`, `List.copyOf()`, `Set.copyOf()`, `Map.copyOf()`
  - **Avoid returning arrays**: Arrays are mutable; use immutable collections instead (see Collections and Arrays section)
  - **Avoid builder pattern**: Builders are mutable; use constructors or factory methods with all required parameters
  - **Thread-safe by default**: Immutable objects are inherently thread-safe

### Pattern Matching and Switch
- **Switch expressions**: Use for mapping logic; avoid traditional switch statements
  - **Enum switches**: No `default` branch needed; compiler enforces exhaustiveness (all enum values must be covered)
  - **Non-enum switches**: `default` branch required; must throw `IllegalArgumentException` with descriptive message
  - **Rationale**: Fail fast on unexpected values instead of silent bugs
- **Primitive type patterns** (Preview feature):
  - Pattern matching with primitive types in `switch`
  - Guard clauses with `when` keyword
  - Type-safe primitive handling, avoid unsafe casts

### Constructors
- **Flexible constructor bodies**:
  - **Prologue**: Execute validation/computation BEFORE calling `super()` or `this()`
  - **Restrictions**: Cannot read instance fields, call instance methods, or reference `this` before super/this
  - **Benefits**: Direct validation without static helper methods
- **Validation order**: Null checks → Argument validation → super() → Additional initialization

### Text Formatting
- **Text blocks `"""`**: Multi-line strings with superior readability
  - **Trailing newline**: Closing `"""` on own line includes trailing newline
  - **No trailing newline**: Closing `"""` immediately after content
  - **Escape `\` at line end**: Suppress trailing newline
  - **Indentation**: Auto-stripped based on closing `"""` position
  - **Use `.formatted()`**: Modern alternative to `String.format()` for text blocks

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Widget subclass using flexible constructor bodies.
 */
public class ValidatedWidget extends Widget {

  private static final Logger logger = LoggerFactory.getLogger(ValidatedWidget.class);

  /**
   * Creates a validated widget.
   *
   * @param name the widget name (must not be blank)
   * @param size the widget size (must be positive)
   * @throws IllegalArgumentException if validation fails
   */
  public ValidatedWidget(final String name, final int size) {
    // Prologue: Validation BEFORE super() call
    if (name.isBlank()) {
      throw new IllegalArgumentException("name must not be blank");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("size must be positive");
    }

    // Call superclass constructor after validation
    super(name, size);

    // Epilogue: Additional initialization after super()
    logger.info("Created widget: {} with size {}", name, size);
  }
}

/**
 * Processes a data set.
 *
 * @param dataSet the data set to process
 */
public void process(final DataSet dataSet) {
  // final var for local variables
  final var tableName = dataSet.getTableName();
  final var rows = dataSet.getRows();
  // ... process logic
}

/**
 * Maps a status to its display name.
 *
 * @param status the status to map
 * @return the display name
 */
public String mapStatus(final Status status) {
  // Switch expression with enum: exhaustive, no default required
  return switch (status) {
    case ACTIVE -> "Active";
    case INACTIVE -> "Inactive";
    case PENDING -> "Pending";
    case ARCHIVED -> "Archived";
  };
}

/**
 * Example status enum for demonstration.
 */
enum Status {
  ACTIVE,
  INACTIVE,
  PENDING,
  ARCHIVED
}

/**
 * Maps a type string to its MIME type.
 *
 * @param type the file type
 * @return the MIME type
 * @throws IllegalArgumentException if type is unknown
 */
public String mapType(final String type) {
  // Switch expression with non-enum: default branch required
  return switch (type) {
    case "csv" -> "text/csv";
    case "json" -> "application/json";
    default -> throw new IllegalArgumentException(
        String.format("Unknown type: %s", type));
  };
}

/**
 * Classifies a numeric value using primitive type patterns.
 *
 * @param value the value to classify
 * @return classification string
 */
public String classifyNumber(final Object value) {

  // Primitive type patterns with guard clauses
  return switch (value) {
    case Integer i when i > 0 -> "positive integer";
    case Integer i when i < 0 -> "negative integer";
    case Integer i -> "zero";
    case Long l -> String.format("long value: %d", l);
    case Double d -> String.format("double value: %.2f", d);
    default -> "unknown type";
  };
}

/**
 * Processor with pattern matching (public API).
 */
public final class DataProcessor {

  private final List<String> patternNames;
  private final ProcessedResult defaultResult;

  /**
   * Creates a processor.
   *
   * @param patternNames the pattern names
   * @param defaultResult the default result
   */
  public DataProcessor(final List<String> patternNames, final ProcessedResult defaultResult) {
    this.patternNames = List.copyOf(patternNames);
    this.defaultResult = defaultResult;
  }

  /**
   * Example of unnamed variable for unused lambda parameter.
   *
   * @param name the name to process (nullable)
   * @return the processed result
   */
  public ProcessedResult processWithFilter(final @Nullable String name) {
    // Unnamed variable for unused lambda parameter
    return Optional.ofNullable(name)
        .filter(_ -> !patternNames.isEmpty())  // parameter not used
        .map(this::process)
        .orElse(defaultResult);
  }

  /**
   * Processes a name.
   *
   * @param name the name (non-null, guaranteed by caller)
   * @return the processed result
   */
  private ProcessedResult process(final String name) {
    return new ProcessedResult(name.toUpperCase());
  }
}

/**
 * Processed result record.
 *
 * @param value the processed value
 */
public record ProcessedResult(String value) {

  /**
   * Compact constructor.
   */
  public ProcessedResult {
    // Example: validation in compact constructor
  }
}

/**
 * Checks if connection is closed, suppressing SQLException.
 *
 * @param connection the connection to check
 * @return true if closed or if check fails
 */
public boolean isClosedSafely(final Connection connection) {

  // Unnamed variable in catch block
  try {
    return connection.isClosed();
  } catch (final SQLException _) {  // exception not used
    return false;
  }
}
```

### Collections and Arrays

#### Array Usage in Public APIs
- Avoid arrays (`String[]`, `int[]`) in public API parameters or return types
- Varargs (`String... args`) permitted for convenience methods
- Arrays are mutable, lack type safety (covariance), and provide limited API functionality

#### Producer (Return Types) - Be Specific and Immutable
Return the most specific **immutable** collection type that communicates intent:

| Return Type | Use When | Immutability |
|-------------|----------|--------------|
| `List<T>` | Ordered, may contain duplicates | Use `.toList()` or `List.copyOf()` |
| `Set<T>` | Unique elements | Use `Set.copyOf()` |
| `Map<K, V>` | Key-value pairs | Use `Map.copyOf()` |

**All public API must return immutable collections** (see Null Safety section for validation rules and Records and Immutability section for immutability requirements).

#### Consumer (Parameters) - Be Abstract
Accept the most **abstract** type that satisfies your needs:

| Need | Parameter Type | Reason |
|------|----------------|---------|
| Iteration only | `Iterable<T>` | Most flexible (Stream, Collection, custom) |
| Read-only collection access | `Collection<T>` | Accepts List, Set, Queue |
| Order/indexed access required | `List<T>` | Requires order guarantee |
| Uniqueness required | `Set<T>` | Requires unique elements |
| Key-value lookup | `Map<K, V>` | Requires associative access |

**Public API parameters**: Must validate with `Objects.requireNonNull()` (see Null Safety section).

#### Wildcard Guidelines (PECS)
- **Producer Extends**: `Collection<? extends T>` for read-only
- **Consumer Super**: `Collection<? super T>` for write-only
- **Use only when generic flexibility is required**: Avoid wildcards if concrete type works (e.g., prefer `Collection<String>` over `Collection<? extends String>` unless accepting subclass collections)

#### Examples

```java
/**
 * Gets all table names (Producer - specific and immutable).
 *
 * @return immutable list of table names, never null
 */
public List<String> getTableNames() {
  // Returns immutable List via .toList()
  return tables.stream()
      .map(Table::getName)
      .toList();
}

/**
 * Gets unique column names (Producer - Set for uniqueness).
 *
 * @return immutable set of column names, never null
 */
public Set<String> getUniqueColumnNames() {
  return Set.copyOf(columnNames);
}

/**
 * Processes items (Consumer - abstract Collection).
 *
 * @param items the items to process
 */
public void processItems(final Collection<String> items) {
  // Accepts any Collection implementation
  items.forEach(item -> logger.info("Processing: {}", item));
}

/**
 * Processes ordered items (Consumer - specific List for ordering).
 *
 * @param orderedItems the items in order
 */
public void processInOrder(final List<String> orderedItems) {
  // List provides indexed access
  for (int i = 0; i < orderedItems.size(); i++) {
    logger.info("Item {}: {}", i, orderedItems.get(i));
  }
}

/**
 * Validates unique identifiers (Consumer - Set for uniqueness).
 *
 * @param ids the unique identifiers
 * @return true if all valid
 */
public boolean validateIds(final Set<String> ids) {
  return ids.stream().allMatch(this::isValidId);
}

/**
 * Validates an ID.
 *
 * @param id the ID to validate (non-null, guaranteed by stream)
 * @return true if valid
 */
private boolean isValidId(final String id) {
  return id.matches("[A-Z][0-9]{3,}");
}

/**
 * Merges collections (Wildcard - read from source).
 *
 * @param target the target collection
 * @param source the source collection
 */
public void merge(
    final Collection<String> target,
    final Collection<? extends String> source) {
  target.addAll(source);
}
```

#### Anti-Patterns

```java
// Incorrect: array return type
public String[] getNames() {
  return names.toArray(new String[0]);
}

// Incorrect: array parameter
public void process(final String[] items) {
  // ...
}

// Incorrect: concrete implementation parameter
public void process(final ArrayList<String> list) {
  // Forces caller to use ArrayList
}

// Incorrect: mutable collection return
public List<String> getNames() {
  return this.names;  // Exposes internal mutable state
}

// Recommended: abstract parameter, immutable return
public List<String> transform(final Collection<String> items) {
  return items.stream()
      .map(String::toUpperCase)
      .toList();
}
```

### Functional Programming
- Prefer streams over loops
- Use `.toList()` instead of `.collect(Collectors.toList())`
- Prefer method references over lambdas when applicable
- **Lambda parameter names**:
  - Descriptive names for unclear context
  - Short names for obvious context (`i` for index, `e` for element, `s` for string)
  - `_` for unused parameters
  - Avoid cryptic abbreviations (`ds`, `d`)
- Avoid side effects in stream operations

```java
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Widget manager (example).
 */
public final class WidgetManager {

  private static final Logger logger = LoggerFactory.getLogger(WidgetManager.class);

  private final List<ManagedWidget> widgets;
  private final List<String> items;

  /**
   * Creates a manager.
   *
   * @param widgets the managed widgets
   * @param items the items
   */
  public WidgetManager(final List<ManagedWidget> widgets, final List<String> items) {
    this.widgets = List.copyOf(widgets);
    this.items = List.copyOf(items);
  }

  /**
   * Gets all active Widget names.
   *
   * @return immutable list of active Widget names
   */
  public List<String> getActiveWidgetNames() {
    // Method references with immutable .toList()
    return widgets.stream()
      .filter(ManagedWidget::isActive)
      .map(ManagedWidget::getName)
      .toList();
  }

  /**
   * Processes elements with indices.
   *
   * @return immutable list of processed results
   */
  public List<String> processWithIndices() {
    // Short name 'i' acceptable for obvious index context
    return IntStream.range(0, items.size())
      .mapToObj(i -> String.format("Item %d: %s", i, items.get(i)))
      .toList();
  }

  /**
   * Processes map entries, ignoring values.
   *
   * @param data the map to process
   */
  public void processKeys(final Map<String, Integer> data) {
    // Underscore for unused parameter
    data.forEach((key, _) -> logger.info("Processing key: {}", key));
  }

  /**
   * Gets all active Widget names (anti-pattern).
   *
   * @return list of active Widget names
   */
  public List<String> getActiveWidgetNamesWrong() {
    // Incorrect: cryptic abbreviations
    return widgets.stream()
      .filter(w -> w.isActive())     // 'w' is cryptic
      .map(x -> x.getName())         // 'x' is cryptic
      .collect(Collectors.toList()); // less clear than .toList()
  }
}

/**
 * Managed Widget wrapper.
 */
record ManagedWidget(String name, Widget widget, boolean active) {
}
```

### Class Structure (Order)
1. Constants
2. Instance fields
3. Constructors
4. Public methods
5. Package-private/protected methods
6. Private methods
7. Nested classes/records

### Import Statements

**Use explicit class imports for clarity and tool compatibility.**

#### Import Rules

1. **Explicit class imports (Required)**: Import specific classes from packages
   - All classes from standard library and third-party libraries
   - Provides clear dependencies, no ambiguity, full tool support
   - One import per line, sorted alphabetically

2. **Fully qualified names (Exception only)**: Use in-line fully qualified class names only to resolve ambiguity
   - Disambiguates classes with same name from different packages
   - Example: `java.lang.annotation.Annotation` vs `java.text.Annotation`
   - Avoid for general code; prefer imports

3. **Star imports (Forbidden)**: Avoid wildcard imports (`import java.util.*;`)
   - Causes unclear dependencies, potential name conflicts, poor IDE support

#### Examples

```java
// Recommended: explicit class imports
import java.util.List;
import java.util.Collection;
import java.util.Optional;
import com.example.context.AppContext;
import com.example.service.ServiceConnection;

/**
 * Service setup with explicit imports.
 */
public final class ServiceSetup {

  /**
   * Sets up the service connection.
   *
   * @param jdbcConnection the JDBC connection
   */
  public void setup(final Connection jdbcConnection) {
    final var context = AppContext.getInstance();
    final var connection = new ServiceConnection(jdbcConnection);
    // ... setup logic
  }
}

// Recommended: fully qualified name to resolve ambiguity
import java.lang.annotation.Annotation;

/**
 * Annotation resolver handling potential ambiguity.
 */
public final class AnnotationResolver {

  /**
   * Finds an annotation.
   *
   * @param <T> the annotation type
   * @param annotationClass the annotation class
   * @return Optional containing the annotation if found
   */
  private <T extends Annotation> Optional<T> findAnnotation(
      final Class<T> annotationClass) {
    return Optional.empty();
  }

  /**
   * Alternative: fully qualified name inline for rare cases.
   *
   * @param <T> the annotation type
   * @param annotationClass the annotation class
   * @return Optional containing the annotation if found
   */
  private <T extends java.lang.annotation.Annotation> Optional<T> findAnnotationAlt(
      final Class<T> annotationClass) {
    return Optional.empty();
  }
}

// Incorrect: star imports
import java.util.*;  // Forbidden

// Incorrect: fully qualified names throughout code
/**
 * Service setup (anti-pattern).
 */
public final class ServiceSetupWrong {

  /**
   * Sets up the service connection.
   *
   * @param jdbcConnection the JDBC connection
   */
  public void setup(final Connection jdbcConnection) {
    // Incorrect: use imports instead of fully qualified names
    final var context = com.example.context.AppContext.getInstance();
    final var connection = new com.example.service.ServiceConnection(jdbcConnection);
  }
}
```

### Naming
- Classes: `PascalCase`
- Methods/variables: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Packages: `lowercase`

### Error Handling

#### Exception Types
- `IllegalArgumentException`: Invalid arguments
- `IllegalStateException`: Invalid state/configuration
- Custom exceptions: Domain-specific errors (in `api.exception` package)

#### Framework Exception Hierarchy
**All framework exceptions extend from `DatabaseTesterException`** (base exception):

```
api/exception/
├── DatabaseTesterException         # Base exception for all framework errors
├── ConfigurationException          # Configuration/initialization failures
├── DataSetLoadException            # Data loading/parsing failures
├── DataSourceNotFoundException     # Missing DataSource registration
└── ValidationException             # Data validation/assertion failures
```

Only framework exceptions may be thrown to user code. Internal exceptions (DbUnit, JDBC) must be caught and wrapped.

#### Exception Handling Rules

1. **Catch specific exceptions only**:
   - Avoid broad exception catching: `catch (final Exception e)`
   - List each exception type explicitly: `catch (final SQLException | DataSetException e)`
   - Use broad catch only when necessary with clear justification

2. **Avoid unnecessary runtime exception catches**:
   - Avoid catching `RuntimeException`, `NullPointerException`, `IllegalArgumentException` without specific intent
   - Let runtime exceptions propagate naturally to reveal programming errors

3. **Wrap external exceptions at boundary**:
   - Catch external library exceptions (DbUnit, JDBC) at the facade boundary
   - Wrap in framework exceptions before throwing to user code
   - Example: `DatabaseUnitException` → `DatabaseTesterException`

4. **Preserve stack traces**:
   - Chain exceptions with cause parameter
   - Avoid swallowing exceptions without logging or re-throwing

5. **Avoid re-throwing unchanged exceptions**:
   - Anti-pattern: `catch (final ValidationException e) { throw e; }` — no value added
   - If catching only to re-throw unchanged, remove the catch block
   - Re-throwing acceptable when:
     - Adding context via wrapping: `throw new ValidationException("Additional context: " + detail, e);`
     - Performing cleanup/logging before re-throwing
     - Converting checked to unchecked exceptions (or vice versa)
   - Let exceptions propagate naturally unless adding value

#### Examples

```java
// Recommended: specific exception types listed
public void execute(final ScenarioDataSet dataSet, final Operation operation,
                   final DataSource dataSource) {
  try {
    executor.execute(dataSet, operation, dataSource, schema);
  } catch (final DatabaseUnitException | SQLException e) {
    throw new DatabaseTesterException(
        String.format("Failed to execute operation: %s", operation), e);
  }
}

// Recommended: wrap external exceptions at facade boundary
public IDataSet toDbUnit(final DataSet dataSet) throws DataSetException {
  try {
    // ... conversion logic
    dbTable.addRow(rowValues);
  } catch (final DataSetException e) {
    throw new RuntimeException("Failed to add row to table: " + tableName, e);
  }
}

// Recommended: specific exception type
private Path resolveClasspathDirectory(final String location) {
  return Optional.ofNullable(classLoader.getResource(resourcePath))
      .map(resourceUrl -> {
        try {
          return Path.of(resourceUrl.toURI());
        } catch (final java.net.URISyntaxException e) {
          throw new DataSetLoadException(
              String.format("Failed to convert classpath resource to Path: %s", resourceUrl), e);
        }
      })
      .orElseThrow(() -> new DataSetLoadException("Resource not found: " + location));
}

// Incorrect: broad Exception catch without justification
public void process(final DataSet dataSet) {
  try {
    // ...
  } catch (final Exception e) {  // Too broad
    throw new DatabaseTesterException("Failed", e);
  }
}

// Incorrect: unnecessary runtime exception catch
public String getName() {
  try {
    return widget.getName();
  } catch (final NullPointerException e) {  // NullAway prevents this
    return "unknown";
  }
}

// Incorrect: swallowing exception
public boolean isValid() {
  try {
    validate();
    return true;
  } catch (final ValidationException e) {  // Exception swallowed
    return false;  // No logging or context
  }
}
```

#### Error Messages
- Provide contextual messages with relevant values and context
- Use `String.format()` instead of string concatenation (`+`)
  - Embedding variables: `String.format("Value: %s", value)`
  - Multiple values: `String.format("%s/%s/%s.csv", dir, cls, table)`
- Chain exceptions to preserve stack traces with cause parameter

#### Text Blocks for Multi-line Messages (Optional)
Use text blocks with `.formatted()` for complex error messages:
```java
final var errorMessage = """
    Failed to load dataset:
      File: %s
      Reason: %s
    """.formatted(fileName, reason);
```

```java
/**
 * Validates and retrieves a Widget.
 *
 * @param name the Widget name
 * @return the Widget
 * @throws IllegalArgumentException if name is empty
 * @throws IllegalStateException if Widget not registered
 */
public Widget getWidget(final String name) {
  // Validate arguments
  if (name.isEmpty()) {
    throw new IllegalArgumentException("Widget name cannot be empty");
  }

  // Validate state
  if (!widgets.containsKey(name)) {
    throw new IllegalStateException(
        String.format("No Widget registered with name: %s", name));
  }

  return widgets.get(name);
}

/**
 * Loads data from a file.
 *
 * @param path the file path
 * @return the loaded data
 * @throws DataLoadException if loading fails
 */
public DataContainer loadData(final Path path) throws DataLoadException {
  try {
    // Load file and create container
    return new DataContainer(path.toFile(), Set.of());
  } catch (final IOException cause) {
    // Chain exceptions to preserve stack trace
    throw new DataLoadException(
        String.format("Failed to load data: %s", path), cause);
  }
}

/**
 * Internal helper (package-private) - no null checks needed.
 *
 * @param itemName the item name (non-null, guaranteed by NullAway)
 * @return the formatted item name
 */
String formatItemName(final String itemName) {
  // Package-private: NullAway guarantees non-null, no check needed
  return itemName.toUpperCase();
}
```

### Documentation Style

**All documentation must be simple, technical, and formal.**

This principle applies to:
- **Javadoc**: Class and method documentation
- **Code comments**: Inline explanations
- **Technical documents**: AGENTS.md, README.md, design documents
- **Commit messages**: Git commit descriptions

#### Style Requirements

1. **Simplicity**: Use concise, direct statements without redundant explanations
2. **Technical accuracy**: Precise terminology, no ambiguous language
3. **Formal tone**: Professional technical writing, no casual expressions
4. **Clarity**: One concept per sentence, avoid compound explanations

#### Prohibited Elements

- Casual expressions: "don't", "let's", "just", "simply"
- Exclamation marks except in code examples showing anti-patterns
- Conversational phrases: "you should", "we recommend" (use imperative: "use", "avoid")
- Redundant modifiers: "very", "really", "quite"

#### Preferred Patterns

| Avoid | Use |
|-------|-----|
| "You should use X" | "Use X" |
| "Don't do this" | "Incorrect pattern" or "Anti-pattern" |
| "Let's implement X" | "Implement X" |
| "Simply call the method" | "Call the method" |
| "Very important" | "Critical" or "Required" |

### Javadoc

**Strictly enforced** by DocLint with `-Xwerror` for all code (including test code).

#### Documentation Requirements
- **All classes**: Overview, purpose, and usage examples
- **All methods (including private)**: Must have `@param`, `@return`, `@throws`
- **Code examples**: Use `<pre>{@code ...}</pre>`
- **Structure**: `<h2>` sections, `<p>` paragraphs, `<ul><li>` lists
- **Language**: English only

#### Test Code Documentation
- All test classes and nested test classes must have constructors with Javadoc
- All test methods must have Javadoc with `@param` and `@throws` tags
- Follow Documentation Style requirements (see above)
- See [junit-jupiter-db-tester/src/test/AGENTS.md](junit-jupiter-db-tester/src/test/AGENTS.md) for test-specific guidelines

#### Example

```java
/**
 * Widget registry (example).
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * WidgetContext.getInstance()
 *     .registerWidget(widget);
 * }</pre>
 *
 * @see WidgetManager
 */
public final class WidgetContext {

  /**
   * Registers a Widget.
   *
   * @param name unique identifier
   * @param widget the Widget to register
   * @throws IllegalArgumentException if name is empty
   * @throws IllegalStateException if name already registered
   */
  public void registerWidget(final String name, final Widget widget) {
    // ...
  }
}
```

### Logging

Use SLF4J for all logging.

**Logger Initialization**:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger logger = LoggerFactory.getLogger(ClassName.class);
```

**Best Practices**:
- Use parameterized logging instead of string concatenation: `logger.info("User {} logged in", username)`
- Name loggers after their enclosing class
- Logger field: `private static final Logger logger`
- Include exception as last parameter: `logger.error("Failed to process", exception)`

**Log Levels**:
- `logger.error()`: Errors preventing normal operation (with stack trace)
- `logger.warn()`: Potential issues without execution interruption
- `logger.info()`: Important business events, lifecycle events
- `logger.debug()`: Detailed diagnostic information (disabled in production)
- `logger.trace()`: Very detailed diagnostic information (rarely used)

**Examples**:
```java
// Recommended: parameterized logging
logger.info("Loading dataset from {}", path);
logger.error("Failed to connect to database: {}", dbUrl, exception);

// Incorrect: string concatenation
logger.info("Loading dataset from " + path);
```

### Thread Safety

All stateful classes must be thread-safe:
- Prefer immutable objects (see Records and Immutability section — inherently thread-safe)
- Use concurrent collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`) when mutability required
- Return immutable collections: `.toList()`, `List.copyOf()`, `Set.copyOf()` for public API returns
- Avoid synchronization; let concurrent collections handle thread safety

**Best Practices**:
- Use `ConcurrentHashMap` for read-heavy, occasionally-written maps
- Concurrent collections make the collection thread-safe, not the contents
- Prefer `java.util.concurrent` utilities over manual locks
- Most classes should be immutable; mutable stateful classes are exceptional

**Example**:
```java
/**
 * Thread-safe registry (example).
 */
public final class WidgetRegistry {
  private final ConcurrentHashMap<String, Widget> widgets = new ConcurrentHashMap<>();

  /**
   * Registers a Widget.
   *
   * @param name the name
   * @param widget the Widget
   */
  public void register(final String name, final Widget widget) {
    widgets.put(name, widget);
  }

  /**
   * Gets all registered Widget names.
   *
   * @return immutable list of names
   */
  public List<String> getNames() {
    return List.copyOf(widgets.keySet());  // Immutable snapshot
  }
}
```

---

## Development Workflow

### Build and Test Commands

```bash
# Full build (compile + test + format check)
./gradlew build

# Format code (required before commit)
./gradlew spotlessApply

# Run all tests
./gradlew test

# Core library only
./gradlew :junit-jupiter-db-tester:build
./gradlew :junit-jupiter-db-tester:test

# Examples/integration tests only
./gradlew :junit-jupiter-db-tester-examples:test

# Spring Boot Starter only
./gradlew :junit-jupiter-db-tester-spring-boot-starter:build
./gradlew :junit-jupiter-db-tester-spring-boot-starter:test

# Spring Boot Starter Example only
./gradlew :junit-jupiter-db-tester-spring-boot-starter-example:test

# Javadoc generation
./gradlew javadoc
```

### Static Analysis (Auto-enforced)

All static analysis runs automatically during compilation. Violations cause build failures.

| Tool | Enforces | Related Section |
|------|----------|-----------------|
| **NullAway** | Null safety at compile time | [Null Safety](#null-safety-nullaway-enforced) |
| **DocLint** | Complete Javadoc for public API | [Javadoc](#javadoc) |
| **Spotless** | Google Java Format | All code formatting |
| **Error Prone** | Disabled (except NullAway) | N/A |

**How to fix violations**:
1. NullAway errors → Check Null Safety section, add `@Nullable` for nullable parameters
2. DocLint errors → Add missing `@param`, `@return`, `@throws` tags
3. Spotless errors → Run `./gradlew spotlessApply`

### Test Structure

| Directory | Purpose | Database | Guidelines |
|-----------|---------|----------|------------|
| `junit-jupiter-db-tester/src/test/java` | Unit tests for library code | Mock only (no real DB) | [junit-jupiter-db-tester/src/test/AGENTS.md](junit-jupiter-db-tester/src/test/AGENTS.md) |
| `junit-jupiter-db-tester-examples/src/test/java/example/` | Usage examples, framework demonstrations | H2 in-memory | Example-driven tests |
| `junit-jupiter-db-tester-examples/src/test/java/example/{db}/` | Database-specific integration tests | Testcontainers (MySQL/PostgreSQL) | Real database tests |
| `junit-jupiter-db-tester-spring-boot-starter/src/test/java` | Spring Boot Starter unit tests | Mock only | Auto-configuration tests |
| `junit-jupiter-db-tester-spring-boot-starter-example/src/test/java` | Spring Boot integration examples | H2 in-memory | Spring Data JPA tests |

**Unit Test Requirements** (junit-jupiter-db-tester/src/test/java):
- Fast (< 1 second per test)
- No database connections - use Mockito
- Test one class in isolation
- See [junit-jupiter-db-tester/src/test/AGENTS.md](junit-jupiter-db-tester/src/test/AGENTS.md) for detailed guidelines

---

## Git Workflow

### Commit Format (Conventional Commits)

```
<type>: <description>

[optional body]

[optional footer]
```

**Types**: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `build`, `ci`, `perf`, `style`

**Examples**:
```bash
feat: add PostgreSQL JSONB support
fix: resolve NPE in DataSet loader
docs: update MySQL configuration guide

refactor: simplify table ordering logic

Replace recursive algorithm with iterative approach
for better performance.

Closes #42
```

### Pre-Commit Checklist

Complete before every commit:

1. **Format code**: `./gradlew spotlessApply`
2. **Build succeeds**: `./gradlew build` (runs tests + static analysis)
3. **Commit message**: Use Conventional Commits format
4. **Atomic commits**: Each commit should be focused on one logical change

### CI Requirements

All pull requests and commits must pass:

```bash
./gradlew build           # Includes:
                          # - Compilation with NullAway + DocLint
                          # - All tests (unit + integration)
                          # - Spotless format check
./gradlew javadoc         # Javadoc generation must succeed
```

**CI failures troubleshooting**:
- **Test failures** → Check test output, fix failing tests
- **NullAway errors** → Add null checks or `@Nullable` (see Null Safety section)
- **Spotless errors** → Run `./gradlew spotlessApply` locally
- **DocLint errors** → Add missing Javadoc tags (see Documentation section)
