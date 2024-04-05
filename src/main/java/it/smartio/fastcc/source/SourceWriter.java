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

package it.smartio.fastcc.source;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import it.smartio.fastcc.FastCC;
import it.smartio.fastcc.parser.JavaCCErrors;
import it.smartio.fastcc.utils.DigestOptions;
import it.smartio.fastcc.utils.DigestWriter;
import it.smartio.fastcc.utils.Template;

/**
 * The {@link SourceWriter} class.
 */
public class SourceWriter extends PrintWriter {

  private final String        name;
  private final DigestOptions options;

  /**
   * Constructs an instance of {@link SourceWriter}.
   *
   * @param name
   * @param options
   */
  public SourceWriter(String name, DigestOptions options) {
    super(new StringWriter());
    this.name = name;
    this.options = options;
  }

  /**
   * Gets the name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Gets the {@link DigestOptions}.
   */
  public final DigestOptions getOptions() {
    return this.options;
  }

  /**
   * Write the content using a template.
   *
   * @param path
   */
  public final void writeTemplate(String path) throws IOException {
    Template template = Template.of(path, getOptions());
    template.render(new PrintWriter(this.out));
  }

  /**
   * Save {@link SourceWriter} to output path.
   *
   * @param path
   */
  public void saveOutput(File path) {
    File file = new File(path, getName() + ".java");
    try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, options)) {
      writer.print(toString());
    } catch (IOException e) {
      JavaCCErrors.fatal("Could not create output file: " + file);
    }
  }

  /**
   * Converts the {@link StringWriter} to a {@link StringBuffer}.
   */
  @Override
  public final String toString() {
    return ((StringWriter) this.out).getBuffer().toString();
  }
}
