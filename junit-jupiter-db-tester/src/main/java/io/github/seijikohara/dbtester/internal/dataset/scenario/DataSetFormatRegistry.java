package io.github.seijikohara.dbtester.internal.dataset.scenario;

import io.github.seijikohara.dbtester.exception.ConfigurationException;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

/**
 * Thread-safe registry for dataset format providers.
 *
 * <p>This registry automatically discovers and registers all {@link DataSetFormatProvider}
 * implementations using reflection. Providers can also be manually registered for extensibility.
 *
 * <h2>Auto-Registration</h2>
 *
 * <p>At class initialization, this registry automatically scans for all concrete classes that
 * implement {@link DataSetFormatProvider} and registers them by their supported file extension. The
 * scanning is performed by analyzing the {@link DataSetFormatProvider} interface itself and finding
 * all its subtypes.
 *
 * <h2>Manual Registration</h2>
 *
 * <p>Additional providers can be registered manually using {@link #register(DataSetFormatProvider)}
 * for extensibility. This allows custom format providers to be added without modifying the
 * framework.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This class is thread-safe. All operations on the registry are atomic and can be safely called
 * from multiple threads.
 *
 * <h2>Example Usage</h2>
 *
 * <pre>{@code
 * // Get provider for CSV files
 * DataSetFormatProvider provider = DataSetFormatRegistry.getProvider(".csv");
 *
 * // Register custom provider
 * DataSetFormatRegistry.register(new CustomFormatProvider());
 * }</pre>
 *
 * @see DataSetFormatProvider
 * @see io.github.seijikohara.dbtester.internal.dataset.scenario.csv.CsvFormatProvider
 */
public final class DataSetFormatRegistry {

  /** Thread-safe map of file extensions to format providers. */
  private static final ConcurrentMap<String, DataSetFormatProvider> PROVIDERS =
      new ConcurrentHashMap<>();

  static {
    // Auto-register all providers found by scanning DataSetFormatProvider subtypes
    autoRegisterProviders();
  }

  /** Private constructor to prevent instantiation. */
  private DataSetFormatRegistry() {}

  /**
   * Automatically discovers and registers all format providers.
   *
   * <p>This method scans for all concrete classes implementing {@link DataSetFormatProvider} by
   * analyzing the interface itself and finding all its subtypes, then registers them by their
   * supported extension.
   */
  private static void autoRegisterProviders() {
    final var reflections =
        new Reflections(DataSetFormatProvider.class.getPackageName(), Scanners.SubTypes);
    final var providerClasses = reflections.getSubTypesOf(DataSetFormatProvider.class);

    providerClasses.stream()
        // Skip interfaces
        .filter(clazz -> !clazz.isInterface())
        // Skip abstract classes
        .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
        // Instantiate and register each provider
        .forEach(
            providerClass -> {
              try {
                final var provider = providerClass.getDeclaredConstructor().newInstance();
                register(provider);
              } catch (final ReflectiveOperationException e) {
                throw new ConfigurationException(
                    String.format(
                        "Failed to instantiate format provider: %s. Ensure the class has a public no-argument constructor.",
                        providerClass.getName()),
                    e);
              }
            });
  }

  /**
   * Registers a format provider.
   *
   * <p>This method associates the provider's supported extension with the provider instance. If a
   * provider is already registered for the same extension, it will be replaced.
   *
   * @param provider the format provider to register
   * @throws NullPointerException if provider is null (enforced by NullAway)
   */
  public static void register(final DataSetFormatProvider provider) {
    final var extension = provider.supportedExtension();
    PROVIDERS.put(extension, provider);
  }

  /**
   * Returns the format provider for the specified file extension.
   *
   * <p>The extension should include the leading dot (e.g., ".csv", ".tsv").
   *
   * @param extension the file extension (with leading dot)
   * @return the format provider for the extension
   * @throws ConfigurationException if no provider is registered for the extension
   */
  public static DataSetFormatProvider getProvider(final String extension) {
    return Optional.ofNullable(PROVIDERS.get(extension))
        .orElseThrow(
            () ->
                new ConfigurationException(
                    String.format(
                        "No format provider registered for extension: %s. Registered extensions: %s",
                        extension, PROVIDERS.keySet())));
  }

  /**
   * Returns all registered file extensions.
   *
   * <p>This method is useful for discovering which file formats are supported.
   *
   * @return immutable set of registered file extensions (with leading dot)
   */
  public static Set<String> getSupportedExtensions() {
    return Set.copyOf(PROVIDERS.keySet());
  }
}
