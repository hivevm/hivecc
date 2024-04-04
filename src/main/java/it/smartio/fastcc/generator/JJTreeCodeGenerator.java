// Copyright 2011 Google Inc. All Rights Reserved.
// Author: sreeni@google.com (Sreeni Viswanadha)

package it.smartio.fastcc.generator;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import it.smartio.fastcc.jjtree.ASTBNFAction;
import it.smartio.fastcc.jjtree.ASTBNFDeclaration;
import it.smartio.fastcc.jjtree.ASTBNFNodeScope;
import it.smartio.fastcc.jjtree.ASTBNFNonTerminal;
import it.smartio.fastcc.jjtree.ASTBNFOneOrMore;
import it.smartio.fastcc.jjtree.ASTBNFSequence;
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
import it.smartio.fastcc.jjtree.JJTreeWriter;
import it.smartio.fastcc.jjtree.Node;
import it.smartio.fastcc.jjtree.NodeScope;
import it.smartio.fastcc.jjtree.Token;

public abstract class JJTreeCodeGenerator extends JJTreeParserDefaultVisitor {

  private final Set<String> nodesToGenerate = new HashSet<>();

  protected final void addType(String nodeType) {
    if (!nodeType.equals("Node")) {
      nodesToGenerate.add(nodeType);
    }
  }

  protected final Iterable<String> nodesToGenerate() {
    return nodesToGenerate;
  }

  protected abstract String getPointer();

  protected abstract String getBoolean();

  protected abstract String getTryFinally();

  @Override
  public final Object defaultVisit(Node node, JJTreeWriter data) {
    data.handleJJTreeNode((JJTreeNode) node, this);
    return null;
  }

  @Override
  public final Object visit(ASTGrammar node, JJTreeWriter data) {
    return node.childrenAccept(this, data);
  }

  @Override
  public final Object visit(ASTBNFAction node, JJTreeWriter writer) {
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
        if (p instanceof ASTBNFSequence) {
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
        writer.openCodeBlock(null);
        insertCloseNodeCode(ns, writer, getIndentation(node) + "  ", false, node.jjtOptions());
        writer.closeCodeBlock();
      }
    }

    return writer.handleJJTreeNode(node, this);
  }

  @Override
  public Object visit(ASTCompilationUnit node, JJTreeWriter writer) {
    Token token = node.getFirstToken();

    while (true) {
      writer.printToken(node, token);
      if (token == node.getLastToken()) {
        return null;
      }
      token = token.next;
    }
  }

  @Override
  public final Object visit(ASTBNFDeclaration node, JJTreeWriter writer) {
    if (!node.node_scope.isVoid()) {
      String indent = "";
      if (node.getLastToken().next == node.getFirstToken()) {
        indent = "  ";
      } else {
        for (int i = 1; i < node.getFirstToken().beginColumn; ++i) {
          indent += " ";
        }
      }

      writer.openCodeBlock(node.node_scope.getNodeDescriptorText());
      insertOpenNodeCode(node.node_scope, writer, indent, node.jjtOptions());
      writer.print(indent + "try {");
      writer.closeCodeBlock();
    }

    return writer.handleJJTreeNode(node, this);
  }

  @Override
  public final Object visit(ASTBNFNodeScope node, JJTreeWriter writer) {
    if (node.node_scope.isVoid()) {
      return writer.handleJJTreeNode(node, this);
    }

    String indent = getIndentation(node.expansion_unit);
    // tryExpansionUnit0(node.node_scope, io, indent, node.expansion_unit);
    node.expansion_unit.jjtAccept(this, writer);
    writer.println();
    writer.print("}");
    catchExpansionUnit(node.node_scope, writer, indent, node.expansion_unit);
    return true;
  }

  @Override
  public final Object visit(ASTExpansionNodeScope node, JJTreeWriter writer) {
    String indent = getIndentation(node.expansion_unit) + "  ";
    writer.openCodeBlock(node.node_scope.getNodeDescriptor().getDescriptor());
    insertOpenNodeCode(node.node_scope, writer, indent, node.jjtOptions());
    writer.print(indent + "try {");
    writer.closeCodeBlock();

    node.expansion_unit.jjtAccept(this, writer);

    catchExpansionUnit(node.node_scope, writer, indent, node.expansion_unit);

    // Print the "whiteOut" equivalent of the Node descriptor to preserve
    // line numbers in the generated file.
    ((ASTNodeDescriptor) node.jjtGetChild(1)).jjtAccept(this, writer);
    return null;
  }

  private final String getIndentation(JJTreeNode n) {
    String s = "";
    for (int i = 1; i < n.getFirstToken().beginColumn; ++i) {
      s += " ";
    }
    return s;
  }

  protected abstract void insertOpenNodeCode(NodeScope ns, JJTreeWriter writer, String indent, JJTreeOptions options);

  private final void insertCloseNodeCode(NodeScope ns, JJTreeWriter writer, String indent, boolean isFinal,
      JJTreeOptions options) {
    String closeNode = ns.getNodeDescriptor().closeNode(ns.nodeVar);
    writer.println(indent + closeNode);
    if (ns.usesCloseNodeVar() && !isFinal) {
      writer.println(indent + ns.closedVar + " = false;");
    }
    if (options.getNodeScopeHook()) {
      writer.println(indent + "if (jjtree.nodeCreated()) {");
      writer.println(indent + " jjtreeCloseNodeScope(" + ns.nodeVar + ");");
      writer.println(indent + "}");
    }

    if (options.getTrackTokens()) {
      writer.println(indent + ns.nodeVar + getPointer() + "jjtSetLastToken(getToken(0));");
    }
  }

  protected abstract void insertCatchBlocks(NodeScope ns, JJTreeWriter writer, Enumeration<String> thrown_names,
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

  private void catchExpansionUnit(NodeScope ns, JJTreeWriter writer, String indent, JJTreeNode expansion_unit) {
    writer.openCodeBlock(null);

    Hashtable<String, String> thrown_set = new Hashtable<>();
    JJTreeCodeGenerator.findThrown(ns, thrown_set, expansion_unit);
    Enumeration<String> thrown_names = thrown_set.elements();
    insertCatchBlocks(ns, writer, thrown_names, indent);

    writer.println(indent + "} " + getTryFinally() + "{");
    if (ns.usesCloseNodeVar()) {
      writer.println(indent + "  if (" + ns.closedVar + ") {");
      insertCloseNodeCode(ns, writer, indent + "    ", true, expansion_unit.jjtOptions());
      writer.println(indent + "  }");
    }
    writer.print(indent + "}");
    writer.closeCodeBlock();
  }

  public abstract void generateJJTree(JJTreeOptions options);
}
