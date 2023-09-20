// Copyright 2011 Google Inc. All Rights Reserved.
// Author: sreeni@google.com (Sreeni Viswanadha)

package it.smartio.fastcc.generator.cpp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.smartio.fastcc.FastCC;
import it.smartio.fastcc.generator.JJTreeCodeGenerator;
import it.smartio.fastcc.jjtree.ASTNodeDescriptor;
import it.smartio.fastcc.jjtree.JJTreeGlobals;
import it.smartio.fastcc.jjtree.JJTreeOptions;
import it.smartio.fastcc.jjtree.NodeScope;
import it.smartio.fastcc.parser.Options;
import it.smartio.fastcc.utils.DigestOptions;
import it.smartio.fastcc.utils.DigestWriter;
import it.smartio.fastcc.utils.Template;

public class CppTreeGenerator extends JJTreeCodeGenerator {

  @Override
  protected final String getPointer() {
    return "->";
  }

  @Override
  protected final String getBoolean() {
    return "bool";
  }

  @Override
  protected final void insertOpenNodeCode(NodeScope ns, PrintWriter io, String indent, JJTreeOptions options) {
    String type = ns.node_descriptor.getNodeType();
    final String nodeClass;
    if ((options.getNodeClass().length() > 0) && !options.getMulti()) {
      nodeClass = options.getNodeClass();
    } else {
      nodeClass = type;
    }

    CppTreeGenerator.addType(type);

    io.print(indent + nodeClass + " *" + ns.nodeVar + " = ");
    if (options.getNodeFactory().equals("*")) {
      // Old-style multiple-implementations.
      io.println("(" + nodeClass + "*)" + nodeClass + "::jjtCreate(" + ns.node_descriptor.getNodeId() + ");");
    } else if (options.getNodeFactory().length() > 0) {
      io.println(
          "(" + nodeClass + "*)" + options.getNodeFactory() + "->jjtCreate(" + ns.node_descriptor.getNodeId() + ");");
    } else {
      io.println("new " + nodeClass + "(" + ns.node_descriptor.getNodeId() + ");");
    }

    if (ns.usesCloseNodeVar()) {
      io.println(indent + getBoolean() + " " + ns.closedVar + " = true;");
    }
    io.println(indent + ns.node_descriptor.openNode(ns.nodeVar));
    if (options.getNodeScopeHook()) {
      io.println(indent + "jjtreeOpenNodeScope(" + ns.nodeVar + ");");
    }

    if (options.getTrackTokens()) {
      io.println(indent + ns.nodeVar + getPointer() + "jjtSetFirstToken(getToken(1));");
    }
  }

  @Override
  protected final void insertCatchBlocks(NodeScope ns, PrintWriter io, Enumeration<String> thrown_names,
      String indent) {
    // if (thrown_names.hasMoreElements()) {
    io.println(indent + "} catch (...) {"); // " + ns.exceptionVar + ") {");

    if (ns.usesCloseNodeVar()) {
      io.println(indent + "  if (" + ns.closedVar + ") {");
      io.println(indent + "    jjtree.clearNodeScope(" + ns.nodeVar + ");");
      io.println(indent + "    " + ns.closedVar + " = false;");
      io.println(indent + "  } else {");
      io.println(indent + "    jjtree.popNode();");
      io.println(indent + "  }");
    }
  }

  @Override
  public final void generateJJTree(JJTreeOptions o) {
    CppTreeGenerator.generateTreeClasses(o);
    CppTreeGenerator.generateTreeConstants(o);
    CppTreeGenerator.generateVisitors(o);
    CppTreeGenerator.generateTreeState(o);
  }

