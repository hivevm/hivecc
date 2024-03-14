package it.smartio.fastcc.jjtree;

public class ASTExpansionNodeScope extends JJTreeNode {

  public NodeScope  node_scope;
  public JJTreeNode expansion_unit;

  public ASTExpansionNodeScope(JJTreeParser p, int id) {
    super(p, id);
  }

  @Override
  public final Object jjtAccept(JJTreeParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}