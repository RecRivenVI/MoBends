package goblinbob.mobends.core.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SerialHelper {
    public static String readChar(DataInputStream stream, int length) throws IOException {
        byte[] buffer = new byte[length];
        stream.readFully(buffer);
        return new String(buffer, StandardCharsets.UTF_8);
    }
}
