package com.unq.rapiempleo.model

import jakarta.persistence.Embeddable

@Embeddable
class CvSummary (
    val idPostulante : Long,
    val cvPath : String,
)
