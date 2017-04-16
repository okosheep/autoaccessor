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
package info.okoshi.context;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.Modifier;

/**
 * Modifiers information context.<br>
 *
 * @version 1.0.0
 * @author okosheep
 */
public class ModifiersInfo {

  /** Abstract field */
  private boolean abstractValue;

  /** Annotation field */
  private boolean annotationValue;

  /** Final field */
  private boolean finalValue;

  /** Native field */
  private boolean nativeValue;

  /** Package private field */
  private boolean packagePrivateValue;

  /** Private field */
  private boolean privateValue;

  /** Protected field */
  private boolean protectedValue;

  /** Public field */
  private boolean publicValue;

  /** Static field */
  private boolean staticValue;

  /** Synchronized field */
  private boolean synchronizedValue;

  /** Volatile field */
  private boolean volatileValue;

  /**
   * Create {@link ModifiersInfo} instance.<br>
   *
   * @param modifiers
   */
  public ModifiersInfo(final List<?> modifiers) {
    for (final Object modifierObject : modifiers) {
      IExtendedModifier extendedModifier = (IExtendedModifier) modifierObject;
      if (extendedModifier.isModifier()) {
        final Modifier modifier = (Modifier) modifierObject;

        abstractValue = modifier.isAbstract();
        annotationValue = modifier.isAnnotation();
        finalValue = modifier.isFinal();
        nativeValue = modifier.isNative();
        privateValue = modifier.isPrivate();
        protectedValue = modifier.isProtected();
        publicValue = modifier.isPublic();
        staticValue = modifier.isStatic();
        synchronizedValue = modifier.isSynchronized();
        volatileValue = modifier.isVolatile();
      }
    }

    if (!(publicValue || protectedValue || privateValue)) {
      privateValue = true;
      packagePrivateValue = true;
    }
  }

  public boolean isAbstract() {
    return abstractValue;
  }

  public boolean isAnnotationValue() {
    return annotationValue;
  }

  public boolean isFinal() {
    return finalValue;
  }

  public boolean isNatvieValue() {
    return nativeValue;
  }

  public boolean isPackagePrivate() {
    return packagePrivateValue;
  }

  public boolean isPrivate() {
    return privateValue;
  }

  public boolean isProtected() {
    return protectedValue;
  }

  public boolean isPublic() {
    return publicValue;
  }

  public boolean isStatic() {
    return staticValue;
  }

  public boolean isSynchronizedValue() {
    return synchronizedValue;
  }

  public boolean isVolatile() {
    return volatileValue;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}