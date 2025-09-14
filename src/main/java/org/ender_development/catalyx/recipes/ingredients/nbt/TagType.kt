package org.ender_development.catalyx.recipes.ingredients.nbt

enum class TagType(val typeId: Int) {
	BOOLEAN(1),
	BYTE(1),
	SHORT(2),
	INT(3),
	LONG(4),
	FLOAT(5),
	DOUBLE(6),
	BYTE_ARRAY(7),
	STRING(8),
	LIST(9),
	COMPOUND(10),
	INT_ARRAY(11),
	LONG_ARRAY(12),
	NUMBER(99);

	companion object {
		fun isNumeric(tagType: TagType) =
			when(tagType) {
				BOOLEAN, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, NUMBER -> true
				else -> false
			}
	}
}
