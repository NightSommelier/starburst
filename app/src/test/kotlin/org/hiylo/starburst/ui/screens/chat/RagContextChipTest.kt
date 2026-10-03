/*
 * Copyright(c) 2016 - Present, Clouds Studio Holding Limited. All rights reserved.
 * Project : StarBurst
 * File : RagContextChipTest.kt
 * Date : 2026/10/02 10:20:00
 * Author : Hsi Chu
 * Contact : hiylo@live.com
 * Version : V1.0
 */
package org.hiylo.starburst.ui.screens.chat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [RagContextChip] 的纯函数回归：识别后端注入的 RAG 上下文块、统计 [来源N] 引用片段、
 * 展开时剥掉包装标记。这些函数只影响渲染，不改写 Part.Text 里的原始文本。
 */
class RagContextChipTest {

    private val block = buildString {
        append(RAG_CONTEXT_START_MARKER).append('\n')
        append("[来源1] docs/architecture.md：项目结构说明").append('\n')
        append("[来源2] docs/api.md：接口约定").append('\n')
        append(RAG_CONTEXT_END_MARKER)
    }

    private val blockBody =
        "[来源1] docs/architecture.md：项目结构说明\n[来源2] docs/api.md：接口约定"

    @Test
    fun `marker pair marks a rag context block`() {
        assertTrue(isRagContextBlock(block))
        assertTrue(isRagContextBlock("${RAG_CONTEXT_START_MARKER}单行正文$RAG_CONTEXT_END_MARKER"))
    }

    @Test
    fun `half open markers are not a rag context block`() {
        assertFalse(isRagContextBlock("${RAG_CONTEXT_START_MARKER}只有起始标记"))
        assertFalse(isRagContextBlock("只有结束标记$RAG_CONTEXT_END_MARKER"))
        assertFalse(isRagContextBlock("普通用户消息"))
        assertFalse(isRagContextBlock(""))
    }

    @Test
    fun `markers not at the start render as a plain message`() {
        // 起始标记必须落在开头，否则用户自己输入的标记文本会被误吞成知识库纸条。
        assertFalse(isRagContextBlock("前缀 $block"))
        assertFalse(isRagContextBlock(" $block"))
    }

    @Test
    fun `counts source snippets inside the block`() {
        assertEquals(2, countRagContextSources(block))
        assertEquals(1, countRagContextSources("$RAG_CONTEXT_START_MARKER[来源1] 引用$RAG_CONTEXT_END_MARKER"))
    }

    @Test
    fun `block without snippets counts zero`() {
        assertEquals(0, countRagContextSources("普通消息，没有引用"))
        assertEquals(0, countRagContextSources(""))
        assertEquals(0, countRagContextSources("$RAG_CONTEXT_START_MARKER$RAG_CONTEXT_END_MARKER"))
    }

    @Test
    fun `repeated source snippets are each counted`() {
        assertEquals(3, countRagContextSources("[来源1] a [来源1] b [来源2] c"))
    }

    @Test
    fun `stripping markers keeps only the body`() {
        assertEquals(blockBody, stripRagContextMarkers(block))
    }

    @Test
    fun `text without markers is kept apart from trimming`() {
        assertEquals("普通消息", stripRagContextMarkers("  普通消息  "))
    }

    @Test
    fun `half open markers are stripped on whichever side exists`() {
        assertEquals(
            "只有起始标记后的正文",
            stripRagContextMarkers("$RAG_CONTEXT_START_MARKER  只有起始标记后的正文  "),
        )
        assertEquals("", stripRagContextMarkers("$RAG_CONTEXT_END_MARKER之后的残留"))
    }

    @Test
    fun `adjacent markers strip to empty body`() {
        assertEquals("", stripRagContextMarkers("$RAG_CONTEXT_START_MARKER$RAG_CONTEXT_END_MARKER"))
    }
}