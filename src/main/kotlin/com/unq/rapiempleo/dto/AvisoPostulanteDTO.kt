package com.unq.rapiempleo.dto

import com.unq.rapiempleo.model.EstadoCvPostulado

class AvisoPostulanteDTO (
    var id_postulante : Long,
    var id_oferta : Long,
    var tipo_aviso : EstadoCvPostulado,
){
}