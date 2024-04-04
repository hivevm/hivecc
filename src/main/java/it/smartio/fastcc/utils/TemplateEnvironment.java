/*
 * Copyright (c) 2001-2022 Territorium Online Srl / TOL GmbH. All Rights Reserved.
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

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link TemplateEnvironment} class.
 */
class TemplateEnvironment implements Environment {

  private final Environment         environment;
  private final Map<String, Object> options = new HashMap<>();

  /**
   * Constructs an instance of {@link TemplateEnvironment}.
   *
   * @param environment
   */
  public TemplateEnvironment(Environment environment) {
    this.environment = environment;
  }

  /**
   * Return <code>true</code> if the environment variable is set.
   * 
   * @param name
   */
  @Override
  public final boolean isSet(String name) {
    return options.containsKey(name);
    // return options.containsKey(name) || environment.isSet(name);
  }

  /**
   * Get the environment variable by name
   * 
   * @param name
   */
  @Override
  public final Object get(String name) {
    return options.get(name);
    // return options.containsKey(name) ? options.get(name) : environment.get(name);
  }

  /**
   * Set an environment variable by name
   * 
   * @param name
   * @param value
   */
  @Override
  public void set(String name, Object value) {
    options.put(name, value);
  }
}
