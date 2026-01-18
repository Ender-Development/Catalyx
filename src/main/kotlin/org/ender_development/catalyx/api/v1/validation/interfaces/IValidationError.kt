package org.ender_development.catalyx.api.v1.validation.interfaces

import org.ender_development.catalyx.api.v1.common.Severity

interface IValidationError {
	val field: String?
	val message: String
	val code: String?
	val severity: Severity
}
