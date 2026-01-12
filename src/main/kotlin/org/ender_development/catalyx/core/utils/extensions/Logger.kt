@file:Suppress("NOTHING_TO_INLINE")

package org.ender_development.catalyx.core.utils.extensions

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

inline fun Logger.subLogger(name: String): Logger =
	LogManager.getLogger("${this.name}-$name")
