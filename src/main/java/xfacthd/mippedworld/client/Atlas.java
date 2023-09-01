package xfacthd.mippedworld.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import xfacthd.mippedworld.client.mixin.AccessorTextureAtlas;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public enum Atlas
{
    BLOCK(TextureAtlas.LOCATION_BLOCKS),
    BEDS(Sheets.BED_SHEET),
    SHULKER_BOXES(Sheets.SHULKER_SHEET),
    SIGNS(Sheets.SIGN_SHEET),
    CHESTS(Sheets.CHEST_SHEET),
    BANNERS(Sheets.BANNER_SHEET),
    SHIELDS(Sheets.SHIELD_SHEET),
    // Pots don't have a dedicated "fixed" buffer, so they fall back to immediate upload, preventing the mip level adjustment
    //POTS(Sheets.DECORATED_POT_SHEET),
    ;

    private static final Atlas[] ATLASES = values();
    private static final Map<ResourceLocation, Atlas> BY_NAME = Arrays.stream(ATLASES)
            .collect(Collectors.toUnmodifiableMap(a -> a.atlasLocation, Function.identity()));
    private final ResourceLocation atlasLocation;
    private boolean active = true;
    private int id = -1;
    private int maxMipLevel = 0;

    Atlas(ResourceLocation location)
    {
        this.atlasLocation = location;
    }

    public boolean isActive()
    {
        return active;
    }

    public int getId()
    {
        return id;
    }

    public int getMaxMipLevel()
    {
        return maxMipLevel;
    }



    public static void updateAtlasReference(TextureAtlas textureAtlas)
    {
        Atlas atlas = BY_NAME.get(textureAtlas.location());
        if (atlas != null)
        {
            atlas.id = textureAtlas.getId();
            atlas.maxMipLevel = ((AccessorTextureAtlas) textureAtlas).mippedworld$getMipLevel();
        }
    }

    // TODO: call from config (re)load handler
    public static void updateActiveStatus()
    {
        for (Atlas atlas : ATLASES)
        {
            atlas.active = true;
        }
    }
}
