/**
 * Internal dataset loading implementation.
 *
 * <p>This package contains the internal implementation of dataset loading strategies, including
 * annotation resolution, directory resolution, and dataset factory. All classes in this package are
 * internal and should not be used directly by library users.
 *
 * <h2>Key Components</h2>
 *
 * <ul>
 *   <li>{@link io.github.seijikohara.dbtester.internal.loader.DataSetLoader} - Core interface for
 *       dataset loaders
 *   <li>{@link io.github.seijikohara.dbtester.internal.loader.TestClassNameBasedDataSetLoader} -
 *       Convention-based loader implementation
 *   <li>{@link io.github.seijikohara.dbtester.internal.loader.AnnotationResolver} - Annotation
 *       value resolution
 *   <li>{@link io.github.seijikohara.dbtester.internal.loader.DirectoryResolver} - Directory
 *       location resolution
 *   <li>{@link io.github.seijikohara.dbtester.internal.loader.DataSetFactory} - Dataset instance
 *       creation
 * </ul>
 *
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit
 * @see io.github.seijikohara.dbtester.internal.bridge.dbunit.format.CsvDataSetReader
 */
package io.github.seijikohara.dbtester.internal.loader;
