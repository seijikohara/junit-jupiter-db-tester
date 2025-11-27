package io.github.seijikohara.dbtester.spring.autoconfigure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.seijikohara.dbtester.api.config.DataSourceRegistry;
import java.util.Map;
import javax.sql.DataSource;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/** Unit tests for {@link DataSourceRegistrar}. */
@DisplayName("DataSourceRegistrar")
class DataSourceRegistrarTest {

  /** Mock DataSource for testing. */
  private DataSource mockDataSource1;

  /** Mock DataSource for testing multiple DataSources. */
  private DataSource mockDataSource2;

  /** DataSourceRegistry for verifying registration (real instance, not mock). */
  private DataSourceRegistry registry;

  /** Mock ApplicationContext for testing. */
  private ConfigurableApplicationContext mockApplicationContext;

  /** Mock BeanFactory for testing primary bean detection. */
  private ConfigurableListableBeanFactory mockBeanFactory;

  /** Mock BeanDefinition for testing primary bean detection. */
  private BeanDefinition mockBeanDefinition1;

  /** Mock BeanDefinition for testing primary bean detection. */
  private BeanDefinition mockBeanDefinition2;

  /** Properties with auto-registration enabled. */
  private DatabaseTesterProperties propertiesEnabled;

  /** Properties with auto-registration disabled. */
  private DatabaseTesterProperties propertiesDisabled;

  /** The registrar under test (initialized in individual tests). */
  @SuppressWarnings("NullAway.Init")
  private DataSourceRegistrar registrar;

  /** Creates a new test instance. */
  DataSourceRegistrarTest() {}

  /** Sets up test fixtures before each test. */
  @BeforeEach
  void setUp() {
    mockDataSource1 = mock(DataSource.class);
    mockDataSource2 = mock(DataSource.class);
    registry = new DataSourceRegistry();
    mockApplicationContext = mock(ConfigurableApplicationContext.class);
    mockBeanFactory = mock(ConfigurableListableBeanFactory.class);
    mockBeanDefinition1 = mock(BeanDefinition.class);
    mockBeanDefinition2 = mock(BeanDefinition.class);
    propertiesEnabled = new DatabaseTesterProperties(true, true);
    propertiesDisabled = new DatabaseTesterProperties(true, false);
  }

  /** Tests for the DataSourceRegistrar constructor. */
  @Nested
  @DisplayName("constructor")
  class Constructor {

    /** Tests for the DataSourceRegistrar constructor. */
    Constructor() {}

    /** Verifies that constructor throws NullPointerException when properties is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw NullPointerException when properties is null")
    @SuppressWarnings("NullAway")
    void shouldThrowNullPointerException_whenPropertiesIsNull() {
      final @Nullable DatabaseTesterProperties nullProperties = null;

      final var exception =
          assertThrows(NullPointerException.class, () -> new DataSourceRegistrar(nullProperties));

      assertEquals("properties must not be null", exception.getMessage());
    }

    /** Verifies that constructor creates instance with valid properties. */
    @Test
    @Tag("normal")
    @DisplayName("should create instance with valid properties")
    void shouldCreateInstance_withValidProperties() {
      final var result = new DataSourceRegistrar(propertiesEnabled);

      assertNotNull(result);
    }
  }

  /** Tests for the setApplicationContext method. */
  @Nested
  @DisplayName("setApplicationContext(ApplicationContext) method")
  class SetApplicationContextMethod {

    /** Tests for the setApplicationContext method. */
    SetApplicationContextMethod() {}

    /** Verifies that setApplicationContext throws NullPointerException when context is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw NullPointerException when applicationContext is null")
    @SuppressWarnings("NullAway")
    void shouldThrowNullPointerException_whenApplicationContextIsNull() {
      registrar = new DataSourceRegistrar(propertiesEnabled);
      final @Nullable ConfigurableApplicationContext nullContext = null;

      final var exception =
          assertThrows(
              NullPointerException.class, () -> registrar.setApplicationContext(nullContext));

      assertEquals("applicationContext must not be null", exception.getMessage());
    }

    /** Verifies that setApplicationContext sets context successfully. */
    @Test
    @Tag("normal")
    @DisplayName("should set application context successfully")
    void shouldSetApplicationContext_successfully() {
      registrar = new DataSourceRegistrar(propertiesEnabled);

      assertDoesNotThrow(() -> registrar.setApplicationContext(mockApplicationContext));
    }
  }

  /** Tests for the registerAll method. */
  @Nested
  @DisplayName("registerAll(DataSourceRegistry) method")
  class RegisterAllMethod {

    /** Tests for the registerAll method. */
    RegisterAllMethod() {}

    /** Verifies that registerAll throws NullPointerException when registry is null. */
    @Test
    @Tag("error")
    @DisplayName("should throw NullPointerException when registry is null")
    @SuppressWarnings("NullAway")
    void shouldThrowNullPointerException_whenRegistryIsNull() {
      registrar = new DataSourceRegistrar(propertiesEnabled);
      registrar.setApplicationContext(mockApplicationContext);
      final @Nullable DataSourceRegistry nullRegistry = null;

      final var exception =
          assertThrows(NullPointerException.class, () -> registrar.registerAll(nullRegistry));

      assertEquals("registry must not be null", exception.getMessage());
    }

    /** Verifies that registerAll throws IllegalStateException when context not set. */
    @Test
    @Tag("error")
    @DisplayName("should throw IllegalStateException when application context not set")
    void shouldThrowIllegalStateException_whenApplicationContextNotSet() {
      registrar = new DataSourceRegistrar(propertiesEnabled);

      final var exception =
          assertThrows(IllegalStateException.class, () -> registrar.registerAll(registry));

      final var message = exception.getMessage();
      assertAll(
          "exception details",
          () -> assertNotNull(message),
          () -> assertTrue(message != null && message.contains("ApplicationContext not set")));
    }

