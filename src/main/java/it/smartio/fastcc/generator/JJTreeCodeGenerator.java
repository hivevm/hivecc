// Copyright 2011 Google Inc. All Rights Reserved.
// Author: sreeni@google.com (Sreeni Viswanadha)

package it.smartio.fastcc.generator;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import it.smartio.fastcc.jjtree.ASTBNFAction;
import it.smartio.fastcc.jjtree.ASTBNFDeclaration;
import it.smartio.fastcc.jjtree.ASTBNFNodeScope;
import it.smartio.fastcc.jjtree.ASTBNFNonTerminal;
import it.smartio.fastcc.jjtree.ASTBNFOneOrMore;
import it.smartio.fastcc.jjtree.ASTBNFSequence;
import it.smartio.fastcc.jjtree.ASTBNFTryBlock;
import it.smartio.fastcc.jjtree.ASTBNFZeroOrMore;
import it.smartio.fastcc.jjtree.ASTBNFZeroOrOne;
import it.smartio.fastcc.jjtree.ASTCompilationUnit;
import it.smartio.fastcc.jjtree.ASTExpansionNodeScope;
import it.smartio.fastcc.jjtree.ASTGrammar;
import it.smartio.fastcc.jjtree.ASTNodeDescriptor;
import it.smartio.fastcc.jjtree.ASTProduction;
import it.smartio.fastcc.jjtree.JJTreeGlobals;
import it.smartio.fastcc.jjtree.JJTreeNode;
import it.smartio.fastcc.jjtree.JJTreeOptions;
import it.smartio.fastcc.jjtree.JJTreeParserDefaultVisitor;
import it.smartio.fastcc.jjtree.Node;
import it.smartio.fastcc.jjtree.NodeScope;
import it.smartio.fastcc.jjtree.Token;
import it.smartio.fastcc.utils.Encoding;

public abstract class JJTreeCodeGenerator extends JJTreeParserDefaultVisitor {

  protected String getPointer() {
    return ".";
  }

  protected String getBoolean() {
    return "boolean";
  }

  protected String getTryFinally() {
    return "";
  }

  @Override
  public final Object defaultVisit(Node node, Object data) {
    handleJJTreeNode((JJTreeNode) node, (PrintWriter) data);
    return null;
  }

  @Override
  public final Object visit(ASTGrammar node, Object data) {
    return node.childrenAccept(this, data);
  }

  @Override
  public final Object visit(ASTBNFAction node, Object data) {
    PrintWriter io = (PrintWriter) data;
    /*
     * Assume that this action requires an early node close, and then try to decide whether this
     * assumption is false. Do this by looking outwards through the enclosing expansion units. If we
     * ever find that we are enclosed in a unit which is not the final unit in a sequence we know
     * that an early close is not required.
     */

    NodeScope ns = NodeScope.getEnclosingNodeScope(node);
    if ((ns != null) && !ns.isVoid()) {
      boolean needClose = true;
      Node sp = node.getScopingParent(ns);

      JJTreeNode n = node;
      while (true) {
        Node p = n.jjtGetParent();
        if ((p instanceof ASTBNFSequence) || (p instanceof ASTBNFTryBlock)) {
          if (n.getOrdinal() != (p.jjtGetNumChildren() - 1)) {
            /* We're not the final unit in the sequence. */
            needClose = false;
            break;
          }
        } else if ((p instanceof ASTBNFZeroOrOne) || (p instanceof ASTBNFZeroOrMore)
            || (p instanceof ASTBNFOneOrMore)) {
          needClose = false;
          break;
        }
        if (p == sp) {
          /* No more parents to look at. */
          break;
        }
        n = (JJTreeNode) p;
      }
      if (needClose) {
        JJTreeCodeGenerator.openJJTreeComment(io, null);
        insertCloseNodeAction(ns, io, getIndentation(node), node.jjtOptions());
        JJTreeCodeGenerator.closeJJTreeComment(io);
      }
    }

    return handleJJTreeNode(node, io);
  }

  @Override
  public Object visit(ASTCompilationUnit node, Object data) {
    PrintWriter io = (PrintWriter) data;
    Token t = node.getFirstToken();

    while (true) {
      print(t, io, node);
      if (t == node.getLastToken()) {
        return null;
      }
      t = t.next;
    }
  }

