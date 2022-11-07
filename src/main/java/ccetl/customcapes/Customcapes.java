package ccetl.customcapes;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;

@Mod(Customcapes.MODID)
public class Customcapes {

    public static final Customcapes INSTANCE = new Customcapes();
    public static final String MODID = "customcapes";
    private static final Logger LOGGER = LogUtils.getLogger();
    private final boolean connection = util.INSTANCE.isConnection();
    private final boolean apiOnline = util.INSTANCE.isApiOnline();

    public Customcapes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modLoading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

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
    }

}
