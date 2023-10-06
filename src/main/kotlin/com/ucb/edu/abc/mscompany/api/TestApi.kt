package com.ucb.edu.abc.mscompany.api


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ucb.edu.abc.mscompany.bl.CompanyBl
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource


@RestController
@RequestMapping("/test-api")
class TestApi @Autowired constructor(
    private val companyBl: CompanyBl
) {
    val objectMapper = jacksonObjectMapper()


    @RequestMapping(value = ["/3/{id}"], method = [RequestMethod.GET])
    fun getImage3(@PathVariable id: Int): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG
        val base64 = companyBl.getImageOfCompany(id)
        return ResponseEntity(base64, headers, HttpStatus.OK)
    }

}