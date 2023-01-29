/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package it.smartio.fastcc.jjtree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.smartio.fastcc.FastCC;

public class JJTreeGlobals {

  public static void initialize() {
    JJTreeGlobals.parserName = null;
    JJTreeGlobals.productions = new HashMap<>();

    JJTreeGlobals.jjtreeOptions = new HashSet<>();
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_MULTI);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_NODE_PREFIX);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_NODE_PACKAGE);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_NODE_EXTENDS);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_NODE_CLASS);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_NODE_DEFAULT_VOID);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_OUTPUT_FILE);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_NODE_SCOPE_HOOK);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_TRACK_TOKENS);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_NODE_FACTORY);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_BUILD_NODE_FILES);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_VISITOR);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_VISITOR_EXCEPTION);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_VISITOR_DATA_TYPE);
    JJTreeGlobals.jjtreeOptions.add(FastCC.JJTREE_VISITOR_RETURN_TYPE);
  }

  static {
    JJTreeGlobals.initialize();
  }

  /**
   * This set stores the JJTree-specific options that should not be passed down to JavaCC
   */
  private static Set<String> jjtreeOptions;

  static boolean isOptionJJTreeOnly(String optionName) {
    return JJTreeGlobals.jjtreeOptions.contains(optionName.toUpperCase());
  }

  /**
   * Use this like className.
   **/
  public static String                     parserName;

  /**
   * This is mapping from production names to ASTProduction objects.
   **/
  public static Map<String, ASTProduction> productions     = new HashMap<>();

}
