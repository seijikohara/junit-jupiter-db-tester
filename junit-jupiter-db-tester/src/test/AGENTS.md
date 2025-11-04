# Unit Test Requirements

> **CRITICAL**: This document defines guidelines for **unit testing ONLY**, NOT integration testing.
>
> For general coding standards (Java style, documentation, error handling, etc.), see [../../AGENTS.md](../../AGENTS.md).
> All rules in the parent AGENTS.md are mandatory and apply to test code as well.

---

## Quick Reference

- [JUnit Jupiter User Guide](https://docs.junit.org/current/user-guide/)
- [Mockito Javadoc](https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html)

---

## Unit Test Principles

### What is a Unit Test?

- Tests **one unit** (class/method) in isolation
- **No external dependencies**: No database, no file I/O, no network
- **Fast**: < 100ms (ideal), < 1 second (acceptable)
- **Deterministic**: Same input = same output, always
- **Independent**: Can run in any order, parallel execution safe

### Unit Test vs Integration Test

| Aspect | Unit Test (`lib/src/test/java`) | Integration Test (`example/src/test/java`) |
|--------|----------------------------------|-------------------------------------------|
| **Database** | Always mocked (Mockito) | Real DB (H2/Testcontainers) |
| **File I/O** | `@TempDir` or mocked | Real files allowed |
| **Speed** | < 1 second | Seconds to minutes |
| **Purpose** | Verify logic in isolation | Verify system integration |

---

## Test Structure & Naming

### Test Class Structure

```java
/**
 * Unit tests for {@link DataSetLoader}.
 */
@DisplayName("DataSetLoader")
class DataSetLoaderTest {

    // 1. Test instance fields (mocks, test data)
    @Mock
    private Connection connection;

    private DataSetLoader loader;

    // 2. @BeforeEach setup
    @BeforeEach
    void setUp() {
        loader = new DataSetLoader();
    }

    // 3. @Nested classes for each method
    @Nested
    @DisplayName("load(Path) method")
    class LoadMethod {

        /** Tests for the load method with Path parameter. */
        LoadMethod() {}

        @Test
        @Tag("normal")
        @DisplayName("should return dataset when file exists")
        void shouldReturnDataSet_whenFileExists() {
            // Given
            final var path = Path.of("test.csv");

            // When
            final var result = loader.load(path);

            // Then
            assertNotNull(result);
        }

        @Test
        @Tag("error")
        @DisplayName("should throw exception when file not found")
        void shouldThrowException_whenFileNotFound() {
            // Given
            final var invalidPath = Path.of("nonexistent.csv");

            // When & Then
            assertThrows(DataSetLoadException.class,
                () -> loader.load(invalidPath));
        }
    }

    @Nested
    @DisplayName("load(String) method")
    class LoadWithStringMethod {

        /** Tests for the load method with String parameter. */
        LoadWithStringMethod() {}

        // Tests...
    }
}
```

### Naming Conventions

**Test Class Names**:
- Pattern: `<ClassName>Test`
- Example: `DataSetLoader` → `DataSetLoaderTest`
- Must have class-level Javadoc and `@DisplayName`

**Test Method Names**:
- Pattern: `should<Action><Object>_<condition>`
- Start with `should` prefix
- Use descriptive names in camelCase
- Examples:
  - `shouldReturnDataSet_whenFileExists()`
  - `shouldThrowException_whenNullArgument()`
  - `shouldCreateConnection_withValidDataSource()`

**@Nested Class Names**:
- Pattern: `<MethodName>Method` (e.g., `LoadMethod`, `GetDataSourceMethod`)
- Alternative: `When<Condition>` (e.g., `WhenFileNotFound`)
- For overloaded methods: Include parameter type (e.g., `LoadWithStringMethod`, `LoadWithPathMethod`)
- Must have constructor with Javadoc
- Must have `@DisplayName`

---

## JUnit Jupiter Annotations

### Test Methods

- `@Test` - Standard test
- `@ParameterizedTest` - Multiple inputs/outputs with same logic
- `@RepeatedTest` - Repeated execution
- `@TestFactory` - Dynamic tests

### Lifecycle Hooks

**@BeforeEach / @AfterEach** (Preferred):
- Use when: Each test needs fresh state
- Ensures test isolation
- Resets state between tests

**@BeforeAll / @AfterAll**:
- Use when: Expensive setup shared across all tests
- Must be `static` (unless using `@TestInstance(Lifecycle.PER_CLASS)`)
- Example: Loading test configuration

### Organization Annotations

**@DisplayName** (STRONGLY RECOMMENDED):
- Use on all test classes and methods
- English only
- Use BDD-style format: `"should ... when ..."` (lowercase, descriptive)
- Example: `@DisplayName("should return empty list when no data exists")`
- May omit only when method name is self-documenting

**@Nested** (RECOMMENDED):
- Use for grouping tests by method or scenario
- One `@Nested` class per public method
- Non-static inner classes
- Must have constructor with Javadoc
- Must have `@DisplayName`
- Avoid excessive nesting (1-2 levels max)

**@Tag** (RECOMMENDED):
- Categorize tests: `"normal"`, `"edge-case"`, `"error"`, `"slow"`
- `"normal"`: Standard success paths
- `"edge-case"`: Boundary conditions, handled exceptions
- `"error"`: Method throws exception
- `"slow"`: Tests > 1 second
- Order tests: normal → edge-case → error

### Parameterized Tests

**When to Use @ParameterizedTest**:
- Multiple test cases with same logic, different data
- Makes tests more compact AND clearer
- No complex conditional branching needed

**When NOT to Use**:
- Only one test case (use `@Test`)
- Complex conditional logic required
- Different setup/verification per case

**Argument Sources**:
- `@ValueSource` - Simple arrays
- `@EnumSource` - Enum values
- `@MethodSource` - Custom provider method
  - Naming: `<testMethodName>Provider`
  - Location: Same `@Nested` class
  - Must be `static`
- `@CsvSource` - Inline CSV
- `@NullSource`, `@EmptySource`, `@NullAndEmptySource`

Example:
```java
@ParameterizedTest
@MethodSource("validPathsProvider")
@DisplayName("should load dataset with valid paths")
void shouldLoadDataSet_withValidPaths(final Path path) {
    final var result = loader.load(path);
    assertNotNull(result);
}

static Stream<Path> validPathsProvider() {
    return Stream.of(
        Path.of("test1.csv"),
        Path.of("test2.csv")
    );
}
```

---

## Assertions

### Static Imports (REQUIRED)

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
```

### Assertion Methods

**Equality & Comparison**:
- `assertEquals(expected, actual)` - Value equality
- `assertEquals(expected, actual, "message")` - With message
- `assertNotEquals(unexpected, actual)`
- `assertSame(expected, actual)` - Reference identity
- **Parameter order**: Expected first, actual second

**Boolean & Null**:
- `assertTrue(condition)`
- `assertFalse(condition)`
- `assertNull(actual)`
- `assertNotNull(actual)`

**Exceptions**:
```java
// Verify exception is thrown
final var exception = assertThrows(
    IllegalArgumentException.class,
    () -> loader.load(null)
);
assertEquals("Path cannot be null", exception.getMessage());

// Verify exact type
assertThrowsExactly(DataSetLoadException.class, () -> loader.load(invalidPath));

// Verify no exception
assertDoesNotThrow(() -> loader.load(validPath));
```

**Grouping** (RECOMMENDED):
```java
// All assertions execute, all failures reported
assertAll("User properties",
    () -> assertEquals("John", user.getName()),
    () -> assertEquals(30, user.getAge()),
    () -> assertNotNull(user.getEmail())
);
```

**Type Validation**:
```java
assertInstanceOf(CsvDataSet.class, result);
```

### Assertion Messages

- Optional but RECOMMENDED for complex assertions
- Last parameter: `assertEquals(expected, actual, "Descriptive message")`
- Use lambda for expensive computation: `assertEquals(expected, actual, () -> "Expensive: " + compute())`
- Lazy evaluation with `Supplier<String>`

### Best Practices

- **One logical assertion per test** - Each test verifies one behavior
- Use `assertAll()` for multiple aspects of same behavior
- Provide messages for non-obvious assertions
- Avoid magic numbers - use named constants

---

## Mocking with Mockito

### Setup

```java
@ExtendWith(MockitoExtension.class)
class DataSetLoaderTest {

    @Mock
    private Connection connection;

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private DataSetLoader loader;
}
```

### When to Mock

**ALWAYS Mock**:
- Database connections
- External services (network, API calls)
- File system operations (when not using `@TempDir`)
- Components with side effects

**DO NOT Mock**:
- Value objects (DTOs, records)
- Simple data structures
- The class under test
- Java standard library (String, List, etc.)

### Mock Behavior

**Stubbing**:
```java
// Return value
when(dataSource.getConnection()).thenReturn(connection);

// Throw exception
when(connection.isClosed()).thenThrow(new SQLException("Connection closed"));

// Multiple calls
when(loader.load(any())).thenReturn(dataSet1, dataSet2);
```

**Verification**:
```java
// Verify method called
verify(dataSource).getConnection();

// Verify with arguments
verify(loader).load(eq(expectedPath));

// Verify number of calls
verify(connection, times(2)).close();
verify(connection, never()).commit();

// Verify no more interactions
verifyNoMoreInteractions(connection);
```

### Best Practices

- Mock only external dependencies
- Prefer constructor injection over field injection
- Use `@InjectMocks` for automatic dependency injection
- Name mock fields clearly (match class field names)
- Don't over-mock - keep it simple

---

## File I/O in Tests

### Temporary Files (@TempDir)

```java
@Test
@DisplayName("should create file in temp directory")
void shouldCreateFile_inTempDirectory(@TempDir final Path tempDir) {
    // Given
    final var testFile = tempDir.resolve("test.csv");

    // When
    loader.save(dataSet, testFile);

    // Then
    assertTrue(Files.exists(testFile));
}
```

### Test Resources

**Location**: `src/test/resources/<TestClassName>/`

**Example**: For `DataSetLoaderTest.java`:
- Place files in: `src/test/resources/DataSetLoaderTest/`
- Structure:
  ```
  src/test/resources/
    DataSetLoaderTest/
      valid-data.csv
      invalid-data.csv
      edge-cases/
        empty.csv
  ```

**Loading**:
```java
final var stream = getClass().getResourceAsStream(
    "/DataSetLoaderTest/valid-data.csv");
```

---

## Test Scope

### What to Test

**Test Target Methods**:
- All public methods
- All protected methods
- Package-private methods with significant behavior/business logic

**DO NOT Test**:
- Private methods (if hard to test, design is wrong)
- Annotation definitions (`@interface`)
- `package-info.java`, `module-info.java`
- Simple enums (without complex logic)
- Simple exception classes (only calling parent constructors)
- Records (simple data holders)
- Facade/utility classes (only wrapping other implementations)
- Interfaces without default methods (test via implementations)

### Constructor Testing

Test constructors when they:
- Validate arguments
- Initialize complex state
- Throw exceptions

```java
@Test
@Tag("error")
@DisplayName("should throw exception when name is null")
void shouldThrowException_whenNameIsNull() {
    assertThrows(IllegalArgumentException.class,
        () -> new DataSetLoader(null));
}

@Test
@Tag("normal")
@DisplayName("should initialize with default configuration")
void shouldInitializeWithDefaults() {
    final var loader = new DataSetLoader();
    assertNotNull(loader.getConfiguration());
}
```

---

## Test Isolation & Independence

### CRITICAL Requirements

- Tests must run independently (any order, parallel-safe)
- Each test self-contained with own setup/cleanup
- No shared mutable state between tests
- Use `@BeforeEach` to reset state
- Use `@AfterEach` to clean up resources

### Anti-patterns to AVOID

- Static fields persisting between tests (unless reset)
- Tests modifying global configuration without restoration
- Tests depending on side effects from other tests
- Shared mutable objects without cleanup

---

## Performance Guidelines

### Speed Requirements

- Individual unit tests: **< 100ms** (ideal), **< 1 second** (acceptable)
- Full test suite: Seconds, not minutes
- Tag slow tests: `@Tag("slow")`

### Keep Tests Fast

**DO**:
- Avoid `Thread.sleep()` - use mocks instead
- Avoid file I/O - use in-memory alternatives
- Avoid network calls - always mock
- Keep test data minimal - only what's necessary

**DO NOT**:
- No real database connections
- No external service calls
- No application context startup

---

## Test Data Management

### Test Data Principles

- Use realistic but minimal datasets
- Document non-obvious data choices
- No magic values - use named constants
- Consider builders/factories for complex objects

### Example

```java
// Constants for test data
private static final String VALID_TABLE_NAME = "TEST_USER";
private static final int MIN_ROW_COUNT = 0;
private static final int MAX_ROW_COUNT = 1000;

// Factory method
private DataSet createTestDataSet() {
    return DataSet.builder()
        .tableName(VALID_TABLE_NAME)
        .rows(List.of())
        .build();
}
```

---

## Coverage Requirements

- Target: **Branch coverage** (not just line coverage)
- Aim for comprehensive coverage of all conditional paths
- Focus on meaningful coverage, not just percentage targets
- Ensure all error paths and edge cases tested

---

## Documentation Requirements

> All documentation rules from [../../AGENTS.md](../../AGENTS.md) apply.

**Test Classes**:
- Must have class-level Javadoc
- Must have `@DisplayName`

**@Nested Classes**:
- Must have constructor with Javadoc describing test scenario
- Must have `@DisplayName`

**Test Methods**:
- Must have Javadoc with `@param` and `@throws` if applicable
- Comments should be simple, technical, formal
- English only

Example:
```java
/**
 * Unit tests for {@link DataSetLoader}.
 */
@DisplayName("DataSetLoader")
class DataSetLoaderTest {

    @Nested
    @DisplayName("load(Path) method")
    class LoadMethod {

        /**
         * Tests for the load method with Path parameter.
         */
        LoadMethod() {}

        /**
         * Verifies that load returns a dataset when file exists.
         *
         * @throws IOException if test file cannot be created
         */
        @Test
        @Tag("normal")
        @DisplayName("should return dataset when file exists")
        void shouldReturnDataSet_whenFileExists() throws IOException {
            // Test implementation
        }
    }
}
```

---

## Prohibited Practices

- `@SuppressWarnings` - Never use in test code
- Unnecessary `throws` clauses - Only for specific checked exceptions
- `System.out.println()` - Use logging framework
- Hard-coded file paths - Use `@TempDir` or classpath resources
- `@Disabled` without explanation - Add comment with reason and timeline
- Real database connections - Always mock
- String concatenation with `+` - Use `String.format()` (per parent AGENTS.md)

---

## Pre-Test Checklist

Before committing tests:

1. Run `./gradlew spotlessApply`
2. Run `./gradlew :lib:test` - all tests pass
3. All tests < 1 second
4. No database connections used
5. All mocks properly configured
6. Javadoc complete (DocLint enforced)
7. Follow parent AGENTS.md standards

---

## Common Patterns

### Testing Exception Messages

```java
@Test
@Tag("error")
@DisplayName("should throw exception with correct message when invalid input")
void shouldThrowExceptionWithMessage_whenInvalidInput() {
    final var exception = assertThrows(
        IllegalArgumentException.class,
        () -> loader.load(null)
    );

    final var expected = "Path cannot be null";
    assertEquals(expected, exception.getMessage());
}
```

### Testing Optional Returns

```java
@Test
@Tag("normal")
@DisplayName("should return empty optional when not found")
void shouldReturnEmptyOptional_whenNotFound() {
    final var result = registry.find("nonexistent");
    assertTrue(result.isEmpty());
}

@Test
@Tag("normal")
@DisplayName("should return present optional when found")
void shouldReturnPresentOptional_whenFound() {
    final var result = registry.find("existing");
    assertTrue(result.isPresent());
    assertEquals("value", result.get());
}
```

### Testing with Multiple Mocks

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {

    @Mock
    private Repository repository;

    @Mock
    private Validator validator;

    @InjectMocks
    private Service service;

    @Test
    @Tag("normal")
    @DisplayName("should call validator and repository in order")
    void shouldCallComponentsInOrder() {
        // Given
        when(validator.isValid(any())).thenReturn(true);
        when(repository.save(any())).thenReturn(savedEntity);

        // When
        service.process(entity);

        // Then
        final var inOrder = inOrder(validator, repository);
        inOrder.verify(validator).isValid(entity);
        inOrder.verify(repository).save(entity);
    }
}
```