  private static void generateTreeState(JJTreeOptions o) {
    DigestOptions options = DigestOptions.get(o);
    options.put(FastCC.PARSER_NAME, JJTreeGlobals.parserName);
    String filePrefix = new File(o.getOutputDirectory(), "TreeState").getAbsolutePath();


    File file = new File(filePrefix + ".h");
    try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, options)) {
      CppTreeGenerator.generateFile(writer, "/templates/cpp/TreeState.h.template", writer.options());
    } catch (IOException e) {
      e.printStackTrace();
    }

    file = new File(filePrefix + ".cc");
    try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, options)) {
      CppTreeGenerator.generateFile(writer, "/templates/cpp/TreeState.cc.template", writer.options());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static List<String> headersForJJTreeH = new ArrayList<>();

  private static Set<String>  nodesToGenerate   = new HashSet<>();

  private static void addType(String type) {
    if (!type.equals("Node")) {
      CppTreeGenerator.nodesToGenerate.add(type);
    }
  }

  private static String nodeIncludeFile(JJTreeOptions o) {
    return new File(o.getOutputDirectory(), "Node.h").getAbsolutePath();
  }

  private static String nodeImplFile(JJTreeOptions o) {
    return new File(o.getOutputDirectory(), "Node.cc").getAbsolutePath();
  }

  private static String jjtreeIncludeFile(String s, JJTreeOptions o) {
    return new File(o.getOutputDirectory(), s + ".h").getAbsolutePath();
  }

  private static String jjtreeImplFile(String s, JJTreeOptions o) {
    return new File(o.getOutputDirectory(), s + ".cc").getAbsolutePath();
  }

  private static void generateTreeClasses(JJTreeOptions o) {
    CppTreeGenerator.generateNodeHeader(o);
    CppTreeGenerator.generateNodeImpl(o);
    CppTreeGenerator.generateMultiTreeImpl(o);
    CppTreeGenerator.generateOneTreeInterface(o);
    // generateOneTreeImpl();
    CppTreeGenerator.generateTreeInterface(o);
  }

  private static void generateNodeHeader(JJTreeOptions o) {
    DigestOptions optionMap = DigestOptions.get(o);
    optionMap.put(FastCC.PARSER_NAME, JJTreeGlobals.parserName);
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_TYPE, CppTreeGenerator.getVisitorReturnType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_DATA_TYPE, CppTreeGenerator.getVisitorArgumentType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_VOID,
        Boolean.valueOf(CppTreeGenerator.getVisitorReturnType(o).equals("void")));

    File file = new File(CppTreeGenerator.nodeIncludeFile(o));
    try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, optionMap)) {
      CppTreeGenerator.generateFile(writer, "/templates/cpp/Node.h.template", writer.options());
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateNodeImpl(JJTreeOptions o) {
    DigestOptions optionMap = DigestOptions.get(o);
    optionMap.put(FastCC.PARSER_NAME, JJTreeGlobals.parserName);
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_TYPE, CppTreeGenerator.getVisitorReturnType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_DATA_TYPE, CppTreeGenerator.getVisitorArgumentType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_VOID,
        Boolean.valueOf(CppTreeGenerator.getVisitorReturnType(o).equals("void")));

    File file = new File(CppTreeGenerator.nodeImplFile(o));
    try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, optionMap)) {
      CppTreeGenerator.generateFile(writer, "/templates/cpp/Node.cc.template", writer.options());
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateTreeInterface(JJTreeOptions o) {
    String node = "Tree";
    DigestOptions optionMap = DigestOptions.get(o);
    optionMap.put(FastCC.PARSER_NAME, JJTreeGlobals.parserName);
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_TYPE, CppTreeGenerator.getVisitorReturnType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_DATA_TYPE, CppTreeGenerator.getVisitorArgumentType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_VOID,
        Boolean.valueOf(CppTreeGenerator.getVisitorReturnType(o).equals("void")));
    optionMap.put(FastCC.JJTREE_NODE_TYPE, node);

    File file = new File(CppTreeGenerator.jjtreeIncludeFile(node, o));
    try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, optionMap)) {
      CppTreeGenerator.generateFile(writer, "/templates/cpp/Tree.h.template", writer.options());
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateMultiTreeImpl(JJTreeOptions o) {
    for (String node : CppTreeGenerator.nodesToGenerate) {
      File file = new File(CppTreeGenerator.jjtreeImplFile(node, o));
      DigestOptions optionMap = DigestOptions.get(o);
      optionMap.put(FastCC.PARSER_NAME, JJTreeGlobals.parserName);
      optionMap.put(FastCC.JJTREE_VISITOR_RETURN_TYPE, CppTreeGenerator.getVisitorReturnType(o));
      optionMap.put(FastCC.JJTREE_VISITOR_DATA_TYPE, CppTreeGenerator.getVisitorArgumentType(o));
      optionMap.put(FastCC.JJTREE_VISITOR_RETURN_VOID,
          Boolean.valueOf(CppTreeGenerator.getVisitorReturnType(o).equals("void")));
      optionMap.put(FastCC.JJTREE_NODE_TYPE, node);

      try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, optionMap)) {
        CppTreeGenerator.generateFile(writer, "/templates/cpp/MultiNode.cc.template", writer.options());
      } catch (IOException e) {
        throw new Error(e.toString());
      }
    }
  }


  private static void generateOneTreeInterface(JJTreeOptions o) {
    DigestOptions optionMap = DigestOptions.get(o);
    optionMap.put(FastCC.PARSER_NAME, JJTreeGlobals.parserName);
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_TYPE, CppTreeGenerator.getVisitorReturnType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_DATA_TYPE, CppTreeGenerator.getVisitorArgumentType(o));
    optionMap.put(FastCC.JJTREE_VISITOR_RETURN_VOID,
        Boolean.valueOf(CppTreeGenerator.getVisitorReturnType(o).equals("void")));

    File file = new File(o.getOutputDirectory(), JJTreeGlobals.parserName + "Tree.h");
    try (DigestWriter writer = DigestWriter.create(file, FastCC.VERSION, optionMap)) {
      // PrintWriter ostr = outputFile.getPrintWriter();
      file.getName().replace('.', '_').toUpperCase();
      writer.println("#ifndef JAVACC_ONE_TREE_H");
      writer.println("#define JAVACC_ONE_TREE_H");
      writer.println();
      writer.println("#include \"Node.h\"");
      for (String s : CppTreeGenerator.nodesToGenerate) {
        writer.println("#include \"" + s + ".h\"");
      }
      writer.println("#endif");
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static void generateTreeConstants(JJTreeOptions o) {
    String name = JJTreeGlobals.parserName + "TreeConstants";
    File file = new File(o.getOutputDirectory(), name + ".h");
    CppTreeGenerator.headersForJJTreeH.add(file.getName());

    try (DigestWriter ostr = DigestWriter.create(file, FastCC.VERSION, DigestOptions.get(o))) {
      List<String> nodeIds = ASTNodeDescriptor.getNodeIds();
      List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

      ostr.println("#ifndef JAVACC_" + file.getName().replace('.', '_').toUpperCase());
      ostr.println("#define JAVACC_" + file.getName().replace('.', '_').toUpperCase());

      ostr.println("\n#include \"JavaCC.h\"");
      boolean hasNamespace = ((String) ostr.options().get(FastCC.JJPARSER_CPP_NAMESPACE)).length() > 0;
      if (hasNamespace) {
        ostr.println("namespace " + ostr.options().get(FastCC.JJPARSER_CPP_NAMESPACE) + " {");
      }
      ostr.println("enum {");
      for (int i = 0; i < nodeIds.size(); ++i) {
        String n = nodeIds.get(i);
        ostr.println("    " + n + " = " + i + ",");
      }

      ostr.println("};");
      ostr.println();

      for (int i = 0; i < nodeNames.size(); ++i) {
        ostr.print("static JJChar jjtNodeName_arr_" + i + "[] = ");
        String n = nodeNames.get(i);
        // ostr.println(" (JJChar*)\"" + n + "\",");
        CppOtherFilesGenerator.printCharArray(ostr, n);
        ostr.println(";");
      }
      ostr.println("static JJString jjtNodeName[] = {");
      for (int i = 0; i < nodeNames.size(); i++) {
        ostr.println("    jjtNodeName_arr_" + i + ",");
      }
      ostr.println("};");

      if (hasNamespace) {
        ostr.println("}");
      }


      ostr.println("#endif");
    } catch (IOException e) {
      throw new Error(e.toString());
    }
  }

  private static String getVisitMethodName(String className) {
    return "visit";
  }

  private static String getVisitorArgumentType(Options o) {
    String ret = o.stringValue(FastCC.JJTREE_VISITOR_DATA_TYPE);
    return (ret == null) || ret.equals("") || ret.equals("Object") ? "void *" : ret;
  }

  private static String getVisitorReturnType(Options o) {
    String ret = o.stringValue(FastCC.JJTREE_VISITOR_RETURN_TYPE);
    return (ret == null) || ret.equals("") || ret.equals("Object") ? "void " : ret;
  }

  private static void generateVisitors(JJTreeOptions o) {
    if (!o.getVisitor()) {
      return;
    }

    File file = new File(o.getOutputDirectory(), JJTreeGlobals.parserName + "Visitor.h");
    try (DigestWriter ostr = DigestWriter.create(file, FastCC.VERSION, DigestOptions.get(o))) {
      ostr.println("#ifndef " + file.getName().replace('.', '_').toUpperCase());
      ostr.println("#define " + file.getName().replace('.', '_').toUpperCase());
      ostr.println("\n#include \"JavaCC.h\"");
      ostr.println("#include \"" + JJTreeGlobals.parserName + "Tree.h" + "\"");

      boolean hasNamespace = ((String) ostr.options().get(FastCC.JJPARSER_CPP_NAMESPACE)).length() > 0;
      if (hasNamespace) {
        ostr.println("namespace " + ostr.options().get(FastCC.JJPARSER_CPP_NAMESPACE) + " {");
      }

      CppTreeGenerator.generateVisitorInterface(ostr, o);
      CppTreeGenerator.generateDefaultVisitor(ostr, o);

      if (hasNamespace) {
        ostr.println("}");
      }

      ostr.println("#endif");
    } catch (IOException ioe) {
      throw new Error(ioe.toString());
    }
  }

  private static void generateVisitorInterface(PrintWriter ostr, JJTreeOptions o) {
    String name = JJTreeGlobals.parserName + "Visitor";
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    ostr.println("class " + name);
    ostr.println("{");

    String argumentType = CppTreeGenerator.getVisitorArgumentType(o);
    String returnType = CppTreeGenerator.getVisitorReturnType(o);
    if (!o.getVisitorDataType().equals("")) {
      argumentType = o.getVisitorDataType();
    }
    ostr.println("  public:");

    ostr.println("  virtual " + returnType + " visit(const Node *node, " + argumentType + " data) = 0;");
    if (o.getMulti()) {
      for (String n : nodeNames) {
        if (n.equals("void")) {
          continue;
        }
        String nodeType = o.getNodePrefix() + n;
        ostr.println("  virtual " + returnType + " " + CppTreeGenerator.getVisitMethodName(nodeType) + "(const "
            + nodeType + " *node, " + argumentType + " data) = 0;");
      }
    }

    ostr.println("  virtual ~" + name + "() { }");
    ostr.println("};");
  }

  private static void generateDefaultVisitor(PrintWriter ostr, JJTreeOptions o) {
    String className = JJTreeGlobals.parserName + "DefaultVisitor";
    List<String> nodeNames = ASTNodeDescriptor.getNodeNames();

    ostr.println("class " + className + " : public " + JJTreeGlobals.parserName + "Visitor {");

    String argumentType = CppTreeGenerator.getVisitorArgumentType(o);
    String ret = CppTreeGenerator.getVisitorReturnType(o);

    ostr.println("public:");
    ostr.println("  virtual " + ret + " defaultVisit(const Node *node, " + argumentType + " data) = 0;");

    ostr.println("  virtual " + ret + " visit(const Node *node, " + argumentType + " data) {");
    ostr.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
    ostr.println("}");

    if (o.getMulti()) {
      for (String n : nodeNames) {
        if (n.equals("void")) {
          continue;
        }
        String nodeType = o.getNodePrefix() + n;
        ostr.println("  virtual " + ret + " " + CppTreeGenerator.getVisitMethodName(nodeType) + "(const " + nodeType
            + " *node, " + argumentType + " data) {");
        ostr.println("    " + (ret.trim().equals("void") ? "" : "return ") + "defaultVisit(node, data);");
        ostr.println("  }");
      }
    }
    ostr.println("  ~" + className + "() { }");
    ostr.println("};");
  }

  private static void generateFile(PrintWriter writer, String template, Map<String, Object> options)
      throws IOException {
    Template.of(template, options).write(writer);
  }
}
