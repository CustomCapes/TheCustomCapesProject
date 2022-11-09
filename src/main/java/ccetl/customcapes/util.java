package ccetl.customcapes;

import com.mojang.logging.LogUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class util {

    public static final util INSTANCE = new util();
    private static final Logger LOGGER = LogUtils.getLogger();

    public String runLocation = Paths.get(".").toAbsolutePath().normalize().toString().toLowerCase().replace(" ", "-");
    public String rawPathCache = Paths.get(".").toAbsolutePath().normalize() + "\\CustomCapes\\cache";
    public String rawPathHash = Paths.get(".").toAbsolutePath().normalize() + "\\CustomCapes\\hash";
    public String rawPathNames = Paths.get(".").toAbsolutePath().normalize() + "\\CustomCapes\\names";

    public boolean debugMode = false;

    public boolean apiOnline = false;
    public boolean connection = false;

    public final List<String> startUpNames = new ArrayList<>();
    public final List<String> namesOfPlayersWithSavedCape = new ArrayList<>();
    public final List<String> namesOfPlayersWhoDoNotHaveACape = new ArrayList<>();
    public final List<String> namesOfPlayersWhoDoHaveACape = new ArrayList<>();

    public String getRawPathCache() {
        return rawPathCache;
    }

    public String getRawPathHash() {
        return rawPathHash;
    }

    public String getRawPathNames() {
        return rawPathNames;
    }

    public void addToStartUpNames(String playerName) {
        startUpNames.add(playerName);
    }

    public List<String> getStartUpNames() {
        return startUpNames;
    }

    public String getRunLocation() {
        return runLocation;
    }

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

    public void createFolders() {
        if(new File(rawPathCache).mkdirs()) {
            LOGGER.info("Created new cache folder");
        } else {
            LOGGER.info("Cache folder already exists or creation failed");
        }
        if(new File(rawPathHash).mkdirs()) {
            LOGGER.info("Created new hash folder");
        } else {
            LOGGER.info("hash folder already exists or creation failed");
        }
        if(new File(rawPathNames).mkdirs()) {
            LOGGER.info("Created new names folder");
        } else {
            LOGGER.info("names folder already exists or creation failed");
        }
    }

    public void deletePlayerCapeImage(String playerName) {
        String path = runLocation.toLowerCase(Locale.ROOT) + "\\customcapes\\cache\\" + playerName.toLowerCase(Locale.ROOT) + ".png";
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHash(String playerName) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://customcapes.org/api/hash/CustomCapes" + playerName);
            URLConnection urlconnection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
        return sb.toString();
    }

    public boolean isSame(String Object1, String Object2) {
        if (Object1.equals(Object2)) {
            return true;
        } else {
            return false;
        }
    }

    public void getCape(String playerName) {
        String path = getRunLocation().toLowerCase(Locale.ROOT) + "\\customcapes\\cache\\" + playerName.toLowerCase(Locale.ROOT) + ".png";

        URL url = null;
        try {
            url = new URL("https://customcapes.org/api/capes/" + playerName + ".png");
        } catch (MalformedURLException e) {
            LOGGER.warn("failed to set the url");
            LOGGER.warn(e.getMessage());
        }

        //create the path
        if (new File(getRawPathCache()).mkdirs()) LOGGER.info("Created the cache folder");

        //safe the image from the api
        InputStream is = null;
        try {
            assert url != null;
            is = url.openStream();
        } catch (IOException e) {
            LOGGER.warn("Failed to open the connection to CustomCapes");
            LOGGER.warn(e.getMessage());
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            LOGGER.warn("failed to create the outputStream");
            LOGGER.warn(e.getMessage());
        }
        byte[] b = new byte[2048];
        int length = 0;
        while (true) {
            try {
                assert is != null;
                if ((length = is.read(b)) == -1) break;
            } catch (IOException e) {
                LOGGER.warn(e.getMessage());
            }
            try {
                assert os != null;
                os.write(b, 0, length);
            } catch (IOException e) {
                LOGGER.warn(e.getMessage());
            }
        }
        try {
            is.close();
        } catch (IOException e) {
            LOGGER.warn("failed to end the connection");
            LOGGER.warn(e.getMessage());
        }
        try {
            assert os != null;
            os.close();
        } catch (IOException e) {
            LOGGER.warn("failed to end the download");
            LOGGER.warn(e.getMessage());
        }
        util.INSTANCE.addToNamesOfPlayersWithSavedCape(playerName);
        createPlayerEntry.INSTANCE.createAPlayerEntry(playerName, util.INSTANCE.getHash(playerName));
    }

}
