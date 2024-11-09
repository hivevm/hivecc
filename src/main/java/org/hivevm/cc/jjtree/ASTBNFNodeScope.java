// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.cc.jjtree;

public class ASTBNFNodeScope extends JJTreeNode {

  public NodeScope  node_scope;
  public JJTreeNode expansion_unit;

  public ASTBNFNodeScope(JJTreeParser p, int id) {
    super(p, id);
  }

  @Override
  public final Object jjtAccept(JJTreeParserVisitor visitor, JJTreeWriter data) {
    return visitor.visit(this, data);
  }
}