package ccetl.customcapes.client;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class createPlayerEntry {

    public static final createPlayerEntry INSTANCE = new createPlayerEntry();
    private static final Logger LOGGER = LogUtils.getLogger();

    public void createAPlayerEntry(String playerName, String hash) {

        if (util.INSTANCE.isDebugMode()) {
            LOGGER.info("create A Player Entry for " + playerName);
        }

        Path hashPath = Path.of(util.INSTANCE.getRawPathHash() + "\\" + playerName + ".file");
        Path namePath = Path.of(util.INSTANCE.getRawPathNames() + "\\player.file");
        try {
            Files.createFile(hashPath);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
        try {
            Files.createFile(namePath);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
        try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.valueOf(hashPath)), StandardCharsets.UTF_8))) {
            w.write(hash);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
        try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.valueOf(namePath)), StandardCharsets.UTF_8))) {
            w.write("##" + playerName + "##");
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public String readPlayerHash(String playerName) {

        if (util.INSTANCE.isDebugMode()) {
            LOGGER.info("started reading the hash from " + playerName);
        }

        String hash = null;
        if (playerName != null) {
            try {
                FileInputStream is = new FileInputStream(util.INSTANCE.getRawPathHash() + "\\" + playerName + ".file");
                Scanner myReader = new Scanner(is);
                while (myReader.hasNextLine()) {
                    hash = myReader.nextLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hash;
        } else {
            return null;
        }
    }

    public void getPlayerNames() {

        if (util.INSTANCE.isDebugMode()) {
            LOGGER.info("started to get the player names");
        }

        String NamesInOneString = null;
        try {
            FileInputStream is = new FileInputStream(util.INSTANCE.getRawPathNames() + "\\player.file" );
            Scanner myReader = new Scanner(is);
            while (myReader.hasNextLine()) {
                NamesInOneString = myReader.nextLine();
            }
            myReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(NamesInOneString != null) {
            String[] s = NamesInOneString.split("##");
            final List<String> playerNames = new ArrayList<>(Arrays.asList(s));


            for (String playerName : playerNames) {
                util.INSTANCE.addToStartUpNames(playerName);
            }
        }
    }

}