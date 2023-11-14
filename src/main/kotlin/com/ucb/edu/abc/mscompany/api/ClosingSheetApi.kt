package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.ClosingSheetBl
import com.ucb.edu.abc.mscompany.bl.TransactionBl
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/closing-sheet")
class ClosingSheetApi @Autowired constructor(
        private val closingSheetBl: ClosingSheetBl,
        private val transactionBl: TransactionBl
) {
    @PostMapping("/company/{companyId}/user/{userId}")
    fun closeTransactions(@PathVariable companyId: Int,
                          @PathVariable userId: Int) : ResponseDto<String>{
        try {
            closingSheetBl.closeTransactions(companyId,userId)
            return ResponseDto("","Cierre de contabilidad exitoso",true,"")

        }catch (e: Exception){
            return ResponseDto("", "Error al cerrar la contabilidad", false, e.message)
        }
    }

    @GetMapping("/closingDate/{companyId}")
    fun getlastClosingDate(@PathVariable companyId: Int) : ResponseDto<Date>{
            return ResponseDto(transactionBl.getLastClosingSheet(companyId),"Datos obtenidos con exito", true, "")
    }
}