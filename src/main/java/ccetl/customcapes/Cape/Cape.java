package ccetl.customcapes.Cape;

import net.minecraftforge.jarjar.metadata.ContainedJarIdentifier;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Cape {
    public static final Cape INSTANCE = new Cape();

    public boolean hasCape(String player) throws IOException {
        ContainedJarIdentifier nameText = null;
        String name = nameText.toString().replace("literal{�7", "").replace("�7}[style={}]", "").replaceAll("[�°^ßöäü{}()&%$§`'#~;]", "");
        String path = Paths.get(".").toAbsolutePath().normalize() + "\\astat\\cache\\" + name + ".png";
        String rawPath = Paths.get(".").toAbsolutePath().normalize() + "\\astat\\cache";
        URL url = new URL("https://customcapes.org/api/capes/" + name + ".png");
        String Astat = "Astat";

        //create the path
        new File(rawPath).mkdirs();

        //safe the image from the api
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(path);
        byte[] b = new byte[2048];
        int length;
        int check = 0;
        while ((length = is.read(b)) != -1) {
            check += 1;
            os.write(b, 0, length);
        }
        is.close();
        os.close();

        //get the file size
        long size = Files.size(Path.of(path));

        //register the location to minecraft
        //final  cape = new Identifier(path);

        if(size > 0.001) {
            return true;
        } else return false;
    }

    public void getCape() {

    }

}
