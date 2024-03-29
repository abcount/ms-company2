package com.ucb.edu.abc.mscompany.dto.request
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Date


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionDto(
        var subsidiaryId: Int,
        var currencyId: String,
        var transactionTypeId: Int,
        var areaId: Int,
        var date: String,
        var ajuste: Boolean,
        var glosaGeneral: String,
        var transactions: List<TransactionAccountDto>,
        var totalDebit: BigDecimal,
        var totalCredit: BigDecimal

)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionAccountDto (
        var accountId: Int,
        var entityId: Int?,
        var auxiliaryId: Int?,
        var amountDebit: BigDecimal,
        var amountCredit: BigDecimal,
        var glosaDetail: String,
        var documentCode: String?,

        )