// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInsight.daemon.impl.quickfix;

import com.intellij.codeInsight.daemon.QuickFixBundle;
import com.intellij.codeInspection.EditorUpdater;
import com.intellij.codeInspection.PsiUpdateModCommandAction;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MakeClassInterfaceFix extends PsiUpdateModCommandAction<PsiClass> {
  private final boolean myMakeInterface;
  private final String myName;

  public MakeClassInterfaceFix(PsiClass aClass, final boolean makeInterface) {
    super(aClass);
    myMakeInterface = makeInterface;
    myName = aClass.getName();
  }

  @Override
  protected @Nullable Presentation getPresentation(@NotNull ActionContext context, @NotNull PsiClass element) {
    return Presentation.of(
      QuickFixBundle.message(myMakeInterface ? "make.class.an.interface.text" : "make.interface.an.class.text", myName));
  }

  @Override
  @NotNull
  public String getFamilyName() {
    return QuickFixBundle.message("make.class.an.interface.family");
  }

  @Override
  protected void invoke(@NotNull ActionContext context, @NotNull PsiClass myClass, @NotNull EditorUpdater updater) {
    final PsiReferenceList extendsList = myMakeInterface? myClass.getExtendsList() : myClass.getImplementsList();
    final PsiReferenceList implementsList = myMakeInterface? myClass.getImplementsList() : myClass.getExtendsList();
    if (extendsList != null) {
      for (PsiJavaCodeReferenceElement referenceElement : extendsList.getReferenceElements()) {
        referenceElement.delete();
      }
      if (implementsList != null) {
        for (PsiJavaCodeReferenceElement referenceElement : implementsList.getReferenceElements()) {
          extendsList.addAfter(referenceElement, null);
          referenceElement.delete();
        }
      }
    }
    convertPsiClass(myClass, myMakeInterface);
  }

  private static void convertPsiClass(PsiClass aClass, final boolean makeInterface) throws IncorrectOperationException {
    final IElementType lookFor = makeInterface? JavaTokenType.CLASS_KEYWORD : JavaTokenType.INTERFACE_KEYWORD;
    final PsiKeyword replaceWith = JavaPsiFacade.getElementFactory(aClass.getProject()).createKeyword(makeInterface? PsiKeyword.INTERFACE : PsiKeyword.CLASS);
    for (PsiElement psiElement = aClass.getFirstChild(); psiElement != null; psiElement = psiElement.getNextSibling()) {
      if (psiElement instanceof PsiKeyword psiKeyword && psiKeyword.getTokenType() == lookFor) {
        psiKeyword.replace(replaceWith);
        break;
      }
    }
  }
}
