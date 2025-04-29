package fr.butinfoalt.riseandfall.network.common;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class WriteHelper {
    private final OutputStream outputStream;

    public WriteHelper(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeBoolean(boolean b) throws IOException {
        this.writeByte((byte) (b ? 1 : 0));
    }

    public void writeByte(byte b) throws IOException {
        this.outputStream.write(new byte[]{b});
    }

    public void writeShort(short s) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(2).putShort(s).array());
    }

    public void writeInt(int i) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(4).putInt(i).array());
    }

    public void writeLong(long l) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(8).putLong(l).array());
    }

    public void writeFloat(float f) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(4).putFloat(f).array());
    }

    public void writeDouble(double d) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(8).putDouble(d).array());
    }

    public void writeChar(char c) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(2).putChar(c).array());
    }

    public void writeBooleanArray(boolean[] booleans) throws IOException {
        if (booleans == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(booleans.length);
        byte[] converted = new byte[(booleans.length + 7) / 8];
        for (int i = 0; i < booleans.length; i++) {
            for (int j = 0; j < 8 && i < booleans.length; j++) {
                if (booleans[i]) {
                    converted[i / 8] |= (byte) (1 << j);
                }
                i++;
            }
        }
        this.outputStream.write(converted);
    }

    public void writeByteArray(byte[] bytes) throws IOException {
        if (bytes == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(bytes.length);
        this.outputStream.write(bytes);
    }

    public void writeShortArray(short[] shorts) throws IOException {
        if (shorts == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(shorts.length);
        ByteBuffer buffer = ByteBuffer.allocate(shorts.length * 2);
        for (short s : shorts) {
            buffer.putShort(s);
        }
        this.outputStream.write(buffer.array());
    }

    public void writeIntArray(int[] ints) throws IOException {
        if (ints == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(ints.length);
        ByteBuffer buffer = ByteBuffer.allocate(ints.length * 4);
        for (int i : ints) {
            buffer.putInt(i);
        }
        this.outputStream.write(buffer.array());
    }

    public void writeLongArray(long[] longs) throws IOException {
        if (longs == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(longs.length);
        ByteBuffer buffer = ByteBuffer.allocate(longs.length * 8);
        for (long l : longs) {
            buffer.putLong(l);
        }
        this.outputStream.write(buffer.array());
    }

    public void writeFloatArray(float[] floats) throws IOException {
        if (floats == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(floats.length);
        ByteBuffer buffer = ByteBuffer.allocate(floats.length * 4);
        for (float f : floats) {
            buffer.putFloat(f);
        }
        this.outputStream.write(buffer.array());
    }

    public void writeDoubleArray(double[] doubles) throws IOException {
        if (doubles == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(doubles.length);
        ByteBuffer buffer = ByteBuffer.allocate(doubles.length * 8);
        for (double d : doubles) {
            buffer.putDouble(d);
        }
        this.outputStream.write(buffer.array());
    }

    public void writeCharArray(char[] chars) throws IOException {
        if (chars == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(chars.length);
        ByteBuffer buffer = ByteBuffer.allocate(chars.length * 2);
        for (char c : chars) {
            buffer.putChar(c);
        }
        this.outputStream.write(buffer.array());
    }

    public void writeString(String s) throws IOException {
        this.writeByteArray(s == null ? null : s.getBytes(StandardCharsets.UTF_8));
    }

    public void writeSerializable(ISerializable serializable) throws IOException {
        serializable.toBytes(this);
    }

    public void writeFile(File file) throws FileNotFoundException, IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            this.writeLong(file.length());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                this.outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