  @Override
  public final Object visit(ASTBNFDeclaration node, Object data) {
    PrintWriter io = (PrintWriter) data;
    if (!node.node_scope.isVoid()) {
      String indent = "";
      if (node.getLastToken().next == node.getFirstToken()) {
        indent = "  ";
      } else {
        for (int i = 1; i < node.getFirstToken().beginColumn; ++i) {
          indent += " ";
        }
      }

      JJTreeCodeGenerator.openJJTreeComment(io, node.node_scope.getNodeDescriptorText());
      insertOpenNodeCode(node.node_scope, io, indent, node.jjtOptions());
      JJTreeCodeGenerator.closeJJTreeComment(io);
    }

    return handleJJTreeNode(node, io);
  }

  @Override
  public final Object visit(ASTBNFNodeScope node, Object data) {
    PrintWriter io = (PrintWriter) data;
    if (node.node_scope.isVoid()) {
      return handleJJTreeNode(node, io);
    }

    String indent = getIndentation(node.expansion_unit);

    JJTreeCodeGenerator.openJJTreeComment(io, node.node_scope.getNodeDescriptor().getDescriptor());
    tryExpansionUnit(node.node_scope, io, indent, node.expansion_unit);
    return null;
  }

  @Override
  public final Object visit(ASTExpansionNodeScope node, Object data) {
    PrintWriter io = (PrintWriter) data;
    String indent = getIndentation(node.expansion_unit);
    JJTreeCodeGenerator.openJJTreeComment(io, node.node_scope.getNodeDescriptor().getDescriptor());
    insertOpenNodeAction(node.node_scope, io, indent, node.jjtOptions());
    tryExpansionUnit(node.node_scope, io, indent, node.expansion_unit);

    // Print the "whiteOut" equivalent of the Node descriptor to preserve
    // line numbers in the generated file.
    ((ASTNodeDescriptor) node.jjtGetChild(1)).jjtAccept(this, io);
    return null;
  }

  /*
   * This method prints the tokens corresponding to this node recursively calling the print methods
   * of its children. Overriding this print method in appropriate nodes gives the output the added
   * stuff not in the input.
   */

  private Object handleJJTreeNode(JJTreeNode node, PrintWriter io) {
    if (node.getLastToken().next == node.getFirstToken()) {
      return null;
    }

    Token t1 = node.getFirstToken();
    Token t = new Token();
    t.next = t1;
    JJTreeNode n;
    for (int ord = 0; ord < node.jjtGetNumChildren(); ord++) {
      n = (JJTreeNode) node.jjtGetChild(ord);
      while (true) {
        t = t.next;
        if (t == n.getFirstToken()) {
          break;
        }
        print(t, io, node);
      }
      n.jjtAccept(this, io);
      t = n.getLastToken();
    }
    while (t != node.getLastToken()) {
      t = t.next;
      print(t, io, node);
    }

    return null;
  }

  private static void openJJTreeComment(PrintWriter io, String arg) {
    io.print("\n/*@begin(jjtree)");
    if (arg != null) {
      io.print(" " + arg + " ");
    }
    io.println("*/");
  }

  private static void closeJJTreeComment(PrintWriter io) {
    io.print("/*@end*/");
  }

  private final String getIndentation(JJTreeNode n) {
    String s = "";
    for (int i = 1; i < n.getFirstToken().beginColumn; ++i) {
      s += " ";
    }
    return s;
  }

  /*
   * Indicates whether the token should be replaced by white space or replaced with the actual node
   * variable.
   */
  private boolean whitingOut = false;

  private void print(Token t, PrintWriter io, JJTreeNode node) {
    Token tt = t.specialToken;
    if (tt != null) {
      while (tt.specialToken != null) {
        tt = tt.specialToken;
      }
      while (tt != null) {
        io.print(Encoding.escapeUnicode(node.translateImage(tt)));
        tt = tt.next;
      }
    }

    /*
     * If we're within a node scope we modify the source in the following ways:
     *
     * 1) we rename all references to `jjtThis' to be references to the actual node variable.
     *
     * 2) we replace all calls to `jjtree.currentNode()' with references to the node variable.
     */

    NodeScope s = NodeScope.getEnclosingNodeScope(node);
    if (s == null) {
      /*
       * Not within a node scope so we don't need to modify the source.
       */
      io.print(Encoding.escapeUnicode(node.translateImage(t)));
      return;
    }

    if (t.image.equals("jjtThis")) {
      io.print(s.getNodeVariable());
      return;
    } else if (t.image.equals("jjtree")) {
      if (t.next.image.equals(".")) {
        if (t.next.next.image.equals("currentNode")) {
          if (t.next.next.next.image.equals("(")) {
            if (t.next.next.next.next.image.equals(")")) {
              /*
               * Found `jjtree.currentNode()' so go into white out mode. We'll stay in this mode
               * until we find the closing parenthesis.
               */
              this.whitingOut = true;
            }
          }
        }
      }
    }
    if (this.whitingOut) {
      if (t.image.equals("jjtree")) {
        io.print(s.getNodeVariable());
        io.print(" ");
      } else if (t.image.equals(")")) {
        io.print(" ");
        this.whitingOut = false;
      } else {
        for (int i = 0; i < t.image.length(); ++i) {
          io.print(" ");
        }
      }
      return;
    }

    io.print(Encoding.escapeUnicode(node.translateImage(t)));
  }


