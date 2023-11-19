package com.ucb.edu.abc.mscompany.config

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

@Service
class FormatDataClass {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getNumber(number: BigDecimal): String{
        logger.info("Formateando numero")
        val format = NumberFormat.getNumberInstance(Locale("en", "EN"))
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        return format.format(number)
    }
}