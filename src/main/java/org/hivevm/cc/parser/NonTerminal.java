// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes non terminals.
 */

public class NonTerminal extends Expansion {

  /**
   * The LHS to which the return value of the non-terminal is assigned. In case there is no LHS,
   * then the vector remains empty.
   */
  private List<Token>      lhsTokens                 = new ArrayList<>();

  /**
   * The name of the non-terminal.
   */
  private String           name;

  /**
   * The list of all tokens in the argument list.
   */
  private List<Token>      argument_tokens           = new ArrayList<>();

  private List<Token>      parametrized_type__tokens = new ArrayList<>();
  /**
   * The production this non-terminal corresponds to.
   */
  private NormalProduction prod;

  /**
   * @param lhsTokens the lhsTokens to set
   */
  public void setLhsTokens(List<Token> lhsTokens) {
    this.lhsTokens = lhsTokens;
  }

  /**
   * @return the lhsTokens
   */
  public List<Token> getLhsTokens() {
    return this.lhsTokens;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return the argument_tokens
   */
  public List<Token> getParametrizedTypeTokens() {
    return this.parametrized_type__tokens;
  }

  /**
   * @return the argument_tokens
   */
  public List<Token> getArgumentTokens() {
    return this.argument_tokens;
  }

  /**
   * @return the prod
   */
  public NormalProduction getProd() {
    return this.prod;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param argument_tokens the argument_tokens to set
   */
  public void setParametrizedTypeTokens(List<Token> argument_tokens) {
    this.argument_tokens = argument_tokens;
  }

  /**
   * @param argument_tokens the argument_tokens to set
   */
  public void setArgumentTokens(List<Token> parametrized_type__tokens) {
    this.parametrized_type__tokens = parametrized_type__tokens;
  }

  /**
   * @param prod the prod to set
   */
  public NormalProduction setProd(NormalProduction prod) {
    return this.prod = prod;
  }
}
