/*
 * MIT License
 *
 * Copyright (c) 2017 okosheep
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.okoshi.visitor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import info.okoshi.GenerateAccessorTypes;
import info.okoshi.context.ModifiersInfo;

/**
 * Accessor generate visitor.<br>
 *
 * @version 1.0.0
 * @author okosheep
 */
public class AccessorsGenerateVisitor extends ASTVisitor {

  /** Accessor type for generating */
  private GenerateAccessorTypes generateAccessorType = GenerateAccessorTypes.GETTERS_AND_SETTERS;

  /**
   * Set accessor type for generating.<br>
   * 
   * @param generateAccessorType
   *          {@link GenerateAccessorTypes} object
   */
  public void setGenerateAccessorType(GenerateAccessorTypes generateAccessorType) {
    this.generateAccessorType = generateAccessorType;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldDeclaration)
   */
  @Override
  public boolean visit(FieldDeclaration node) {
    if (!isAccessbileField(node)) {
      return super.visit(node);
    }

    TypeDeclaration parent = (TypeDeclaration) node.getParent();
    AST ast = parent.getAST();

    VariableDeclarationFragment var = (VariableDeclarationFragment) node.fragments().get(0);
    String name = var.getName().getIdentifier();

    switch (generateAccessorType) {
    case GETTERS_AND_SETTERS:
      addAccessorMethodIfNotExists(parent, generateGetter(node, ast, name));
      addAccessorMethodIfNotExists(parent, generateSetter(node, ast, name));
      break;
    case GETTERS_ONLY:
      addAccessorMethodIfNotExists(parent, generateGetter(node, ast, name));
      break;
    default: // means "case SETTERS_ONLY:"
      addAccessorMethodIfNotExists(parent, generateSetter(node, ast, name));
    }

    return super.visit(node);
  }

  /**
   * Add accessor method delcarations.<br>
   *
   * @param parent
   *          {@link TypeDeclaration} object
   * @param accessorMethod
   *          accessor method
   */
  @SuppressWarnings("unchecked")
  private void addAccessorMethodIfNotExists(TypeDeclaration parent, MethodDeclaration accessorMethod) {
    String accessorMethodName = accessorMethod.getName().getIdentifier();
    for (Object bodyDeclaration : parent.bodyDeclarations()) {
      if (bodyDeclaration instanceof MethodDeclaration) {
        MethodDeclaration methodDec = (MethodDeclaration) bodyDeclaration;
        String identifier = methodDec.getName().getIdentifier();
        if (StringUtils.equals(accessorMethodName, identifier)) {
          return;
        }
      }
    }

    parent.bodyDeclarations().add(accessorMethod);
  }

  /**
   * Create getter method javadoc.<rb>
   *
   * @param node
   *          {@link FieldDeclaration} object
   * @param ast
   *          {@link AST} object
   * @return {@link Javadoc} object
   */
  @SuppressWarnings("unchecked")
  private Javadoc createGetterJavadoc(FieldDeclaration node, AST ast) {
    Javadoc javadoc = ast.newJavadoc();
    String propertyName = getJavadoc(node);
    javadoc.tags().add(createJavadocTextLine(ast, propertyName + "を取得する。"));
    javadoc.tags().add(createJavadocTextLine(ast, ""));
    javadoc.tags().add(createJavadocTextLine(ast, "@return", propertyName));
    return javadoc;
  }

  /**
   * Create a line of javadoc.<rb>
   *
   * @param ast
   *          {@link AST} object
   * @param line
   *          text
   * @return {@link TagElement} object
   */
  private TagElement createJavadocTextLine(AST ast, String line) {
    return createJavadocTextLine(ast, null, line);
  }

  /**
   * Create a line of javadoc.<rb>
   *
   * @param ast
   *          {@link AST} object
   * @param tagName
   *          tag name
   * @param line
   *          text
   * @return {@link TagElement} object
   */
  @SuppressWarnings("unchecked")
  private TagElement createJavadocTextLine(AST ast, String tagName, String line) {
    TagElement newTag = ast.newTagElement();
    newTag.setTagName(tagName);
    TextElement newText = ast.newTextElement();
    newText.setText(line);
    newTag.fragments().add(newText);
    return newTag;
  }

