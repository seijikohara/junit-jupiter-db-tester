package io.github.seijikohara.dbtester.spring.autoconfigure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Unit tests for {@link SpringBootDatabaseTestExtension}. */
@DisplayName("SpringBootDatabaseTestExtension")
class SpringBootDatabaseTestExtensionTest {

  /** The extension under test. */
  private SpringBootDatabaseTestExtension extension;

  /** Creates a new test instance. */
  SpringBootDatabaseTestExtensionTest() {}

  /** Sets up test fixtures before each test. */
  @BeforeEach
  void setUp() {
    extension = new SpringBootDatabaseTestExtension();
  }

  /** Tests for SpringBootDatabaseTestExtension constructor. */
  @Nested
  @DisplayName("constructor")
  class Constructor {

    /** Tests for SpringBootDatabaseTestExtension constructor. */
    Constructor() {}

    /** Verifies that constructor creates instance successfully. */
    @Test
    @Tag("normal")
    @DisplayName("should create instance successfully")
    void shouldCreateInstance_successfully() {
      final var result = new SpringBootDatabaseTestExtension();

      assertNotNull(result);
    }
  }

  /** Tests for the beforeAll method. */
  @Nested
  @DisplayName("beforeAll(ExtensionContext) method")
  class BeforeAllMethod {

    /** Tests for the beforeAll method. */
    BeforeAllMethod() {}

    /** Verifies that beforeAll throws NullPointerException when context is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw NullPointerException when context is null")
    @SuppressWarnings("NullAway")
    void shouldThrowNullPointerException_whenContextIsNull() {
      final @Nullable ExtensionContext nullContext = null;

      final var exception =
          assertThrows(NullPointerException.class, () -> extension.beforeAll(nullContext));

      assertEquals("context must not be null", exception.getMessage());
    }

    /** Verifies that beforeAll skips registration when Spring context not available. */
    @Test
    @Tag("normal")
    @DisplayName("should skip registration when Spring context not available")
    void shouldSkipRegistration_whenSpringContextNotAvailable() {
      final ExtensionContext extensionContext = mock(ExtensionContext.class);

      try (MockedStatic<SpringExtension> mockedSpringExtension =
          mockStatic(SpringExtension.class)) {
        mockedSpringExtension
            .when(() -> SpringExtension.getApplicationContext(extensionContext))
            .thenThrow(new IllegalStateException("No Spring context"));

        assertDoesNotThrow(() -> extension.beforeAll(extensionContext));
      }
    }

    /** Verifies that beforeAll skips registration when DataSourceRegistrar bean not found. */
    @Test
    @Tag("normal")
    @DisplayName("should skip registration when DataSourceRegistrar bean not found")
    void shouldSkipRegistration_whenDataSourceRegistrarBeanNotFound() {
      final ExtensionContext extensionContext = mock(ExtensionContext.class);
      final ApplicationContext applicationContext = mock(ApplicationContext.class);

      try (MockedStatic<SpringExtension> mockedSpringExtension =
          mockStatic(SpringExtension.class)) {
        mockedSpringExtension
            .when(() -> SpringExtension.getApplicationContext(extensionContext))
            .thenReturn(applicationContext);
        when(applicationContext.containsBean("dataSourceRegistrar")).thenReturn(false);

        assertDoesNotThrow(() -> extension.beforeAll(extensionContext));

        verify(applicationContext, never()).getBean(DataSourceRegistrar.class);
      }
    }
  }
}
