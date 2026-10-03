/*
 * Copyright(c) 2016 - Present, Clouds Studio Holding Limited. All rights reserved.
 * Project : StarBurst
 * File : DocumentPreviewUrlTest.kt
 * Date : 2026/10/02 10:22:00
 * Author : Hsi Chu
 * Contact : hiylo@live.com
 * Version : V1.0
 */
package org.hiylo.starburst.ui.screens.chat

import android.net.Uri
import org.hiylo.starburst.data.api.isSameOrigin
import org.hiylo.starburst.data.api.resolveDocumentUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

/**
 * 文档预览地址组装 + 文档下载地址同源校验的回归覆盖（H7）。
 *
 * [documentPreviewUrl] 定义在 ui.screens.chat（[DocumentPreviewSheet]），
 * [resolveDocumentUrl] / [isSameOrigin] 定义在 data.api（`BackendDocumentsApi`）。
 * 后两者用 `java.net.URL` 比较 scheme+host+port，是拒绝 Bearer token 外发的唯一闸门。
 */
class DocumentPreviewUrlTest {

    private val backend = "http://192.0.2.150:18090"
    private val fileUrl = "$backend/api/documents/5/download"

    @Test
    fun `preview url hangs the preview page off the backend root`() {
        assertTrue(documentPreviewUrl(backend, fileUrl).startsWith("$backend/doc/preview.html?src="))
    }

    @Test
    fun `preview url trims trailing slash of the backend root`() {
        assertTrue(documentPreviewUrl("$backend/", fileUrl).startsWith("$backend/doc/preview.html?src="))
        assertTrue(documentPreviewUrl("$backend///", fileUrl).startsWith("$backend/doc/preview.html?src="))
    }

    @Test
    fun `preview url percent encodes the source file url`() {
        // 单元测试跑在 mockable android.jar 上（unitTests.isReturnDefaultValues = true），
        // android.net.Uri.encode 返回 null（或直接抛 not-mocked），编码断言只在真机上生效；
        // 地址组装的断言不依赖 Uri，因此始终有效。
        assumeTrue(
            "android.net.Uri is stubbed on the JVM unit-test runner",
            runCatching { Uri.encode("a") }.getOrNull() != "a",
        )
        val previewUrl = documentPreviewUrl(backend, "$fileUrl?name=a b.md")
        assertFalse("raw space must not reach the preview page url", previewUrl.contains(" "))
        assertTrue(previewUrl.contains("%3F"))
        assertTrue(previewUrl.contains("%20"))
    }

    @Test
    fun `relative download path is joined onto the backend root`() {
        assertEquals(fileUrl, resolveDocumentUrl(backend, "/api/documents/5/download"))
        assertEquals(fileUrl, resolveDocumentUrl(backend, "api/documents/5/download"))
        assertEquals(fileUrl, resolveDocumentUrl("$backend/", "api/documents/5/download"))
    }

    @Test
    fun `same origin absolute url passes through`() {
        assertEquals(fileUrl, resolveDocumentUrl(backend, fileUrl))
        assertEquals(fileUrl, resolveDocumentUrl(backend, "  $fileUrl  "))
    }

    @Test
    fun `cross origin absolute url is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            resolveDocumentUrl(backend, "https://evil.example.com/api/documents/5/download")
        }
    }

    @Test
    fun `host prefix lookalikes are rejected instead of passing a prefix allowlist`() {
        // 旧实现是 startsWith(backendUrl) 白名单：下面前两条都以 backend 开头，会被放行
        // （第二条 java.net.URL 解析出的 host 是 evil.example.com，token 会外发）；
        // 第三条是绝对跨源地址，只是 query 里带了后端地址，同样必须拒。
        assertThrows(IllegalArgumentException::class.java) {
            resolveDocumentUrl(backend, "$backend.evil.example.com:18090/api/documents/5/download")
        }
        assertThrows(IllegalArgumentException::class.java) {
            resolveDocumentUrl(backend, "$backend:18090@evil.example.com/api/documents/5/download")
        }
        assertThrows(IllegalArgumentException::class.java) {
            resolveDocumentUrl(backend, "http://evil.example.com/?next=$fileUrl")
        }
    }

    @Test
    fun `different scheme or port is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            resolveDocumentUrl(backend, "https://192.0.2.150:18090/api/documents/5/download")
        }
        assertThrows(IllegalArgumentException::class.java) {
            resolveDocumentUrl(backend, "http://192.0.2.150:18091/api/documents/5/download")
        }
    }

    @Test
    fun `same origin means identical scheme host and port`() {
        assertTrue(isSameOrigin(backend, backend))
        assertTrue(isSameOrigin(backend, "$backend/doc/preview.html?src=x"))
        assertTrue(isSameOrigin("https://192.0.2.150:443/x", "https://192.0.2.150:443/y"))
        assertFalse(isSameOrigin(backend, "https://192.0.2.150:18090"))
        assertFalse(isSameOrigin(backend, "http://192.0.2.151:18090"))
        assertFalse(isSameOrigin(backend, "http://192.0.2.150:18091"))
        // 三段严格相等：省略默认端口与显式写默认端口不等价，URL 归一化不做额外处理。
        assertFalse(isSameOrigin("https://example.com", "https://example.com:443"))
    }

    @Test
    fun `unparsable or blank urls are never same origin`() {
        assertFalse(isSameOrigin("", backend))
        assertFalse(isSameOrigin(backend, ""))
        assertFalse(isSameOrigin("", ""))
        assertFalse(isSameOrigin("not a url", backend))
        assertFalse(isSameOrigin("ftp://192.0.2.150:18090", backend))
    }
}