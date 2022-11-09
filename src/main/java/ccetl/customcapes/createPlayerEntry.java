package ccetl.customcapes;

import com.mojang.logging.LogUtils;
import org.apache.logging.log4j.core.util.IOUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class createPlayerEntry {

    public static final createPlayerEntry INSTANCE = new createPlayerEntry();
    private static final Logger LOGGER = LogUtils.getLogger();

    public void createAPlayerEntry(String playerName, String hash) {
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
        String hash = null;
        try {
            FileInputStream is = new FileInputStream(util.INSTANCE.getRawPathHash() + "\\" + playerName + ".file" );
            hash = is.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public void getPlayerNames() {
        String NamesInOneString = null;
        try {
            FileInputStream is = new FileInputStream(util.INSTANCE.getRawPathNames() + "\\player.file" );
            NamesInOneString = is.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert NamesInOneString != null;
        String [] s = NamesInOneString.split("##");
        final List<String> playerNames = new ArrayList<>(Arrays.asList(s));

        for (String playerName : playerNames) {
            util.INSTANCE.addToStartUpNames(playerName);
        }
    }

}
