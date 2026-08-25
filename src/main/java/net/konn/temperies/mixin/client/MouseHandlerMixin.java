package net.konn.temperies.mixin.client;

import net.konn.temperies.client.LoomTabClientHandler;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(
            method = "grabMouse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void temperies$preventMouseGrabDuringLoomTabSwitch(
            CallbackInfo ci
    ) {
        if (LoomTabClientHandler.consumeSeamlessTabSwitch()) {
            ci.cancel();
        }
    }
}
