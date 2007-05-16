/*******************************************************************************
 * Copyright (c) 2002,2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.shrikeBT;

/**
 * This class represents get and getstatic instructions.
 */
public class GetInstruction extends Instruction {
  protected String type;
  protected String classType;
  protected String fieldName;

  GetInstruction(short opcode, String type, String classType, String fieldName) {
    this.opcode = opcode;
    this.type = type;
    this.classType = classType;
    this.fieldName = fieldName;
  }

  ConstantPoolReader getLazyConstantPool() {
    return null;
  }

  final static class Lazy extends GetInstruction {
    private ConstantPoolReader cp;
    private int index;

    Lazy(short opcode, ConstantPoolReader cp, int index) {
      super(opcode, null, null, null);
      this.index = index;
      this.cp = cp;
    }

    ConstantPoolReader getLazyConstantPool() {
      return cp;
    }

    int getCPIndex() {
      return index;
    }

    public String getClassType() {
      if (classType == null) {
        classType = cp.getConstantPoolMemberClassType(index);
      }
      return classType;
    }

    public String getFieldName() {
      if (fieldName == null) {
        fieldName = cp.getConstantPoolMemberName(index);
      }
      return fieldName;
    }

    public String getFieldType() {
      if (type == null) {
        type = cp.getConstantPoolMemberType(index);
      }
      return type;
    }
  }

  static GetInstruction make(ConstantPoolReader cp, int index, boolean isStatic) {
    return new Lazy(isStatic ? OP_getstatic : OP_getfield, cp, index);
  }

  public static GetInstruction make(String type, String className, String fieldName, boolean isStatic) {
    if (type == null) {
      throw new IllegalArgumentException("type must not be null");
    }
    if (className == null) {
      throw new IllegalArgumentException("className must not be null");
    }
    if (fieldName == null) {
      throw new IllegalArgumentException("fieldName must not be null");
    }
    return new GetInstruction(isStatic ? OP_getstatic : OP_getfield, type, className, fieldName);
  }

  final public boolean equals(Object o) {
    if (o instanceof GetInstruction) {
      GetInstruction i = (GetInstruction) o;
      return i.getFieldType().equals(getFieldType()) && i.getClassType().equals(getClassType())
          && i.getFieldName().equals(getFieldName()) && i.opcode == opcode;
    } else {
      return false;
    }
  }

  public String getClassType() {
    return classType;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getFieldType() {
    return type;
  }

  final public boolean isStatic() {
    return opcode == OP_getstatic;
  }

  final public int hashCode() {
    return getClassType().hashCode() + 11113 * getFieldType().hashCode() + 398011 * getFieldName().hashCode() + opcode;
  }

  final public int getPoppedCount() {
    return isStatic() ? 0 : 1;
  }

  final public String getPushedType(String[] types) {
    return getFieldType();
  }

  final public byte getPushedWordSize() {
    return Util.getWordSize(getFieldType());
  }

  public String toString() {
    return "Get(" + getFieldType() + "," + (isStatic() ? "STATIC" : "NONSTATIC") + "," + getClassType() + "," + getFieldName()
        + ")";
  }

  public void visit(Visitor v) throws IllegalArgumentException {
    if (v == null) {
      throw new IllegalArgumentException();
    }
    v.visitGet(this);
  }
    /* (non-Javadoc)
   * @see com.ibm.domo.cfg.IInstruction#isPEI()
   */
  public boolean isPEI() {
    return true;
  }
}