package com.unq.rapiempleo.model

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class NotificationEntry (
    @Enumerated(EnumType.STRING)
    var typeNotif : EstadoCvPostulado,
    var titleNotif : String
){
}