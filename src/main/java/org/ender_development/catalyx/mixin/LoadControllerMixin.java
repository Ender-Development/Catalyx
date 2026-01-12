package org.ender_development.catalyx.mixin;

import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import org.ender_development.catalyx.core.module.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LoadController.class, remap = false)
public class LoadControllerMixin {
	@Inject(method = "sendEventToModContainer", at = @At("TAIL"))
	public void sendEventToModContainer(FMLEvent stateEvent, ModContainer mc, CallbackInfo ci) {
		if(stateEvent instanceof FMLStateEvent)
			ModuleManager.INSTANCE.stateEvent$catalyx(mc, (FMLStateEvent) stateEvent);
	}
}
