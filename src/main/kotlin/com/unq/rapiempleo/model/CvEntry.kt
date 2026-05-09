package com.unq.rapiempleo.model

import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
class CvEntry(
    var cvPath: String,
    var uploadedAt: LocalDateTime = LocalDateTime.now()
)
