package fr.butinfoalt.riseandfall.network.common;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Classe utilitaire pour écrire des données dans un flux de sortie.
 */
public class WriteHelper {
    /**
     * Flux de sortie dans lequel les données seront écrites.
     */
    private final OutputStream outputStream;

    /**
     * Constructeur de la classe WriteHelper.
     *
     * @param outputStream Le flux de sortie dans lequel les données seront écrites.
     */
    public WriteHelper(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Écrit un booléen dans le flux de sortie.
     *
     * @param b Le booléen à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeBoolean(boolean b) throws IOException {
        this.writeByte((byte) (b ? 1 : 0));
    }

    /**
     * Écrit un octet dans le flux de sortie.
     *
     * @param b L'octet à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeByte(byte b) throws IOException {
        this.outputStream.write(new byte[]{b});
    }

    /**
     * Écrit un entier court dans le flux de sortie.
     *
     * @param s L'entier court à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeShort(short s) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(2).putShort(s).array());
    }

    /**
     * Écrit un entier dans le flux de sortie.
     *
     * @param i L'entier à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeInt(int i) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(4).putInt(i).array());
    }

    /**
     * Écrit un entier long dans le flux de sortie.
     *
     * @param l L'entier long à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeLong(long l) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(8).putLong(l).array());
    }

    /**
     * Écrit un flottant dans le flux de sortie.
     *
     * @param f Le flottant à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeFloat(float f) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(4).putFloat(f).array());
    }

    /**
     * Écrit un double dans le flux de sortie.
     *
     * @param d Le double à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeDouble(double d) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(8).putDouble(d).array());
    }

    /**
     * Écrit un caractère dans le flux de sortie.
     *
     * @param c Le caractère à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeChar(char c) throws IOException {
        this.outputStream.write(ByteBuffer.allocate(2).putChar(c).array());
    }

    /**
     * Écrit un tableau de booléens dans le flux de sortie.
     * Les booléens sont stockés sous forme de bits dans un tableau d'octets pour économiser de l'espace.
     *
     * @param booleans Le tableau de booléens à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
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

    /**
     * Écrit un tableau d'octets dans le flux de sortie.
     *
     * @param bytes Le tableau d'octets à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeByteArray(byte[] bytes) throws IOException {
        if (bytes == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(bytes.length);
        this.outputStream.write(bytes);
    }

    /**
     * Écrit un tableau d'entiers courts dans le flux de sortie.
     *
     * @param shorts Le tableau d'entiers courts à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
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

    /**
     * Écrit un tableau d'entiers dans le flux de sortie.
     *
     * @param ints Le tableau d'entiers à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
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

    /**
     * Écrit un tableau d'entiers longs dans le flux de sortie.
     *
     * @param longs Le tableau d'entiers longs à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
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

    /**
     * Écrit un tableau de flottants dans le flux de sortie.
     *
     * @param floats Le tableau de flottants à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
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

    /**
     * Écrit un tableau de doubles dans le flux de sortie.
     *
     * @param doubles Le tableau de doubles à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
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

    /**
     * Écrit un tableau de caractères dans le flux de sortie.
     *
     * @param chars Le tableau de caractères à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
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

    /**
     * Écrit une chaîne de caractères dans le flux de sortie. La chaîne est encodée en UTF-8.
     *
     * @param s La chaîne de caractères à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeString(String s) throws IOException {
        this.writeByteArray(s == null ? null : s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Écris un tableau d'objets sérialisables dans le flux de sortie.
     *
     * @param serializables Le tableau d'objets sérialisables à écrire.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */
    public void writeSerializableArray(ISerializable[] serializables) throws IOException {
        if (serializables == null) {
            this.writeInt(-1);
            return;
        }
        this.writeInt(serializables.length);
        for (ISerializable serializable : serializables) {
            serializable.toBytes(this);
        }
    }

    /**
     * Écris un fichier dans le flux de sortie.
     *
     * @param file Le fichier à écrire.
     * @throws FileNotFoundException Si le fichier n'est pas trouvé.
     * @throws IOException           Si une erreur d'entrée/sortie se produit.
     */
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
