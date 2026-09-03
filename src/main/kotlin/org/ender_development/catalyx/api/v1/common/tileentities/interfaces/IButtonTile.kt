package org.ender_development.catalyx.api.v1.common.tileentities.interfaces

import org.ender_development.catalyx.core.client.button.AbstractButtonWrapper

interface IButtonTile {
	fun handleButtonPress(button: AbstractButtonWrapper)
}
