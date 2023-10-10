package com.ucb.edu.abc.mscompany.api

import com.ucb.edu.abc.mscompany.bl.AccountBl
import com.ucb.edu.abc.mscompany.dto.request.AccountablePlanDto
import com.ucb.edu.abc.mscompany.dto.request.SetNewAccountPlan
import com.ucb.edu.abc.mscompany.dto.response.ResponseDto
import com.ucb.edu.abc.mscompany.entity.AccountEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/config/enterprise/account")
class AccountPlanApi @Autowired constructor(
    private val accountBl: AccountBl
){

    @GetMapping("/{companyId}")
    fun getAccountPlan(@PathVariable companyId: Int): ResponseDto<List<AccountablePlanDto>> {
        println("DTO recibido: $companyId")
        val accountPlan = accountBl.getAccountPlan(companyId)
        return ResponseDto(accountPlan, "Request exitoso", true, "")
    }

    @PostMapping("/{companyId}")
    fun setNewAccountPlan(@PathVariable companyId: Int, @RequestBody setNewAccountPlan: SetNewAccountPlan): ResponseDto<List<AccountablePlanDto>> {
        val accountPlan = accountBl.setNewAccountPlan(setNewAccountPlan, companyId)
        return ResponseDto(accountPlan, "Request exitoso", true, "")
    }
}