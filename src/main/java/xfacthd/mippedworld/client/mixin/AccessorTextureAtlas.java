package xfacthd.mippedworld.client.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlas.class)
public interface AccessorTextureAtlas
{
    @Accessor("mipLevel")
    int mippedworld$getMipLevel();
}
