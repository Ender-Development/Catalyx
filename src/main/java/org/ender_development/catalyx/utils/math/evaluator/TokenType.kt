package org.ender_development.catalyx.utils.math.evaluator

internal enum class TokenType {

	// Basic operators
	PLUS,
	MINUS,
	STAR,
	SLASH,
	MODULO,
	EXPONENT,
	SQUARE_ROOT,
	ASSIGN,

	// Logical operators
	EQUAL_EQUAL,
	NOT_EQUAL,
	GREATER,
	GREATER_EQUAL,
	LESS,
	LESS_EQUAL,
	BAR_BAR,
	AMP_AMP,

	// Other
	COMMA,

	// Parentheses
	LEFT_PAREN,
	RIGHT_PAREN,

	// Literals
	NUMBER,
	IDENTIFIER,

	EOF

}
