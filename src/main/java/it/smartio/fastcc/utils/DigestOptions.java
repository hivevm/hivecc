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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import it.smartio.fastcc.parser.Options;


/**
 * The {@link DigestOptions} class.
 */
public class DigestOptions implements Environment {

  private final Options     global;
  private final Environment environment;
  private final Set<String> consumed = new HashSet<>();

  /**
   * Constructs an instance of {@link DigestOptions}.
   * 
   * @param global
   * @param environment
   */
  public DigestOptions(Options global) {
    this(global, new TemplateOptions());
  }

  /**
   * Constructs an instance of {@link DigestOptions}.
   * 
   * @param global
   * @param environment
   */
  public DigestOptions(Options global, Environment environment) {
    this.global = global;
    this.environment = environment;
  }

  /**
   * Gets the {@link Options}.
   */
  public final Options getOptions() {
    return global;
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
    return environment.isSet(name) || global.getOptions().containsKey(name);
  }

  /**
   * Returns the value to which the specified key is mapped, or {@code null} if this map contains no
   * mapping for the key.
   *
   * @param name
   */
  @Override
  public final Object get(String name) {
    Object value = environment.isSet(name) ? environment.get(name) : global.getOptions().get(name);
    this.consumed.add(name);
    return value;
  }

  /**
   * Sets a key/value option.
   *
   * @param name
   * @param value
   */
  @Override
  public final void set(String name, Object value) {
    environment.set(name, value);
  }

  private static boolean isPrintableOption(Object value) {
    return (value instanceof String) || (value instanceof Number) || (value instanceof Boolean);
  }

  private static String toPrintable(String name, Object value) {
    if ((value instanceof Number) || (value instanceof Boolean))
      return String.format("%s=%s", name, value);
    return String.format("%s='%s'", name, value);
  }
}