  /**
   * Create setter method javadoc.<rb>
   *
   * @param node
   *          {@link FieldDeclaration} object
   * @param ast
   *          {@link AST} object
   * @param fieldName
   *          field name
   * @return {@link Javadoc} object
   */
  @SuppressWarnings("unchecked")
  private Javadoc createSetterJavadoc(FieldDeclaration node, AST ast, String fieldName) {
    Javadoc javadoc = ast.newJavadoc();
    String propertyName = getJavadoc(node);
    javadoc.tags().add(createJavadocTextLine(ast, propertyName + "を設定する。"));
    javadoc.tags().add(createJavadocTextLine(ast, ""));
    javadoc.tags().add(createJavadocTextLine(ast, "@param", fieldName + propertyName));
    return javadoc;
  }

  /**
   * Generate getter method.<br>
   *
   * @param node
   *          {@link FieldDeclaration} object
   * @param ast
   *          {@link AST} object
   * @param name
   *          name of field variable
   * @return {@link MethodDeclaration} object
   */
  @SuppressWarnings("unchecked")
  private MethodDeclaration generateGetter(FieldDeclaration node, AST ast, String name) {
    MethodDeclaration getter = ast.newMethodDeclaration();
    if (node.getJavadoc() != null) {
      getter.setJavadoc(createGetterJavadoc(node, ast));
    }
    getter.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

    Type type = (Type) ASTNode.copySubtree(ast, node.getType());
    getter.setReturnType2(type);
    getter.setName(ast.newSimpleName(getGetterPrefix(type) + StringUtils.capitalize(name)));

    Block block = ast.newBlock();
    ReturnStatement returnStatement = ast.newReturnStatement();
    returnStatement.setExpression(ast.newSimpleName(name));
    block.statements().add(returnStatement);
    getter.setBody(block);
    return getter;
  }

  /**
   * Generate setter method.<br>
   *
   * @param node
   *          {@link FieldDeclaration} object
   * @param ast
   *          {@link AST} object
   * @param name
   *          name of field variable
   * @return {@link MethodDeclaration} object
   */
  @SuppressWarnings("unchecked")
  private MethodDeclaration generateSetter(FieldDeclaration node, AST ast, String name) {
    MethodDeclaration setter = ast.newMethodDeclaration();
    if (node.getJavadoc() != null) {
      setter.setJavadoc(createSetterJavadoc(node, ast, name));
    }
    setter.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
    setter.setName(ast.newSimpleName("set" + StringUtils.capitalize(name)));

    SingleVariableDeclaration singleVariable = ast.newSingleVariableDeclaration();
    singleVariable.setType((Type) ASTNode.copySubtree(ast, node.getType()));
    singleVariable.setName(ast.newSimpleName(name));
    setter.parameters().add(singleVariable);

    Block block = ast.newBlock();
    Assignment assignment = ast.newAssignment();
    FieldAccess fieldAccess = ast.newFieldAccess();
    fieldAccess.setExpression(ast.newThisExpression());
    fieldAccess.setName(ast.newSimpleName(name));
    assignment.setLeftHandSide(fieldAccess);
    assignment.setOperator(Assignment.Operator.ASSIGN);
    assignment.setRightHandSide(ast.newSimpleName(name));
    block.statements().add(ast.newExpressionStatement(assignment));
    setter.setBody(block);

    return setter;
  }

  /**
   * Get getter method prefix.<br>
   *
   * @param type
   *          {@link Type} object
   * @return "is" returns when type is primitive boolean, otherwise "get"
   */
  private String getGetterPrefix(Type type) {
    if (type.isPrimitiveType() && "boolean".equals(type.toString())) {
      return "is";
    }
    return "get";
  }

  /**
   * Get javadoc text from field.<br>
   *
   * @param field
   *          {@link FieldDeclaration} object
   * @return javadoc text
   */
  @SuppressWarnings("unchecked")
  private String getJavadoc(FieldDeclaration field) {
    Javadoc javadoc = field.getJavadoc();
    StringBuilder builder = new StringBuilder();
    for (TagElement tag : (List<TagElement>) javadoc.tags()) {
      if (tag.getTagName() != null) {
        continue;
      }
      for (Object element : tag.fragments()) {
        builder.append(element);
      }
    }

    return builder.toString();
  }

  /**
   * Check field for accessible.<br>
   *
   * @param node
   *          {@link FieldDeclaration} object
   * @return {@code true} is accessible, otherwise {@code false}
   */
  private boolean isAccessbileField(FieldDeclaration node) {
    ModifiersInfo info = new ModifiersInfo(node.modifiers());
    if (info.isFinal() || info.isStatic() || info.isAbstract()) {
      return false;
    }
    return true;
  }
}
