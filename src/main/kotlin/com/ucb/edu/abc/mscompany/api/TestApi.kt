package com.ucb.edu.abc.mscompany.api


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ucb.edu.abc.mscompany.bl.CompanyBl
import org.apache.tomcat.util.codec.binary.Base64
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

//    @RequestMapping(value = ["/{id}"], method = [RequestMethod.GET], produces = [MediaType.IMAGE_JPEG_VALUE])
//    @Throws(
//        IOException::class
//    )
//    fun getImage(@PathVariable id: Int): ResponseEntity<ByteArray?>? {
//        val imgFile = companyBl.getImageOfCompany(id)
//        //val bytes: ByteArray = StreamUtils.copyToByteArray(imgFile.inputStream)
//        return ResponseEntity
//            .ok()
//            .contentType(MediaType.IMAGE_JPEG)
//            .body(imgFile)
//    }

    @RequestMapping(value = ["/1/{id}"], method = [RequestMethod.GET])
    fun getImage(@PathVariable id: Int): ResponseEntity<ByteArray> {
//        Base64.decode()
//        val decoder = Base64.getDecoder()
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG

        val base64 = companyBl.getImageOfCompany2(id)
        return ResponseEntity(Base64.decodeBase64(base64), headers, HttpStatus.OK)
    }

    @RequestMapping(value = ["/2/{id}"], method = [RequestMethod.GET])
    fun getImage2(@PathVariable id: Int): ResponseEntity<ByteArray> {
//        Base64.decode()
//        val decoder = Base64.getDecoder()
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG

        val base64 = companyBl.getImageOfCompany(id)
        return ResponseEntity(Base64.decodeBase64(base64), headers, HttpStatus.OK)
    }

    @RequestMapping(value = ["/3/{id}"], method = [RequestMethod.GET])
    fun getImage3(@PathVariable id: Int): ResponseEntity<ByteArray> {
//        Base64.decode()
//        val decoder = Base64.getDecoder()


        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG
        val base64 = companyBl.getImageOfCompany(id)
        return ResponseEntity(base64, headers, HttpStatus.OK)
    }

}