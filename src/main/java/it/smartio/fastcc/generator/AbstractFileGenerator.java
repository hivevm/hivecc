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

package it.smartio.fastcc.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import it.smartio.fastcc.parser.JavaCCErrors;
import it.smartio.fastcc.utils.DigestOptions;
import it.smartio.fastcc.utils.DigestWriter;
import it.smartio.fastcc.utils.Template;

/**
 * The {@link AbstractFileGenerator} class.
 */
public abstract class AbstractFileGenerator {

  /**
   * Gets the template by name.
   * 
   * @param name
   */
  protected abstract String getTemplate(String name);

  /**
   * Creates a new {@link DigestWriter}.
   * 
   * @param file
   * @param options
   */
  protected abstract DigestWriter createDigestWriter(File file, DigestOptions options) throws FileNotFoundException;

  /**
   * Generates a {@link File} from a template.
   *
   * @param filename
   * @param context
   */
  protected final void generateFile(String filename, LexerData context) {
    generateFile(filename, filename, new DigestOptions(context.options()));
  }

  /**
   * Generates a {@link File} from a template.
   *
   * @param name
   * @param filename
   * @param options
   */
  protected final void generateFile(String name, String filename, DigestOptions options) {
    File file = new File(options.getOptions().getOutputDirectory(), filename);

    try (DigestWriter writer = createDigestWriter(file, options)) {
      Template template = Template.of(getTemplate(name), writer.options());
      template.write(writer);
    } catch (IOException e) {
      System.err.println("Failed to create file: " + filename + " " + e);
      JavaCCErrors.semantic_error("Could not open file: " + filename + " for writing.");
      throw new Error();
    }
  }
}
