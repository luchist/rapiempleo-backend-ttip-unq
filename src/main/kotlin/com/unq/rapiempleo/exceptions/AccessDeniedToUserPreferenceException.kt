package com.unq.rapiempleo.exceptions

class AccessDeniedToUserPreferenceException :
    RuntimeException("No tenés permiso de editar las preferencias de este usuario")
