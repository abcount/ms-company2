package com.ucb.edu.abc.mscompany.config

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class FormatDataClass {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getNumber(number: BigDecimal): String{
        logger.info("Formateando numero")
        if (number.compareTo(BigDecimal.ZERO) == 0) {
            return " - "
        }
        val format = NumberFormat.getNumberInstance(Locale("en", "EN"))
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        return format.format(number)
    }

    fun getDateFromLocalDateTime(date: LocalDateTime): String{
        logger.info("Formateando fecha")
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(formatter)
    }

    fun getHourFromLocalDateTime(date: LocalDateTime): String{
        logger.info("Formateando hora")
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return date.format(formatter)
    }

    fun convertDateToString(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(date)
    }

    fun convertLocalDateTimeToString(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return localDateTime.format(formatter)
    }
}