package it.smartio.fastcc.jjtree;

public class ASTBNFDeclaration extends JJTreeNode {

  public NodeScope node_scope;

  public ASTBNFDeclaration(JJTreeParser p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  @Override
  public final Object jjtAccept(JJTreeParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}