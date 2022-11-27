/*
 *  This file is part of the CustomCapes Project (https://github.com/CustomCapes)
 *  Copyright (C) 2022  ccetl
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ccetl.customcapes;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
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

    private String runLocation = Paths.get(".").toAbsolutePath().normalize().toString().toLowerCase(Locale.ENGLISH).replace(" ", "-");
    private String rawPathCache = Paths.get(".").toAbsolutePath().normalize() + "\\CustomCapes\\cache";
    private String rawPathHash = Paths.get(".").toAbsolutePath().normalize() + "\\CustomCapes\\hash";
    private String rawPathNames = Paths.get(".").toAbsolutePath().normalize() + "\\CustomCapes\\names";

    private boolean debugMode = false;

    private boolean apiOnline = false;
    private boolean connection = false;

    private final List<String> playersWithSavedResourceLocation = new ArrayList<>();
    private final List<String> startUpNames = new ArrayList<>();
    private final List<String> namesOfPlayersWithSavedCape = new ArrayList<>();
    private final List<String> namesOfPlayersWhoDoNotHaveACape = new ArrayList<>();
    private final List<String> namesOfPlayersWhoDoHaveACape = new ArrayList<>();

    public void addToPlayersWithSavedResourceLocation(String playerName) {
        playersWithSavedResourceLocation.add(playerName);
    }

    public List<String> getPlayersWithSavedResourceLocation() {
        return playersWithSavedResourceLocation;
    }

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

    public String uid() {
        double r1 = Math.random();
        double r2 = Math.random();
        double r3 = Math.random();
        double r4 = Math.random();
        double r5 = Math.random();
        double r6 = Math.random();
        double r7 = Math.random();
        double r8 = Math.random();
        double r9 = Math.random();
        double r10 = Math.random();
        return String.valueOf(r1) + r2 + r3 + r4 + r5 + r6 + r7 + r8 + r9 + r10;
    }

    public String getShortUID() {
        double r1 = Math.random();
        return String.valueOf(r1);
    }

    public void deletePlayerCapeImage(String playerName) {
        if (util.INSTANCE.isDebugMode()) {
            LOGGER.info("started to delete the cape of " + playerName);
        }
        if (playerName != null) {
            try {
                String path = runLocation.toLowerCase(Locale.ROOT) + "\\customcapes\\cache\\" + playerName.toLowerCase(Locale.ENGLISH) + ".png";
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getHash(String playerName) {
        if (util.INSTANCE.isDebugMode()) {
            LOGGER.info("started to get the hash from the web of " + playerName);
        }
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
        if (Object1 != null && Object2 != null) {
            return Object1.equals(Object2);
        } else {
            return false;
        }
    }

    public void getCape(String playerName) {
        if (util.INSTANCE.isDebugMode()) {
            LOGGER.info("started to get the cape of " + playerName);
        }
        if (playerName != null) {
            String path = getRunLocation().toLowerCase(Locale.ENGLISH) + "\\customcapes\\cache\\" + playerName.toLowerCase(Locale.ENGLISH) + ".png";

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

    public ResourceLocation getResourceLocation(String playerName) {
        String path = runLocation.toLowerCase(Locale.ENGLISH) + "\\customcapes\\cache\\" + playerName.toLowerCase(Locale.ENGLISH) + ".png";
        InputStream is;
        NativeImage ni = null;
        try {
            is = new FileInputStream(path);
            ni = NativeImage.read(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert ni != null;
        util.INSTANCE.addToPlayersWithSavedResourceLocation(playerName);
        return Minecraft.getInstance().getTextureManager().register(playerName.toLowerCase(Locale.ENGLISH) + util.INSTANCE.getShortUID().replace(".", "").toLowerCase(Locale.ENGLISH).replace("-", "") , new DynamicTexture(ni));
    }

    public boolean hasCape(String playerName) {
        URL hasCapeURL = null;
        if (util.INSTANCE.isDebugMode()) {
            LOGGER.info("started checking if " + playerName + " has a cape");
        }
        try {
            hasCapeURL = new URL("https://customcapes.org/api/hascape/" + playerName);
        } catch (MalformedURLException e) {
            LOGGER.warn("Unable to set the url to api/ hasCape");
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        }
        try {
            assert hasCapeURL != null;
            URLConnection urlconnection = hasCapeURL.openConnection();
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            String hasCapeString = sb.toString();
            br.close();

            if (util.INSTANCE.isDebugMode()) {
                LOGGER.warn("requested has cape for " + playerName);
            }
            if (hasCapeString.equals("true")) {
                util.INSTANCE.addToNamesOfPlayersWhoDoHaveACape(playerName);
                return true;
            } else if (hasCapeString.equals("false")) {
                util.INSTANCE.addToNamesOfPlayersWhoDoNotHaveACape(playerName);
                return false;
            } else {
                LOGGER.warn("Something went wrong while reading " + hasCapeURL);
                util.INSTANCE.addToNamesOfPlayersWhoDoNotHaveACape(playerName);
            }

        } catch (IOException e) {
            LOGGER.warn("Unable to check if " + playerName + " has a cape");
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
