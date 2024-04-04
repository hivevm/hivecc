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

package it.smartio.fastcc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the various regular expression productions.
 */

public class TokenProduction {

  /**
   * Definitions of constants that identify the kind of regular expression production this is.
   */
  public enum Kind {
    TOKEN,
    SKIP,
    MORE,
    SPECIAL
  }

  /**
   * The starting line and column of this token production.
   */
  private int                     column;
  private int                     line;

  /**
   * The states in which this regular expression production exists. If this array is null, then
   * "<*>" has been specified and this regular expression exists in all states. However, this null
   * value is replaced by a String array that includes all lexical state names during the
   * semanticization phase.
   */
  private String[]                lexStates;

  /**
   * The kind of this token production - TOKEN, SKIP, MORE, or SPECIAL.
   */
  private Kind                    kind;

  /**
   * The list of regular expression specifications that comprise this production. Each entry is a
   * "RegExprSpec".
   */
  private final List<RegExprSpec> respecs    = new ArrayList<>();

  /**
   * This is true if this corresponds to a production that actually appears in the input grammar.
   * Otherwise (if this is created to describe a regular expression that is part of the BNF) this is
   * set to false.
   */
  private boolean                 isExplicit = true;

  /**
   * This is true if case is to be ignored within the regular expressions of this token production.
   */
  private boolean                 ignoreCase = false;

  /**
   * The first and last tokens from the input stream that represent this production.
   */
  private Token                   firstToken;

  /**
   * @return the line
   */
  public int getLine() {
    return this.line;
  }

  /**
   * @param column the column to set
   */
  public void setLocation(Token token) {
    this.line = token.beginLine;
    this.column = token.beginColumn;
  }

  /**
   * @return the column
   */
  public int getColumn() {
    return this.column;
  }


  /**
   * Gets the {@link #kind}.
   */
  public final Kind getKind() {
    return this.kind;
  }


  /**
   * Sets the {@link #kind}.
   */
  public final void setKind(Kind kind) {
    this.kind = kind;
  }


  /**
   * Gets the {@link #respecs}.
   */
  public final List<RegExprSpec> getRespecs() {
    return this.respecs;
  }

  /**
   * Gets the {@link #isExplicit}.
   */
  public final boolean isExplicit() {
    return this.isExplicit;
  }


  /**
   * Sets the {@link #isExplicit}.
   */
  public final void setExplicit(boolean isExplicit) {
    this.isExplicit = isExplicit;
  }


  /**
   * Gets the {@link #ignoreCase}.
   */
  public final boolean isIgnoreCase() {
    return this.ignoreCase;
  }


  /**
   * Sets the {@link #ignoreCase}.
   */
  public final void setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }


  /**
   * Gets the {@link #firstToken}.
   */
  public final Token getFirstToken() {
    return this.firstToken;
  }


  /**
   * Sets the {@link #firstToken}.
   */
  public final void setFirstToken(Token firstToken) {
    this.firstToken = firstToken;
  }


  /**
   * Gets the {@link #lexStates}.
   */
  public final String[] getLexStates() {
    return this.lexStates;
  }


  /**
   * Gets the {@link #lexStates}.
   */
  public final void setLexStates(String[] lexstates) {
    this.lexStates = lexstates;
  }


  /**
   * Gets the {@link #lexStates}.
   */
  public final void setLexState(String lexstate, int index) {
    this.lexStates[index] = lexstate;
  }
}
