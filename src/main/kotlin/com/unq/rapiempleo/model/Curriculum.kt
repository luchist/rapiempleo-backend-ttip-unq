package com.unq.rapiempleo.model

import jakarta.persistence.Embeddable

@Embeddable
class Curriculum (
    var nombre : String,
    var dni : String
) {
}