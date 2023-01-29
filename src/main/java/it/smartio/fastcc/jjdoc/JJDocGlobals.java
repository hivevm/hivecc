/*
 * Copyright (c) 2006, Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * Neither the name of the Sun Microsystems, Inc. nor
 * the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package it.smartio.fastcc.jjdoc;

/**
 * Global variables for JJDoc.
 *
 */
class JJDocGlobals {

  /**
   * The name of the input file.
   */
  static String    input_file;
  /**
   * The name of the output file.
   */
  static String    output_file;

  /**
   * The Generator to create output with.
   */
  static Generator generator;

  /**
   * The commandline option is either TEXT or not, but the generator might have been set to some
   * other Generator using the setGenerator method.
   *
   * @return the generator configured in options or set by setter.
   */
  static Generator getGenerator(JJDocOptions opts) {
    if (JJDocGlobals.generator == null) {
      if (opts.getText()) {
        JJDocGlobals.generator = new TextGenerator(opts);
      } else if (opts.getBNF()) {
        JJDocGlobals.generator = new BNFGenerator(opts);
      } else if (opts.getXText()) {
        JJDocGlobals.generator = new XTextGenerator(opts);
      } else {
        JJDocGlobals.generator = new HTMLGenerator(opts);
      }
    } else if (opts.getText()) {
      if (JJDocGlobals.generator instanceof HTMLGenerator) {
        JJDocGlobals.generator = new TextGenerator(opts);
      }
    } else if (opts.getBNF()) {
      JJDocGlobals.generator = new BNFGenerator(opts);
    } else if (opts.getXText()) {
      JJDocGlobals.generator = new XTextGenerator(opts);
    } else if (JJDocGlobals.generator instanceof TextGenerator) {
      JJDocGlobals.generator = new HTMLGenerator(opts);
    }
    return JJDocGlobals.generator;
  }

  /**
   * Log informational messages.
   *
   * @param message the message to log
   */
  static void info(String message) {
    System.out.println(message);
  }

  /**
   * Log error messages.
   *
   * @param message the message to log
   */
  static void error(String message) {
    System.err.println(message);
  }
}
