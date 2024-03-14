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
 * Describes regular expressions.
 */

public abstract class RegularExpression extends Expansion {

  /**
   * The label of the regular expression (if any). If no label is present, this is set to "".
   */
  protected String        label        = "";

  /**
   * The ordinal value assigned to the regular expression. It is used for internal processing and
   * passing information between the parser and the lexical analyzer.
   */
  protected int           ordinal;

  /**
   * The LHS to which the token value of the regular expression is assigned. In case there is no
   * LHS, then the list remains empty.
   */
  private List<Token>     lhsTokens    = new ArrayList<>();

  /**
   * We now allow qualified access to token members. Store it here.
   */
  private Token           rhsToken;

  /**
   * This flag is set if the regular expression has a label prefixed with the # symbol - this
   * indicates that the purpose of the regular expression is solely for defining other regular
   * expressions.
   */
  private boolean         private_rexp = false;

  /**
   * If this is a top-level regular expression (nested directly within a TokenProduction), then this
   * field point to that TokenProduction object.
   */
  private TokenProduction tpContext    = null;


  /**
   * Gets the {@link #label}.
   */
  public final String getLabel() {
    return label;
  }

  public boolean CanMatchAnyChar() {
    return false;
  }

  /**
   * The following variable is used to maintain state information for the loop determination
   * algorithm: It is initialized to 0, and set to -1 if this node has been visited in a pre-order
   * walk, and then it is set to 1 if the pre-order walk of the whole graph from this node has been
   * traversed. i.e., -1 indicates partially processed, and 1 indicates fully processed.
   */
  private int walkStatus = 0;

  public abstract <R, D> R accept(RegularExpressionVisitor<R, D> visitor, D data);


  /**
   * Gets the {@link #rhsToken}.
   */
  public final Token getRhsToken() {
    return rhsToken;
  }

  /**
   * Gets the {@link #rhsToken}.
   */
  public final void setRhsToken(Token token) {
    this.rhsToken = token;
  }

  /**
   * Gets the {@link #lhsTokens}.
   */
  public final List<Token> getLhsTokens() {
    return lhsTokens;
  }


  /**
   * Sets the {@link #lhsTokens}.
   */
  public final void setLhsTokens(List<Token> lhsTokens) {
    this.lhsTokens = lhsTokens;
  }


  /**
   * Gets the {@link #private_rexp}.
   */
  public final boolean isPrivateExp() {
    return private_rexp;
  }


  /**
   * Sets the {@link #private_rexp}.
   */
  public final void setPrivateRegExp(boolean private_rexp) {
    this.private_rexp = private_rexp;
  }


  /**
   * Gets the {@link #ordinal}.
   */
  public final int getOrdinal() {
    return ordinal;
  }


  /**
   * Sets the {@link #ordinal}.
   */
  public final void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }


  /**
   * Gets the {@link #walkStatus}.
   */
  public final int getWalkStatus() {
    return walkStatus;
  }


  /**
   * Sets the {@link #walkStatus}.
   */
  public final void setWalkStatus(int walkStatus) {
    this.walkStatus = walkStatus;
  }

  /**
   * Gets the {@link #tpContext}.
   */
  public final TokenProduction getTpContext() {
    return tpContext;
  }

  /**
   * Sets the {@link #tpContext}.
   */
  public final void setTpContext(TokenProduction tpContext) {
    this.tpContext = tpContext;
  }
}