    /** Verifies that registerAll skips registration when auto-register is disabled. */
    @Test
    @Tag("normal")
    @DisplayName("should skip registration when auto-register is disabled")
    void shouldSkipRegistration_whenAutoRegisterDisabled() {
      registrar = new DataSourceRegistrar(propertiesDisabled);
      registrar.setApplicationContext(mockApplicationContext);

      registrar.registerAll(registry);

      assertAll(
          "skip registration verification",
          () -> assertFalse(registry.hasDefault()),
          () -> verifyNoInteractions(mockApplicationContext));
    }

    /** Verifies that registerAll skips registration when no DataSource beans found. */
    @Test
    @Tag("normal")
    @DisplayName("should skip registration when no DataSource beans found")
    void shouldSkipRegistration_whenNoDataSourceBeansFound() {
      registrar = new DataSourceRegistrar(propertiesEnabled);
      registrar.setApplicationContext(mockApplicationContext);
      when(mockApplicationContext.getBeansOfType(DataSource.class)).thenReturn(Map.of());

      registrar.registerAll(registry);

      assertFalse(registry.hasDefault());
    }

    /** Verifies that registerAll registers single DataSource as default. */
    @Test
    @Tag("normal")
    @DisplayName("should register single DataSource as default")
    void shouldRegisterSingleDataSource_asDefault() {
      registrar = new DataSourceRegistrar(propertiesEnabled);
      registrar.setApplicationContext(mockApplicationContext);
      when(mockApplicationContext.getBeansOfType(DataSource.class))
          .thenReturn(Map.of("dataSource", mockDataSource1));

      registrar.registerAll(registry);

      assertAll(
          "single DataSource registration",
          () -> assertTrue(registry.hasDefault()),
          () -> assertTrue(registry.has("dataSource")),
          () -> assertSame(mockDataSource1, registry.getDefault()),
          () -> assertSame(mockDataSource1, registry.get("dataSource")));
    }

    /** Verifies that registerAll registers multiple DataSources by name. */
    @Test
    @Tag("normal")
    @DisplayName("should register multiple DataSources by name")
    void shouldRegisterMultipleDataSources_byName() {
      registrar = new DataSourceRegistrar(propertiesEnabled);
      registrar.setApplicationContext(mockApplicationContext);
      when(mockApplicationContext.getBeansOfType(DataSource.class))
          .thenReturn(Map.of("mainDb", mockDataSource1, "archiveDb", mockDataSource2));
      when(mockApplicationContext.containsBeanDefinition(anyString())).thenReturn(false);

      registrar.registerAll(registry);

      assertAll(
          "multiple DataSources registration",
          () -> assertTrue(registry.has("mainDb")),
          () -> assertTrue(registry.has("archiveDb")),
          () -> assertSame(mockDataSource1, registry.get("mainDb")),
          () -> assertSame(mockDataSource2, registry.get("archiveDb")));
    }

    /** Verifies that registerAll registers primary DataSource as default when multiple exist. */
    @Test
    @Tag("normal")
    @DisplayName("should register primary DataSource as default when multiple exist")
    void shouldRegisterPrimaryDataSource_asDefault_whenMultipleExist() {
      registrar = new DataSourceRegistrar(propertiesEnabled);
      registrar.setApplicationContext(mockApplicationContext);
      when(mockApplicationContext.getBeansOfType(DataSource.class))
          .thenReturn(Map.of("mainDb", mockDataSource1, "archiveDb", mockDataSource2));
      when(mockApplicationContext.containsBeanDefinition("mainDb")).thenReturn(true);
      when(mockApplicationContext.containsBeanDefinition("archiveDb")).thenReturn(true);
      when(mockApplicationContext.getBeanFactory()).thenReturn(mockBeanFactory);
      when(mockBeanFactory.containsBeanDefinition("mainDb")).thenReturn(true);
      when(mockBeanFactory.containsBeanDefinition("archiveDb")).thenReturn(true);
      when(mockBeanFactory.getBeanDefinition("mainDb")).thenReturn(mockBeanDefinition1);
      when(mockBeanFactory.getBeanDefinition("archiveDb")).thenReturn(mockBeanDefinition2);
      when(mockBeanDefinition1.isPrimary()).thenReturn(true);
      when(mockBeanDefinition2.isPrimary()).thenReturn(false);

      registrar.registerAll(registry);

      assertAll(
          "primary DataSource as default",
          () -> assertTrue(registry.hasDefault()),
          () -> assertSame(mockDataSource1, registry.getDefault()));
    }

    /** Verifies that registerAll registers dataSource bean as default when no primary exists. */
    @Test
    @Tag("normal")
    @DisplayName("should register dataSource bean as default when no primary exists")
    void shouldRegisterDataSourceBean_asDefault_whenNoPrimaryExists() {
      registrar = new DataSourceRegistrar(propertiesEnabled);
      registrar.setApplicationContext(mockApplicationContext);
      when(mockApplicationContext.getBeansOfType(DataSource.class))
          .thenReturn(Map.of("dataSource", mockDataSource1, "archiveDb", mockDataSource2));
      when(mockApplicationContext.containsBeanDefinition(anyString())).thenReturn(false);

      registrar.registerAll(registry);

      assertAll(
          "dataSource bean as default",
          () -> assertTrue(registry.hasDefault()),
          () -> assertSame(mockDataSource1, registry.getDefault()));
    }
  }
}
