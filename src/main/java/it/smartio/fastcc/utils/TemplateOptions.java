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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * The {@link TemplateOptions} class.
 */
public class TemplateOptions implements Environment {

  private final Map<String, Object> options = new HashMap<>();

  /**
   * Returns <code>true</code> if the environment variable is set.
   *
   * @param name
   */
  @Override
  public final boolean isSet(String name) {
    return options.containsKey(name);
  }

  /**
   * Returns the value to which the specified key is mapped, or {@code null} if this map contains no
   * mapping for the key.
   *
   * @param name
   */
  @Override
  public final Object get(String name) {
    return options.get(name);
  }

  /**
   * Sets a key/value option.
   *
   * @param name
   * @param value
   */
  @Override
  public final void set(String name, Object value) {
    options.put(name, value);
  }

  public final void set(String name, Supplier<Object> value) {
    options.put(name, value);
  }

  public final void setWriter(String name, Consumer<PrintWriter> value) {
    set(name, () -> {
      StringWriter builder = new StringWriter();
      try (PrintWriter writer = new PrintWriter(builder)) {
        value.accept(writer);
      }
      return builder.toString();
    });
  }

  public final void set(String name, Function<?, Object> value) {
    options.put(name, value);
  }

  public final <T> Mapper<T> add(String name, T value) {
    options.put(name, value);
    return new Mapper<>(name);
  }

  public final <T> Mapper<T> add(String name, Iterable<T> value) {
    options.put(name, value);
    return new Mapper<>(name);
  }

  public class Mapper<T> {

    private final String name;

    /**
     * Constructs an instance of {@link Mapper}.
     *
     * @param name
     */
    private Mapper(String name) {
      this.name = name;
    }

    public final Mapper<T> set(String key, Function<T, Object> function) {
      options.put(String.join(".", name, key), function);
      return this;
    }

    public final Mapper<T> set(String key, BiConsumer<T, PrintWriter> consumer) {
      set(key, i -> {
        StringWriter builder = new StringWriter();
        try (PrintWriter writer = new PrintWriter(builder)) {
          consumer.accept(i, writer);
        }
        return builder.toString();
      });
      return this;
    }
  }
}
