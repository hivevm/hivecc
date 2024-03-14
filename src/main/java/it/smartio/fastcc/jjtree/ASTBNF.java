
package it.smartio.fastcc.jjtree;

class ASTBNF extends ASTProduction {

  public ASTBNF(JJTreeParser p, int id) {
    super(p, id);
    this.throws_list.add("ParseException");
    this.throws_list.add("RuntimeException");
  }

  @Override
  public final Object jjtAccept(JJTreeParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  @Override
  public final String toString() {
    return super.toString() + ": " + this.name;
  }
}
