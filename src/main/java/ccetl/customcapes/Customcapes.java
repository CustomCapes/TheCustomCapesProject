package ccetl.customcapes;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(Customcapes.MODID)
public class Customcapes {

    public static final Customcapes INSTANCE = new Customcapes();
    public static final String MODID = "customcapes";
    private static final Logger LOGGER = LogUtils.getLogger();

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
            util.INSTANCE.createFolders();

        /*
        *    createPlayerEntry.INSTANCE.getPlayerNames();
            for (String name : util.INSTANCE.getStartUpNames()) {
                if(name != null) {
                    String oldHash = createPlayerEntry.INSTANCE.readPlayerHash(name);
                    String newHash = util.INSTANCE.getHash(name);
                    boolean same = util.INSTANCE.isSame(oldHash, newHash);
                    if (same) {
                        util.INSTANCE.addToNamesOfPlayersWithSavedCape(name);
                    } else {
                        util.INSTANCE.deletePlayerCapeImage(name);
                        util.INSTANCE.getCape(name);
                    }
                }
            }
        */

        }
    }

}
