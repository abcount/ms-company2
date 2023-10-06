package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.ExchangeDao
import com.ucb.edu.abc.mscompany.entity.ExchangeEntity
import com.ucb.edu.abc.mscompany.exception.PostgresException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExchangeBl @Autowired constructor(
        private val exchangeDao: ExchangeDao
) {

    fun getExchangeByNameOrIso(name: String): List<ExchangeEntity>{
        try{
            return exchangeDao.getExchangeByNameOrIso(name)
        } catch (e: Exception){
            throw PostgresException("Error al obtener el tipo de cambio por nombre o iso: $name", e.message.toString())
        }
    }


}