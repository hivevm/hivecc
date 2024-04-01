
package it.smartio.fastcc.jjtree;

public class ASTBNFNodeScope extends JJTreeNode {

  public NodeScope  node_scope;
  public JJTreeNode expansion_unit;

  public ASTBNFNodeScope(JJTreeParser p, int id) {
    super(p, id);
  }

  @Override
  public final Object jjtAccept(JJTreeParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}