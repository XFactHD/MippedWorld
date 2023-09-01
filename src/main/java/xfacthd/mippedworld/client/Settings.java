package xfacthd.mippedworld.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLPaths;
import xfacthd.mippedworld.MippedWorld;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public final class Settings
{
    private static final String FILE_NAME = "mippedworld.dat";

    private static boolean changed = false;
    private static boolean active = false;
    private static int mipLevel = 2;
    private static int maxMipLevel = 4;

    public static boolean isActive()
    {
        return active;
    }

    public static int getMipLevel()
    {
        return mipLevel;
    }

    public static boolean toggle()
    {
        active = !active;
        changed = true;
        return active;
    }

    public static int increment()
    {
        if (mipLevel < maxMipLevel)
        {
            mipLevel++;
            changed = true;
        }
        return mipLevel;
    }

    public static int decrement()
    {
        if (mipLevel > 1)
        {
            mipLevel--;
            changed = true;
        }
        return mipLevel;
    }

    public static int getMaxMipLevel()
    {
        return maxMipLevel;
    }

    public static void onMipLevelOptionChanged(int newLevel)
    {
        maxMipLevel = newLevel;
    }

    public static void load()
    {
        Path path = FMLPaths.GAMEDIR.get().resolve(FILE_NAME);
        if (!Files.exists(path))
        {
            changed = true;
            return;
        }

        try
        {
            List<String> data = Files.readAllLines(path);
            Boolean active = null;
            int level = -1;
            for (String line : data)
            {
                if (line.startsWith("active:"))
                {
                    active = Boolean.parseBoolean(line.split(":")[1]);
                }
                else if (line.startsWith("level:"))
                {
                    level = Integer.parseInt(line.split(":")[1]);
                }
            }
            if (active == null || level < 1 || level > 4)
            {
                throw new IOException("Settings file invalid");
            }
            Settings.active = active;
            Settings.mipLevel = level;
            onMipLevelOptionChanged(Minecraft.getInstance().options.mipmapLevels().get());
        }
        catch (IOException | NumberFormatException e)
        {
            MippedWorld.LOGGER.error("Failed to load settings file, falling back to defaults", e);
        }
    }

    public static void save()
    {
        if (changed)
        {
            changed = false;

            Path path = FMLPaths.GAMEDIR.get().resolve(FILE_NAME);
            try
            {
                Files.writeString(
                        path,
                        "active:%s\nlevel:%d".formatted(active ? "true" : "false", mipLevel),
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.CREATE
                );
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private Settings() { }
}
