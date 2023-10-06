package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.CompanyBl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/image")
class ImageApi @Autowired constructor(
    private val companyBl: CompanyBl
) {
    // http://localhost:8082/image/company/
    @RequestMapping(value = ["/company/{id}"], method = [RequestMethod.GET])
    fun getImageByCompanyId(@PathVariable id: Int): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_JPEG
        val base64 = companyBl.getImageOfCompany(id)
        return ResponseEntity(base64, headers, HttpStatus.OK)
    }
}