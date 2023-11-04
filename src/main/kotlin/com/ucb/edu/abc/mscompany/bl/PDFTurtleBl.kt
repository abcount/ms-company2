package com.ucb.edu.abc.mscompany.bl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ucb.edu.abc.mscompany.dto.request.MarginDto
import com.ucb.edu.abc.mscompany.dto.request.OptionDto
import com.ucb.edu.abc.mscompany.dto.request.PDFRequestDto
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset


@Service
class PDFTurtleBl @Autowired constructor(

) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val uri = "http://104.248.232.102:8000/api/pdf/from/html-template/render"

    suspend fun getPDF(footer: String, header: String, body: String, model: Any): ByteArray? {
        logger.info("getPDF")
        var pdfRequestDto = PDFRequestDto()
        pdfRequestDto.footerHtmlTemplate = footer
        pdfRequestDto.headerHtmlTemplate = header
        pdfRequestDto.htmlTemplate = body
        pdfRequestDto.model = model
        pdfRequestDto.options = OptionDto(
            false,
            "Letter",
            MarginDto(25, 25, 25, 25)
        )
        pdfRequestDto.templateEngine = "golang"
        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json")
        val entity = HttpEntity(pdfRequestDto, headers)
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        val response: Deferred<ResponseEntity<ByteArray>> = coroutineScope.async {
            val restTemplate = RestTemplate()
            restTemplate.exchange(
                uri,
                HttpMethod.POST,
                entity,
                ByteArray::class.java
            )
        }
        if(response.await().statusCodeValue != 200) throw Exception("Error al generar el PDF")

        return response.await().body
    }

    fun readHtmlToString(resource: String): String {
        val resource = ClassPathResource(resource)
        return resource.inputStream.bufferedReader(Charset.defaultCharset()).use { it.readText() }
    }

    fun readJson(json: String): Map<String, Any> {
        val objectMapper = jacksonObjectMapper()
        val resource = ClassPathResource(json)
        val jsonString = resource.inputStream.bufferedReader(Charset.defaultCharset()).use { it.readText() }
        return objectMapper.readValue(jsonString, Map::class.java) as Map<String, Any>
    }

    fun byteArrayToFile(byteArray: ByteArray, filePath: String) {
        val file = File(filePath)
        val fos = FileOutputStream(file)
        fos.write(byteArray)
        fos.close()
    }


}