// Generated by FastCC v.8.0 - Do not edit this line!

package it.smartio.fastcc.jjtree;

class ASTBNF extends ASTProduction {

  ASTBNF(int id) {
    super(id);
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
// FastCC Checksum=D39DFAFBDBA2C64ACD7E7174710BFB0C (Do not edit this line!)
// FastCC Options: NODE_FACTORY='', VISITOR_DATA_TYPE='', VISITOR='true', NODE_CLASS='JJTreeNode', NODE_TYPE='ASTBNF', VISITOR_RETURN_TYPE_VOID='false', VISITOR_EXCEPTION='', PARSER_NAME='JJTreeParser', VISITOR_RETURN_TYPE='Object'
