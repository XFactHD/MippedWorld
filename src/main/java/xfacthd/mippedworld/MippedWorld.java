package xfacthd.mippedworld;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(MippedWorld.MOD_ID)
@SuppressWarnings("UtilityClassWithPublicConstructor")
public final class MippedWorld
{
    public static final String MOD_ID = "mippedworld";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MippedWorld()
    {
        if (FMLEnvironment.dist.isDedicatedServer())
        {
            LOGGER.warn("MippedWorld is a client-only mod!");
        }
    }
}
