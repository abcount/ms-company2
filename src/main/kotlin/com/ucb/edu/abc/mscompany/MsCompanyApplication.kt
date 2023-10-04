package com.ucb.edu.abc.mscompany

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class MsCompanyApplication

fun main(args: Array<String>) {
	runApplication<MsCompanyApplication>(*args)
}
