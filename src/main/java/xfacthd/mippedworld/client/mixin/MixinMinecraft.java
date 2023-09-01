package xfacthd.mippedworld.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.mippedworld.client.Settings;

@Mixin(Minecraft.class)
@SuppressWarnings("MethodMayBeStatic")
public final class MixinMinecraft
{
    @Inject(method = "updateMaxMipLevel", at = @At("HEAD"), require = 1)
    private void mippedworld$onMaxMipLevelChanged(int newLevel, CallbackInfo ci)
    {
        Settings.onMipLevelOptionChanged(newLevel);
    }
}
