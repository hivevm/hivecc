// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

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
    return this.label;
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
    return this.rhsToken;
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
    return this.lhsTokens;
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
    return this.private_rexp;
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
    return this.ordinal;
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
    return this.walkStatus;
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
    return this.tpContext;
  }

  /**
   * Sets the {@link #tpContext}.
   */
  public final void setTpContext(TokenProduction tpContext) {
    this.tpContext = tpContext;
  }
}
