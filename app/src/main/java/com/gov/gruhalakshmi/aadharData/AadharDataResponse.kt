package com.gov.gruhalakshmi.aadharData

data class AadharDataResponse(
    val FamilyHead: List<FamilyHead>,
    val Male: List<Male>,
    val RCNo: String
)