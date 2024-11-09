// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes regular expressions which are choices from from among included regular expressions.
 */

public class RChoice extends RegularExpression {

  /**
   * The list of choices of this regular expression. Each list component will narrow to
   * RegularExpression.
   */
  private List<RegularExpression> choices = new ArrayList<>();

  /**
   * @param choices the choices to set
   */
  public void setChoices(List<RegularExpression> choices) {
    this.choices = choices;
  }

  /**
   * @return the choices
   */
  public List<RegularExpression> getChoices() {
    return this.choices;
  }

  public final void CompressCharLists() {
    CompressChoices(); // Unroll nested choices
    RegularExpression curRE;
    RCharacterList curCharList = null;

    for (int i = 0; i < getChoices().size(); i++) {
      curRE = getChoices().get(i);

      while (curRE instanceof RJustName) {
        curRE = ((RJustName) curRE).getRegexpr();
      }

      if ((curRE instanceof RStringLiteral) && (((RStringLiteral) curRE).getImage().length() == 1)) {
        getChoices().set(i, curRE = new RCharacterList(((RStringLiteral) curRE).getImage().charAt(0)));
      }

      if (curRE instanceof RCharacterList) {
        if (((RCharacterList) curRE).isNegated_list()) {
          ((RCharacterList) curRE).RemoveNegation();
        }

        List<Object> tmp = ((RCharacterList) curRE).getDescriptors();

        if (curCharList == null) {
          getChoices().set(i, curRE = curCharList = new RCharacterList());
        } else {
          getChoices().remove(i--);
        }

        for (int j = tmp.size(); j-- > 0;) {
          curCharList.getDescriptors().add(tmp.get(j));
        }
      }

    }
  }

  private void CompressChoices() {
    RegularExpression curRE;

    for (int i = 0; i < getChoices().size(); i++) {
      curRE = getChoices().get(i);

      while (curRE instanceof RJustName) {
        curRE = ((RJustName) curRE).getRegexpr();
      }

      if (curRE instanceof RChoice) {
        getChoices().remove(i--);
        for (int j = ((RChoice) curRE).getChoices().size(); j-- > 0;) {
          getChoices().add(((RChoice) curRE).getChoices().get(j));
        }
      }
    }
  }

  @Override
  public final <R, D> R accept(RegularExpressionVisitor<R, D> visitor, D data) {
    return visitor.visit(this, data);
  }
}
