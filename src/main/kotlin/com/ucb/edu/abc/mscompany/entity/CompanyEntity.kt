package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import java.time.LocalDate

@Data
@NoArgsConstructor
@AllArgsConstructor
data class CompanyEntity (
        var companyId: Int,
        var companyName: String,
        var diccCategory: String,
        var nit: String,
        var address: String,
        var logoUuid: ByteArray?,
        var openingDate: LocalDate,
        var emailRepresentative: String,
        var numberRepresentative: String,
        var legalRepresentative: String,
        var ciRepresentative: String,
        var numberRegistration: String,
        var numberEmployee: String,
        var rubro: String,
        var status: Boolean

){
    constructor(): this(0,"","","","",null, LocalDate.now(),"","","","","","","", true)

    override fun toString(): String {
        return "CompanyEntity(companyId=$companyId, companyName='$companyName', diccCategory='$diccCategory', nit='$nit', " +
                "address='$address', logoUuid=${logoUuid?.contentToString()}, openingDate=$openingDate, emailRepresentative='$emailRepresentative', " +
                "numberRepresentative='$numberRepresentative', legalRepresentative='$legalRepresentative', ciRepresentative='$ciRepresentative'," +
                " numberRegistration='$numberRegistration', numberEmployee='$numberEmployee', rubro='$rubro', status=$status)"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompanyEntity

        if (logoUuid != null) {
            if (other.logoUuid == null) return false
            if (!logoUuid.contentEquals(other.logoUuid)) return false
        } else if (other.logoUuid != null) return false

        return true
    }

    override fun hashCode(): Int {
        return logoUuid?.contentHashCode() ?: 0
    }
}