package ccetl.customcapes;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class util {

    public static final util INSTANCE = new util();
    private static final Logger LOGGER = LogUtils.getLogger();

    public boolean debugMode = false;

    public boolean apiOnline = false;
    public boolean connection = false;

    public final List<String> namesOfPlayersWithSavedCape = new ArrayList<>();
    public final List<String> namesOfPlayersWhoDoNotHaveACape = new ArrayList<>();
    public final List<String> namesOfPlayersWhoDoHaveACape = new ArrayList<>();

    public boolean isConnection() {
        return connection;
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public boolean isApiOnline() {
        return apiOnline;
    }

    public void setApiOnline(boolean apiOnline) {
        this.apiOnline = apiOnline;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void addToNamesOfPlayersWithSavedCape(String nameToAdd) {
        namesOfPlayersWithSavedCape.add(nameToAdd);
    }

    public void addToNamesOfPlayersWhoDoNotHaveACape(String nameToAdd) {
        namesOfPlayersWhoDoNotHaveACape.add(nameToAdd);
    }

    public void addToNamesOfPlayersWhoDoHaveACape(String nameToAdd) {
        namesOfPlayersWhoDoHaveACape.add(nameToAdd);
    }

    public List<String> getNamesOfPlayersWhoDoHaveACape() {
        return namesOfPlayersWhoDoHaveACape;
    }

    public List<String> getNamesOfPlayersWhoDoNotHaveACape() {
        return namesOfPlayersWhoDoNotHaveACape;
    }

    public List<String> getNamesOfPlayersWithSavedCape() {
        return namesOfPlayersWithSavedCape;
    }

    public void checkConnection() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection UC = url.openConnection();
            UC.connect();
            UC.getInputStream().close();
            connection = true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            connection = false;
        }
    }

    public void checkApiStatus() {
        try {
           URL url = new URL("https://customcapes.org/api/hascape/test");
           URLConnection urlconnection = url.openConnection();
           BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
           String TRUE;
           while ((TRUE = br.readLine()) != null) {
               if (TRUE.equals("true")) {
                    util.INSTANCE.setApiOnline(true);
                    } else if (TRUE.equals("false")) {
                        util.INSTANCE.setApiOnline(true);
                    } else {
                        LOGGER.warn("Something went wrong while reading " + url);
                        util.INSTANCE.setApiOnline(false);
                    }
               }
                br.close();
            } catch (IOException e) {
                util.INSTANCE.setApiOnline(false);
            }
    }

    public void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
