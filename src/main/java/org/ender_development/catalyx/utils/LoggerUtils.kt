package org.ender_development.catalyx.utils

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.ender_development.catalyx.Reference

object LoggerUtils {
	internal val logger = LogManager.getLogger(Reference.MOD_NAME)

	fun new(name: String): Logger =
		LogManager.getLogger("${Reference.MOD_NAME}-$name")
}