  private final void insertOpenNodeAction(NodeScope ns, PrintWriter io, String indent, JJTreeOptions options) {
    io.println(indent + "{");
    insertOpenNodeCode(ns, io, indent + "  ", options);
    io.println(indent + "}");
  }


  private final void insertCloseNodeAction(NodeScope ns, PrintWriter io, String indent, JJTreeOptions options) {
    io.println(indent + "{");
    insertCloseNodeCode(ns, io, indent + "  ", false, options);
    io.println(indent + "}");
  }

  protected abstract void insertOpenNodeCode(NodeScope ns, PrintWriter io, String indent, JJTreeOptions options);

  private final void insertCloseNodeCode(NodeScope ns, PrintWriter io, String indent, boolean isFinal,
      JJTreeOptions options) {
    String closeNode = ns.node_descriptor.closeNode(ns.nodeVar);
    io.println(indent + closeNode);
    if (ns.usesCloseNodeVar() && !isFinal) {
      io.println(indent + ns.closedVar + " = false;");
    }
    if (options.getNodeScopeHook()) {
      io.println(indent + "if (jjtree.nodeCreated()) {");
      io.println(indent + " jjtreeCloseNodeScope(" + ns.nodeVar + ");");
      io.println(indent + "}");
    }

    if (options.getTrackTokens()) {
      io.println(indent + ns.nodeVar + getPointer() + "jjtSetLastToken(getToken(0));");
    }
  }

  protected abstract void insertCatchBlocks(NodeScope ns, PrintWriter io, Enumeration<String> thrown_names,
      String indent);


  private static void findThrown(NodeScope ns, Hashtable<String, String> thrown_set, JJTreeNode expansion_unit) {
    if (expansion_unit instanceof ASTBNFNonTerminal) {
      /*
       * Should really make the nonterminal explicitly maintain its name.
       */
      String nt = expansion_unit.getFirstToken().image;
      ASTProduction prod = JJTreeGlobals.productions.get(nt);
      if (prod != null) {
        Enumeration<String> e = prod.throws_list.elements();
        while (e.hasMoreElements()) {
          String t = e.nextElement();
          thrown_set.put(t, t);
        }
      }
    }
    for (int i = 0; i < expansion_unit.jjtGetNumChildren(); ++i) {
      JJTreeNode n = (JJTreeNode) expansion_unit.jjtGetChild(i);
      JJTreeCodeGenerator.findThrown(ns, thrown_set, n);
    }
  }

  private void tryExpansionUnit(NodeScope ns, PrintWriter io, String indent, JJTreeNode expansion_unit) {
    io.println(indent + "try {");
    JJTreeCodeGenerator.closeJJTreeComment(io);

    expansion_unit.jjtAccept(this, io);

    JJTreeCodeGenerator.openJJTreeComment(io, null);

    Hashtable<String, String> thrown_set = new Hashtable<>();
    JJTreeCodeGenerator.findThrown(ns, thrown_set, expansion_unit);
    Enumeration<String> thrown_names = thrown_set.elements();
    insertCatchBlocks(ns, io, thrown_names, indent);

    io.println(indent + "} " + getTryFinally() + "{");
    if (ns.usesCloseNodeVar()) {
      io.println(indent + "  if (" + ns.closedVar + ") {");
      insertCloseNodeCode(ns, io, indent + "    ", true, expansion_unit.jjtOptions());
      io.println(indent + "  }");
    }
    io.println(indent + "}");
    JJTreeCodeGenerator.closeJJTreeComment(io);
  }

  public abstract void generateJJTree(JJTreeOptions options);
}
