package com.ucb.edu.abc.mscompany.config

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class FormatDataClass {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getNumber(number: BigDecimal): String{
        logger.info("Formateando numero")

        val format = NumberFormat.getNumberInstance(Locale("en", "EN"))
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        val formattedNumber = format.format(number.abs()) // Tomar el valor absoluto para quitar el signo
        if (number.signum() == -1){
            return "($formattedNumber)"
        }else{
            return formattedNumber
        }

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

    fun stringToDateAtBeginOfDay(date: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        calendar.time = formatter.parse(date)

        // Establecer la hora al principio del día (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun stringToDateAtEndOfDay(date: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        calendar.time = formatter.parse(date)

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    fun changeFormatStringDate(date: String): String {
        val entryDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fecha = LocalDate.parse(date, entryDate)
        val outDate= DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return fecha.format(outDate)
    }
}