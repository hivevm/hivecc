/*
 * Copyright (c) 2001-2021 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package it.smartio.fastcc.utils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.smartio.fastcc.parser.Options;


/**
 * The {@link DigestOptions} class.
 */
public class DigestOptions implements Environment {

  private final Options delegate;


  private final Map<String, Object> options  = new HashMap<>();
  private final Set<String>         consumed = new HashSet<>();

  /**
   * Constructs an instance of {@link DigestOptions}.
   * 
   * @param delegate
   */
  public DigestOptions(Options delegate) {
    this.delegate = delegate;
  }

  /**
   * Gets the {@link Options}.
   */
  public final Options getOptions() {
    return delegate;
  }

  final boolean hasConsumed() {
    return !this.consumed.isEmpty();
  }

  final Stream<String> consumed() {
    return this.consumed.stream().filter(n -> DigestOptions.isPrintableOption(get(n))).sorted()
        .map(n -> toPrintable(n, get(n)));
  }

  /**
   * Returns <code>true</code> if the environment variable is set.
   *
   * @param name
   */
  @Override
  public final boolean isSet(String name) {
    return options.containsKey(name) || delegate.getOptions().containsKey(name);
  }

  /**
   * Returns the value to which the specified key is mapped, or {@code null} if this map contains no
   * mapping for the key.
   *
   * @param name
   */
  @Override
  public final Object get(String name) {
    Object value = options.containsKey(name) ? options.get(name) : delegate.getOptions().get(name);
    this.consumed.add(name);
    return value;
  }

  /**
   * Sets a key/value option.
   *
   * @param key
   * @param value
   */
  @Override
  public final void set(String key, Object value) {
    options.put(key, value);
  }

  /**
   * Set an {@link BiConsumer} writer.
   *
   * @param name
   * @param function
   */
  public final TemplateFunction<Integer> addValues(String key, Integer value) {
    return addValues(key, IntStream.range(0, value).boxed().collect(Collectors.toList()));
  }

  /**
   * Set an {@link BiConsumer} writer.
   *
   * @param name
   * @param function
   */
  public final <T> TemplateFunction<T> addValues(String key, Iterable<T> value) {
    TemplateFunction<T> functions = new TemplateFunction<>(value, new ArrayList<>());
    options.put(key, functions);
    return functions;
  }

  /**
   * Set an {@link BiConsumer} writer.
   *
   * @param name
   * @param function
   */
  public final <T> void setFunc(String key, Function<T, String> function) {
    options.put(key, function);
  }

  /**
   * Set an {@link BiConsumer} writer.
   *
   * @param name
   * @param writer
   */
  public final void set(String key, BiConsumer<PrintWriter, Object> writer) {
    options.put(key, writer);
  }


  private static boolean isPrintableOption(Object value) {
    return (value instanceof String) || (value instanceof Number) || (value instanceof Boolean)
        || (value instanceof TemplateFunction);
  }

  private static String toPrintable(String name, Object value) {
    if ((value instanceof Number) || (value instanceof Boolean))
      return String.format("%s=%s", name, value);
    if (value instanceof TemplateFunction) {
      return name;
    }
    return String.format("%s='%s'", name, value);
  }
}
