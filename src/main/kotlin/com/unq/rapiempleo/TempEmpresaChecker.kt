package com.unq.rapiempleo

import com.unq.rapiempleo.dto.EmpresaCheckDTO
import com.unq.rapiempleo.exceptions.InvalidCompanyDataException
import org.springframework.context.annotation.Configuration

@Configuration
class TempEmpresaChecker {


    fun checkEmpresaRequestVerify(empresaData : EmpresaCheckDTO) {
        val searchResult = allCompaniesRegistries.containsKey(empresaData.cuit)
        if (!searchResult) {
            throw InvalidCompanyDataException()
        }
        val checkName = allCompaniesRegistries[empresaData.cuit]!!.contains(empresaData.company, ignoreCase = true)
        if (!checkName) {
            throw InvalidCompanyDataException()
        }


    }

    val allCompaniesRegistries = mapOf(
        "30715027654" to "SEMBRAR IDEAS S.A. ",
        "30708723017" to "SUR AGROPECUARIA S.A.",
        "30717946487" to "COOPERATIVA DE TRABAJO LUPA LIMITADA ",
        "30640328505" to "CAÑADA COLORADA S.R.L.",
        "30718321669" to "FRUTA LINK S.A.",
        "30715481851 " to "SOLVIE SA",
        "30516645543" to "ELEPRINT SA",
        "30628406177" to "TECMACO INTEGRAL SA",
        "30506919009" to "AXION ENERGY ARGENTINA SA",
        "30502793175" to "ARCOR SA",
        "30503508725" to "MONSANTO ARGENTINA SRL",
        "30518408689" to "SALTA REFRESCOS SA",
        "30708895780" to "BAGLEY ARGENTINA SA",
        "30501036672" to "LA PAPELERA DEL PLATA SA",
        "30589123286" to "TETRA PAK S.R.L.",
        "30709590894" to "RENOVA SA",
        "30584811982" to "RADIO VICTORIA FUEGUINA SOCIEDAD ANONIMA",
        "30708668733" to "REFRES NOW SA.",
        "30505817423" to "INDUSTRIA DEL PLASTICO Y METALURGICA ALBANO COZZUOL SA",
        "30708602309" to "PC ARTS ARGENTINA SA",
        "30710853688" to "GLUCOVIL ARGENTINA SA.",
        "30686263238" to "BIOMAS SA",
        "30655275998" to "PAPELERA SAMSENG SA ",
        "30621181838" to "DISAL SA ",
        "30516877223" to "LILIANA SRL",
        "30682434585" to "DISTROCUYO SA",
    )
}