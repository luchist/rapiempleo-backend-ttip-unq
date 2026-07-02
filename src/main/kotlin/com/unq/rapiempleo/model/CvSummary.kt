package com.unq.rapiempleo.model

import jakarta.persistence.Embeddable

@Embeddable
class CvSummary (
    val id_postulante : Long,
    val cvPath : String,
)
