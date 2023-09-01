package xfacthd.mippedworld.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.*;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL13;
import xfacthd.mippedworld.MippedWorld;

@Mod.EventBusSubscriber(modid = MippedWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MWClient
{
    private static final String KEY_CATEGORY = "key.category.mippedworld";
    private static final Lazy<KeyMapping> KEY_TOGGLE_MIP = makeKeyMapping("key.mippedworld.toggle", GLFW.GLFW_KEY_U);
    private static final Lazy<KeyMapping> KEY_INCR_MIP = makeKeyMapping("key.mippedworld.increment", GLFW.GLFW_KEY_I);
    private static final Lazy<KeyMapping> KEY_DECR_MIP = makeKeyMapping("key.mippedworld.decrement", GLFW.GLFW_KEY_O);
    private static final Component MSG_ACTIVATED = Component.translatable("msg.mippedworld.activated");
    private static final Component MSG_DEACTIVATED = Component.translatable("msg.mippedworld.deactivated");

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.addListener(MWClient::onClientTickStart);
        MinecraftForge.EVENT_BUS.addListener(MWClient::onGameShuttingDown);

        Settings.load();
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
    {
        event.register(KEY_TOGGLE_MIP.get());
        event.register(KEY_INCR_MIP.get());
        event.register(KEY_DECR_MIP.get());
    }

    private static Lazy<KeyMapping> makeKeyMapping(String name, int key)
    {
        return Lazy.of(() -> new KeyMapping(name, key, KEY_CATEGORY));
    }

    @SubscribeEvent
    public static void onTextureStitchPost(final TextureStitchEvent.Post event)
    {
        Atlas.updateAtlasReference(event.getAtlas());
    }

    private static void onGameShuttingDown(final GameShuttingDownEvent event)
    {
        Settings.save();
    }

    private static void onClientTickStart(final TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.START || mc.level == null || mc.player == null) return;

        if (KEY_TOGGLE_MIP.get().consumeClick())
        {
            boolean active = Settings.toggle();
            mc.player.displayClientMessage(active ? MSG_ACTIVATED : MSG_DEACTIVATED, true);
        }
        else if (KEY_INCR_MIP.get().consumeClick())
        {
            int mipLevel = Settings.increment();
            mc.player.displayClientMessage(
                    Component.translatable("msg.mippedworld.incremented", mipLevel, Settings.getMaxMipLevel()), true
            );
        }
        else if (KEY_DECR_MIP.get().consumeClick())
        {
            int mipLevel = Settings.decrement();
            mc.player.displayClientMessage(Component.translatable("msg.mippedworld.decremented", mipLevel, 1), true);
        }
    }

    public static void setBlockAtlasMipLevel(boolean set)
    {
        if (!Settings.isActive() || !Atlas.BLOCK.isActive()) return;

        int mipLevel = Settings.getMipLevel();
        if (mipLevel > 0)
        {
            RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            setAtlasMipLevel(Atlas.BLOCK, set ? mipLevel : 0);
        }
    }

    public static void setAtlasMipLevel(boolean set, Atlas[] atlases)
    {
        if (!Settings.isActive()) return;

        int mipLevel = Settings.getMipLevel();
        if (mipLevel <= 0) return;

        if (!set) mipLevel = 0;

        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
        for (Atlas atlas : atlases)
        {
            if (atlas.isActive())
            {
                setAtlasMipLevel(atlas, mipLevel);
            }
        }
    }

    private static void setAtlasMipLevel(Atlas atlas, int level)
    {
        int id = atlas.getId();
        if (id <= 0) return;

        int realLevel = Math.min(level, atlas.getMaxMipLevel());
        if (level == 0 || realLevel > 0)
        {
            RenderSystem.bindTexture(id);
            RenderSystem.texParameter(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_BASE_LEVEL, level);
        }
    }



    private MWClient() { }
}
