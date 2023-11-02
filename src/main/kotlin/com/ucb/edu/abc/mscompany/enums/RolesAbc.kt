package com.ucb.edu.abc.mscompany.enums

enum class RolesAbc(
    val descriptionDb: String,
) {
    CAN_ADD_VOUCHER("Agregar comprobantes"),
    CAN_EDIT_VOUCHER("Editar comprobantes"),

    CAN_GENERATE_REPORT_LIBRO_DIARIO("Generar reportes de libro diario"),
    CAN_GENERATE_REPORT_LIBRO_MAYOR("Generar reportes de libro mayor"),
    CAN_GENERATE_REPORT_BALANCE_GENERAL("Generar reportes de balance general"),
    CAN_GENERATE_REPORT_ESTADO_RESULTADO("Generar reportes de estado de resultados"),


    CAN_ADD_EXCHANGE("Agregar tasa de cambio"),

    CAN_INVITE_PEOPLE("Invitar nuevos usuarios"),
    CAN_DELETE_PEOPLE("Eliminar usuarios"),

    CAN_EDIT_PERMISSIONS("Modificar permisos de usuarios"),
    CAN_ACCESS_CONFIGURATION("Acceder a la configuración de la empresa"),

    CAN_CLOSE_ANY("Puede cerrar un documento contablemente"),
    CAN_REOPEN_ANY("Puede reabri un documento contable"),




}

