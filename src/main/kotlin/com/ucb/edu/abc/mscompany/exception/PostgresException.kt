package com.ucb.edu.abc.mscompany.exception

class PostgresException(message: String, val error: String): Exception(message) {
}