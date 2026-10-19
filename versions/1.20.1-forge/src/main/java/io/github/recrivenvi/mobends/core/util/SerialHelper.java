// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.core.util;

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
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.core.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.util.ByteArrayBuffer;

public class SerialHelper {
    public static String readChar(DataInputStream stream, int length) throws IOException {
        ByteArrayBuffer buffer = new ByteArrayBuffer(length);
        for (int i = 0; i < length; ++i) {
            buffer.append(stream.readByte());
        }

        return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
    }
}
// ?}
