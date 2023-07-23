package tk.zulfengaming.zulfbungee.universal.storage.util.variable;

import com.google.common.primitives.Longs;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.Value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UniversalVariableUtils {

    public static byte[] add(byte[] fromDB, Value toDB, String fromType) {

        byte[] bytesOut = new byte[0];

        if (toDB.type.equals("long")) {

            long storedLong = Longs.fromByteArray(fromDB);
            long givenLong = Longs.fromByteArray(toDB.data);
            
            bytesOut = Longs.toByteArray(storedLong + givenLong);

        } else if (toDB.type.equals("double")) {

            if (fromType.equals("long")) {

                long storedLong = Longs.fromByteArray(fromDB);

                double givenDouble = ByteBuffer.wrap(toDB.data).getDouble();

                bytesOut = ByteBuffer.wrap(new byte[8]).putDouble(storedLong + givenDouble).array();

            }

        }

        return bytesOut;

    }

    public static byte[] subtract(byte[] fromDB, Value toDB, String fromType) {

        byte[] bytesOut = new byte[0];

        if (toDB.type.equals("long")) {

            long storedLong = Longs.fromByteArray(fromDB);
            long givenLong = Longs.fromByteArray(toDB.data);

            bytesOut = Longs.toByteArray(storedLong - givenLong);

        } else if (toDB.type.equals("double")) {

            if (fromType.equals("long")) {

                long storedLong = Longs.fromByteArray(fromDB);

                double givenDouble = ByteBuffer.wrap(toDB.data).getDouble();

                bytesOut = ByteBuffer.wrap(new byte[8]).putDouble(storedLong - givenDouble).array();

            }

        }

        return bytesOut;

    }

}
