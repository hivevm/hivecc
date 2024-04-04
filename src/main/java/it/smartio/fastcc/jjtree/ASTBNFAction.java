
package it.smartio.fastcc.jjtree;

public class ASTBNFAction extends JJTreeNode {

  public ASTBNFAction(JJTreeParser p, int id) {
    super(p, id);
  }

  public Node getScopingParent(NodeScope ns) {
    for (Node n = jjtGetParent(); n != null; n = n.jjtGetParent()) {
      if (n instanceof ASTBNFNodeScope) {
        if (((ASTBNFNodeScope) n).node_scope == ns) {
          return n;
        }
      } else if (n instanceof ASTExpansionNodeScope) {
        if (((ASTExpansionNodeScope) n).node_scope == ns) {
          return n;
        }
      }
    }
    return null;
  }


  @Override
  public final Object jjtAccept(JJTreeParserVisitor visitor, JJTreeWriter data) {
    return visitor.visit(this, data);
  }
}
