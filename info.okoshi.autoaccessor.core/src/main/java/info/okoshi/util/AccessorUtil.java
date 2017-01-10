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
package info.okoshi.util;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Accessor method utility.<br>
 *
 * @version 1.0.0
 * @author okosheep
 */
public class AccessorUtil {

  /**
   * Generate getter/setter accessors.<br>
   *
   * @param event
   *          {@link ExecutionEvent} object
   * @param visitor
   *          {@link ASTVisitor} object
   */
  public static void generateAccessors(ExecutionEvent event, ASTVisitor visitor) {
    ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditor(event);
    IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
    IDocument document = editor.getDocumentProvider().getDocument(input);
    IFile file = input.getFile();
    ICompilationUnit source = JavaCore.createCompilationUnitFrom(file);

    ASTParser parser = ASTParser.newParser(AST.JLS8);
    parser.setSource(source);
    CompilationUnit root = (CompilationUnit) parser.createAST(null);

    root.recordModifications();
    root.accept(visitor);

    TextEdit textEdit = root.rewrite(document, null);
    try {
      textEdit.apply(document);
    } catch (BadLocationException | MalformedTreeException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Create {@link AccessorUtil} instance.<br>
   */
  private AccessorUtil() {
    throw new UnsupportedOperationException("Utility class is not instantiate.");
  }
}
