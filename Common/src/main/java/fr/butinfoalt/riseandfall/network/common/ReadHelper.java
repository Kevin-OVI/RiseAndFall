package fr.butinfoalt.riseandfall.network.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ReadHelper {
    private final InputStream inputStream;

    public ReadHelper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public byte[] readNBytes(int len) throws IOException {
        byte[] buffer = this.inputStream.readNBytes(len);
        if (buffer.length != len) {
            throw new SocketException("Socket closed");
        }
        return buffer;
    }

    public boolean readBoolean() throws IOException {
        return this.readByte() != 0;
    }

    public byte readByte() throws IOException {
        return this.readNBytes(1)[0];
    }

    public short readShort() throws IOException {
        return ByteBuffer.wrap(this.readNBytes(2)).getShort();
    }

    public int readInt() throws IOException {
        return ByteBuffer.wrap(this.readNBytes(4)).getInt();
    }

    public long readLong() throws IOException {
        return ByteBuffer.wrap(this.readNBytes(8)).getLong();
    }

    public float readFloat() throws IOException {
        return ByteBuffer.wrap(this.readNBytes(4)).getFloat();
    }

    public double readDouble() throws IOException {
        return ByteBuffer.wrap(this.readNBytes(8)).getDouble();
    }

    public char readChar() throws IOException {
        return ByteBuffer.wrap(this.readNBytes(2)).getChar();
    }

    public boolean[] readBooleanArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        boolean[] booleans = new boolean[size];
        byte[] bytes = this.readNBytes((size + 7) / 8);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 8 && i < size; j++) {
                booleans[i] = (bytes[i / 8] & 1 << j) != 0;
                i++;
            }
        }
        return booleans;
    }

    public byte[] readByteArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readNBytes(size);
    }

    public short[] readShortArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        short[] shorts = new short[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readNBytes(size * 2));
        for (int i = 0; i < size; i++) {
            shorts[i] = buffer.getShort();
        }
        return shorts;
    }

    public int[] readIntArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        int[] ints = new int[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readNBytes(size * 4));
        for (int i = 0; i < size; i++) {
            ints[i] = buffer.getInt();
        }
        return ints;
    }

    public long[] readLongArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        long[] longs = new long[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readNBytes(size * 8));
        for (int i = 0; i < size; i++) {
            longs[i] = buffer.getLong();
        }
        return longs;
    }

    public float[] readFloatArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        float[] floats = new float[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readNBytes(size * 4));
        for (int i = 0; i < size; i++) {
            floats[i] = buffer.getFloat();
        }
        return floats;
    }

    public double[] readDoubleArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        double[] doubles = new double[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readNBytes(size * 8));
        for (int i = 0; i < size; i++) {
            doubles[i] = buffer.getDouble();
        }
        return doubles;
    }

    public char[] readCharArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        char[] chars = new char[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readNBytes(size * 2));
        for (int i = 0; i < size; i++) {
            chars[i] = buffer.getChar();
        }
        return chars;
    }

    public String readString() throws IOException {
        byte[] b = this.readByteArray();
        if (b == null) return null;
        return new String(b, StandardCharsets.UTF_8);
    }

    public <T extends ISerializable> T readSerializable(IDeserializer<T> deserializer) throws IOException {
        return deserializer.deserialize(this);
    }

    public void readFileTo(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            long remainingSize = this.readLong();
            byte[] buffer = new byte[1024];
            while (remainingSize > 0) {
                int read = this.inputStream.read(buffer, 0, (int) Math.min(buffer.length, remainingSize));
                if (read == -1) {
                    throw new IOException("End of stream reached before reading file");
                }
                fos.write(buffer, 0, read);
                remainingSize -= read;
            }
        }
    }

    public File readFile() throws IOException {
        File file = File.createTempFile("riseandfall-protocol", ".tmp");
        this.readFileTo(file);
        return file;
    }
}
