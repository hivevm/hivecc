// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.semantic;

import org.hivevm.cc.parser.Choice;
import org.hivevm.cc.parser.Expansion;
import org.hivevm.cc.parser.Lookahead;
import org.hivevm.cc.parser.OneOrMore;
import org.hivevm.cc.parser.RChoice;
import org.hivevm.cc.parser.ROneOrMore;
import org.hivevm.cc.parser.RRepetitionRange;
import org.hivevm.cc.parser.RSequence;
import org.hivevm.cc.parser.RZeroOrMore;
import org.hivevm.cc.parser.RZeroOrOne;
import org.hivevm.cc.parser.Sequence;
import org.hivevm.cc.parser.ZeroOrMore;
import org.hivevm.cc.parser.ZeroOrOne;

/**
 * Objects of this type are passed to the tree walker routines in ExpansionTreeWalker.
 */
interface TreeWalker {

  /**
   * When called at a particular node, this specifies to the tree walker if it should visit more
   * nodes under this node.
   */
  boolean goDeeper(Expansion e);

  /**
   * When a node is visited, this method is invoked with the node as parameter.
   */
  void action(Expansion e);

  /**
   * Visits the nodes of the tree rooted at "node" in pre/post-order. i.e., it executes opObj.action
   * first and then visits the children.
   */
  static void walk(Expansion node, TreeWalker opObj, boolean post) {
    if (!post) {
      opObj.action(node);
    }

    if (opObj.goDeeper(node)) {
      if (node instanceof Choice) {
        for (Object object : ((Choice) node).getChoices()) {
          TreeWalker.walk((Expansion) object, opObj, post);
        }
      } else if (node instanceof Sequence) {
        for (Object object : ((Sequence) node).getUnits()) {
          TreeWalker.walk((Expansion) object, opObj, post);
        }
      } else if (node instanceof OneOrMore) {
        TreeWalker.walk(((OneOrMore) node).getExpansion(), opObj, post);
      } else if (node instanceof ZeroOrMore) {
        TreeWalker.walk(((ZeroOrMore) node).getExpansion(), opObj, post);
      } else if (node instanceof ZeroOrOne) {
        TreeWalker.walk(((ZeroOrOne) node).getExpansion(), opObj, post);
      } else if (node instanceof Lookahead) {
        Expansion nested_e = ((Lookahead) node).getLaExpansion();
        if (!((nested_e instanceof Sequence) && ((Expansion) (((Sequence) nested_e).getUnits().get(0)) == node))) {
          TreeWalker.walk(nested_e, opObj, post);
        }
      } else if (node instanceof RChoice) {
        for (Object object : ((RChoice) node).getChoices()) {
          TreeWalker.walk((Expansion) object, opObj, post);
        }
      } else if (node instanceof RSequence) {
        for (Object object : ((RSequence) node).getUnits()) {
          TreeWalker.walk((Expansion) object, opObj, post);
        }
      } else if (node instanceof ROneOrMore) {
        TreeWalker.walk(((ROneOrMore) node).getRegexpr(), opObj, post);
      } else if (node instanceof RZeroOrMore) {
        TreeWalker.walk(((RZeroOrMore) node).getRegexpr(), opObj, post);
      } else if (node instanceof RZeroOrOne) {
        TreeWalker.walk(((RZeroOrOne) node).getRegexpr(), opObj, post);
      } else if (node instanceof RRepetitionRange) {
        TreeWalker.walk(((RRepetitionRange) node).getRegexpr(), opObj, post);
      }
    }

    if (post) {
      opObj.action(node);
    }
  }
}
