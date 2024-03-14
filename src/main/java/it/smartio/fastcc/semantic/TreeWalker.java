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

package it.smartio.fastcc.semantic;

import it.smartio.fastcc.parser.Choice;
import it.smartio.fastcc.parser.Expansion;
import it.smartio.fastcc.parser.Lookahead;
import it.smartio.fastcc.parser.OneOrMore;
import it.smartio.fastcc.parser.RChoice;
import it.smartio.fastcc.parser.ROneOrMore;
import it.smartio.fastcc.parser.RRepetitionRange;
import it.smartio.fastcc.parser.RSequence;
import it.smartio.fastcc.parser.RZeroOrMore;
import it.smartio.fastcc.parser.RZeroOrOne;
import it.smartio.fastcc.parser.Sequence;
import it.smartio.fastcc.parser.TryBlock;
import it.smartio.fastcc.parser.ZeroOrMore;
import it.smartio.fastcc.parser.ZeroOrOne;

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
    if (!post)
      opObj.action(node);

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
      } else if (node instanceof TryBlock) {
        TreeWalker.walk(((TryBlock) node).getExpansion(), opObj, post);
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

    if (post)
      opObj.action(node);
  }
}
