package com.mytaxi.apis.phraseapi.client

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.Gson
import com.mytaxi.apis.phraseapi.client.model.CreateTranslation
import com.mytaxi.apis.phraseapi.client.model.PhraseLocale
import com.mytaxi.apis.phraseapi.client.model.Translation
import com.mytaxi.apis.phraseapi.client.model.TranslationKey
import feign.Response
import org.apache.commons.httpclient.HttpStatus
import org.junit.Test
import org.mockito.Mockito
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PhraseApiClientTranslationTest {
    private var client: PhraseApi = Mockito.mock(PhraseApi::class.java, Mockito.withSettings().extraInterfaces(CacheApi::class.java))

    private var phraseApiClient: PhraseApiClient

    init {
        phraseApiClient = PhraseApiClientImpl(client)
    }

    @Test
    fun `Should create translation`() {

        //GIVEN
        val projectId = UUID.randomUUID().toString()
        val localeId = UUID.randomUUID().toString()
        val keyId = UUID.randomUUID().toString()
        val keyName = "key.name"
        val translationContent = "translation"

        val headers = mapOf(
            HttpHeaders.CONTENT_TYPE to listOf(MediaType.JSON_UTF_8.toString())
        )

        val createTranslation = CreateTranslation(
            localeId = localeId,
            keyId = keyId,
            content = translationContent
        )

        val translationJSON = Gson().toJson(createTranslation)

        val response = Response.create(
            HttpStatus.SC_CREATED,
            "OK",
            headers,
            translationJSON,
            StandardCharsets.UTF_8
        )

        Mockito.`when`(client.createTranslation(
            projectId = projectId,
            localeId = localeId,
            keyId = keyId,
            content = translationContent
        )).thenReturn(response)

        val expectedTranslation = Translation(
            id = UUID.randomUUID().toString(),
            key = TranslationKey(
                id = keyId,
                name = keyName
            ),
            locale = PhraseLocale(
                id = localeId,
                code = Locale.US.toLanguageTag(),
                name = UUID.randomUUID().toString()
            ),
            content = translationContent
        )

        //WHEN
        val actualResponse = phraseApiClient.createTranslation(projectId, createTranslation)

        //THEN
        assertNotNull(actualResponse)
        assertEquals(actualResponse!!.content, expectedTranslation.content)
    }

}
