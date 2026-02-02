package org.ender_development.catalyx.api.v1.client

import org.ender_development.catalyx.api.v1.client.interfaces.IAreaHighlighter
import org.ender_development.catalyx.core.client.AreaHighlighter

object Client {
	fun newAreaHighlighter(): IAreaHighlighter =
		AreaHighlighter()
}
