package ccetl.customcapes.client;

import net.fabricmc.api.ClientModInitializer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CustomCapesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        util.INSTANCE.checkApiStatus();
        util.INSTANCE.checkConnection();
        util.INSTANCE.createFolders();
    }

}
