package ccetl.customcapes;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Mod(
        modid = Customcapes.MOD_ID,
        name = Customcapes.MOD_NAME,
        version = Customcapes.VERSION
)
public class Customcapes {

    public static final String MOD_ID = "customcapes";
    public static final String MOD_NAME = "CustomCapes";
    public static final String VERSION = "1.0-SNAPSHOT";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static Customcapes INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        util.INSTANCE.checkApiStatus();
        util.INSTANCE.checkConnection();

        String rawPath = Paths.get(".").toAbsolutePath().normalize() + "\\CustomCapes\\cache";
        try {
            FileUtils.deleteDirectory(new File(rawPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(new File(rawPath).mkdirs()) {
            LOGGER.info("Created new cache folder");
        } else {
            LOGGER.info("Cache folder already exists or creation failed");
        }
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

}