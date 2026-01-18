package org.ender_development.catalyx.api.v1.common

import org.apache.logging.log4j.Level

enum class Severity(val loggerLevel: Level) {
    WARNING(Level.WARN),
    ERROR(Level.ERROR),
    CRITICAL(Level.FATAL)
}
