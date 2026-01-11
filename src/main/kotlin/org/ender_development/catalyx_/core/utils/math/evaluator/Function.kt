package org.ender_development.catalyx_.core.utils.math.evaluator

import java.math.BigDecimal

abstract class Function {
	abstract fun call(arguments: List<BigDecimal>): BigDecimal
}
