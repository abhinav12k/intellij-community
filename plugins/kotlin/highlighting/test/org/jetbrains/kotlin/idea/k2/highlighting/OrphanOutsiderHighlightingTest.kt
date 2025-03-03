// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.kotlin.idea.k2.highlighting

import com.intellij.diff.DiffContentFactory
import com.intellij.psi.PsiDocumentManager
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.base.plugin.KotlinPluginKind
import org.jetbrains.kotlin.idea.base.test.KotlinJvmLightProjectDescriptor
import org.jetbrains.kotlin.idea.base.test.KotlinRoot
import org.jetbrains.kotlin.idea.base.test.NewLightKotlinCodeInsightFixtureTestCase
import org.junit.internal.runners.JUnit38ClassRunner
import org.junit.runner.RunWith

@RunWith(JUnit38ClassRunner::class)
class OrphanOutsiderHighlightingTest : NewLightKotlinCodeInsightFixtureTestCase() {
    override val pluginKind: KotlinPluginKind
        get() = KotlinPluginKind.FIR_PLUGIN

    fun testDiff() {
        // Orphan outsider files are analyzed as out-of-content-root
        // Error/warning highlighting is unavailable

        val documentText = """
            package test
            <info>abstract</info> <info>sealed</info> class <info>Foo</info>
        """.trimIndent()

        val diffContentFactory = DiffContentFactory.getInstance()
        val document = diffContentFactory.create(project, documentText, KotlinFileType.INSTANCE).document
        document.setReadOnly(false) // For 'ExpectedHighlightingData'

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)!!
        myFixture.openFileInEditor(psiFile.virtualFile)
        myFixture.checkHighlighting(true, true, true, false)
    }

    override fun getProjectDescriptor() = KotlinJvmLightProjectDescriptor.DEFAULT
    override fun getTestDataPath() = KotlinRoot.PATH.toString()
}