package com.ucb.edu.abc.mscompany.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.web.multipart.MultipartFile



@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class EnterpriseDto(
        var companyName: String,
        var diccCategory: String,
        var nit: String,
        var address: String,
        var logoUuid: ByteArray?,
        var contactEmail: String,
        var contactName: String,
){
}

