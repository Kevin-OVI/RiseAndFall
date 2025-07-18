package fr.butinfoalt.riseandfall.network.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire pour lire des données à partir d'un flux d'entrée.
 */
public class ReadHelper {
    /**
     * Le flux d'entrée à partir duquel lire les données.
     */
    private final InputStream inputStream;

    /**
     * Constructeur de la classe ReadHelper.
     *
     * @param inputStream Le flux d'entrée à partir duquel lire les données.
     */
    public ReadHelper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Lit un nombre spécifié d'octets à partir du flux d'entrée.
     *
     * @param len Le nombre d'octets à lire.
     * @return Un tableau d'octets contenant les données lues.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public byte[] readBytesArray(int len) throws IOException {
        byte[] buffer = this.inputStream.readNBytes(len);
        if (buffer.length != len) {
            throw new SocketException("Socket closed");
        }
        return buffer;
    }

    /**
     * Lit un octet du flux d'entrée et le convertit en un booléen.
     *
     * @return true si l'octet est différent de 0, false sinon.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public boolean readBoolean() throws IOException {
        return this.readByte() != 0;
    }

    /**
     * Lit un octet du flux d'entrée.
     *
     * @return L'octet lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public byte readByte() throws IOException {
        return this.readBytesArray(1)[0];
    }

    /**
     * Lit un entier court à partir du flux d'entrée.
     *
     * @return L'entier lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public short readShort() throws IOException {
        return ByteBuffer.wrap(this.readBytesArray(2)).getShort();
    }

    /**
     * Lit un entier à partir du flux d'entrée.
     *
     * @return L'entier lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public int readInt() throws IOException {
        return ByteBuffer.wrap(this.readBytesArray(4)).getInt();
    }

    /**
     * Lit un entier long à partir du flux d'entrée.
     *
     * @return L'entier lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public long readLong() throws IOException {
        return ByteBuffer.wrap(this.readBytesArray(8)).getLong();
    }

    /**
     * Lit un flottant de 4 octets à partir du flux d'entrée.
     *
     * @return Le flottant lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public float readFloat() throws IOException {
        return ByteBuffer.wrap(this.readBytesArray(4)).getFloat();
    }

    /**
     * Lit un double à partir du flux d'entrée.
     *
     * @return Le double lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public double readDouble() throws IOException {
        return ByteBuffer.wrap(this.readBytesArray(8)).getDouble();
    }

    /**
     * Lit un caractère à partir du flux d'entrée.
     *
     * @return Le caractère lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public char readChar() throws IOException {
        return ByteBuffer.wrap(this.readBytesArray(2)).getChar();
    }

    /**
     * Lit un tableau de booléens à partir du flux d'entrée.
     * Les booléens sont stockés sous forme de bits dans un tableau d'octets pour économiser de l'espace.
     *
     * @param size La taille du tableau de booléens à lire.
     * @return Le tableau de booléens lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public boolean[] readBooleanArray(int size) throws IOException {
        boolean[] booleans = new boolean[size];
        byte[] bytes = this.readBytesArray((size + 7) / 8);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 8 && i < size; j++) {
                booleans[i] = (bytes[i / 8] & 1 << j) != 0;
                i++;
            }
        }
        return booleans;
    }

    /**
     * Lit un tableau de booléens à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau de booléens lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public boolean[] readSizedBooleanArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readBooleanArray(size);
    }

    /**
     * Lit un tableau d'octets à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau d'octets lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public byte[] readSizedByteArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readBytesArray(size);
    }

    /**
     * Lit un tableau d'entiers courts à partir du flux d'entrée.
     *
     * @param size La taille du tableau d'entiers courts à lire.
     * @return Le tableau d'entiers courts lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public short[] readShortArray(int size) throws IOException {
        short[] shorts = new short[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readBytesArray(size * 2));
        for (int i = 0; i < size; i++) {
            shorts[i] = buffer.getShort();
        }
        return shorts;
    }

    /**
     * Lit un tableau d'entiers courts à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau d'entiers courts lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public short[] readSizedShortArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readShortArray(size);
    }

    /**
     * Lit un tableau d'entiers à partir du flux d'entrée.
     *
     * @param size La taille du tableau d'entiers à lire.
     * @return Le tableau d'entiers lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public int[] readIntArray(int size) throws IOException {
        int[] ints = new int[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readBytesArray(size * 4));
        for (int i = 0; i < size; i++) {
            ints[i] = buffer.getInt();
        }
        return ints;
    }

    /**
     * Lit un tableau d'entiers à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau d'entiers lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public int[] readSizedIntArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readIntArray(size);
    }

    /**
     * Lit un tableau d'entiers longs à partir du flux d'entrée.
     *
     * @param size La taille du tableau d'entiers longs à lire.
     * @return Le tableau d'entiers longs lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public long[] readLongArray(int size) throws IOException {
        long[] longs = new long[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readBytesArray(size * 8));
        for (int i = 0; i < size; i++) {
            longs[i] = buffer.getLong();
        }
        return longs;
    }

    /**
     * Lit un tableau d'entiers longs à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau d'entiers longs lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public long[] readSizedLongArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readLongArray(size);
    }

    /**
     * Lit un tableau de flottants à partir du flux d'entrée.
     *
     * @param size La taille du tableau de flottants à lire.
     * @return Le tableau de flottants lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public float[] readFloatArray(int size) throws IOException {
        float[] floats = new float[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readBytesArray(size * 4));
        for (int i = 0; i < size; i++) {
            floats[i] = buffer.getFloat();
        }
        return floats;
    }

    /**
     * Lit un tableau de flottants à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau de flottants lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public float[] readSizedFloatArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readFloatArray(size);
    }

    /**
     * Lit un tableau de doubles à partir du flux d'entrée.
     *
     * @param size La taille du tableau de doubles à lire.
     * @return Le tableau de doubles lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public double[] readDoubleArray(int size) throws IOException {
        double[] doubles = new double[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readBytesArray(size * 8));
        for (int i = 0; i < size; i++) {
            doubles[i] = buffer.getDouble();
        }
        return doubles;
    }

    /**
     * Lit un tableau de doubles à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau de doubles lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public double[] readSizedDoubleArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readDoubleArray(size);
    }

    /**
     * Lit un tableau de caractères à partir du flux d'entrée.
     *
     * @param size La taille du tableau de caractères à lire.
     * @return Le tableau de caractères lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public char[] readCharArray(int size) throws IOException {
        char[] chars = new char[size];
        ByteBuffer buffer = ByteBuffer.wrap(this.readBytesArray(size * 2));
        for (int i = 0; i < size; i++) {
            chars[i] = buffer.getChar();
        }
        return chars;
    }

    /**
     * Lit un tableau de caractères à partir du flux d'entrée.
     * Le tableau est précédé de sa taille sous forme d'entier. Si la taille est négative, le tableau est considéré comme nul.
     *
     * @return Le tableau de caractères lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public char[] readSizedCharArray() throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        return this.readCharArray(size);
    }

    /**
     * Lit une chaîne de caractères à partir du flux d'entrée.
     * La chaîne est encodée en UTF-8.
     *
     * @return La chaîne de caractères lue.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public String readString() throws IOException {
        byte[] b = this.readSizedByteArray();
        if (b == null) return null;
        return new String(b, StandardCharsets.UTF_8);
    }

    /**
     * Lit un tableau d'objets sérialisables à partir du flux d'entrée.
     *
     * @param deserializer L'interface de désérialisation pour les objets sérialisables.
     * @param <T>          Le type des objets sérialisables.
     * @return Le tableau d'objets sérialisables lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public <T extends ISerializable> List<T> readSerializableList(IDeserializer<T> deserializer) throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(deserializer.deserialize(this));
        }
        return list;
    }

    /**
     * Lit un tableau d'objets sérialisables à partir du flux d'entrée et d'un argument de contexte.
     *
     * @param deserializer L'interface de désérialisation pour les objets sérialisables.
     * @param context      Le contexte utilisé lors de la désérialisation.
     * @param <T>          Le type des objets sérialisables.
     * @param <U>          Le type du contexte.
     * @return Le tableau d'objets sérialisables lu.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture.
     */
    public <T extends ISerializable, U> List<T> readSerializableList(IContextDeserializer<T, U> deserializer, U context) throws IOException {
        int size = this.readInt();
        if (size < 0) return null;
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(deserializer.deserialize(this, context));
        }
        return list;
    }

    /**
     * Lit un fichier à partir du flux d'entrée et l'enregistre dans le fichier spécifié.
     *
     * @param file Le fichier dans lequel enregistrer les données lues.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture ou de l'écriture.
     */
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

    /**
     * Lit un fichier à partir du flux d'entrée et le renvoie sous forme de fichier temporaire.
     *
     * @return Le fichier temporaire contenant les données lues.
     * @throws IOException Si une erreur d'entrée/sortie se produit lors de la lecture ou de l'écriture.
     */
    public File readFile() throws IOException {
        File file = File.createTempFile("riseandfall-protocol", ".tmp");
        this.readFileTo(file);
        return file;
    }
}
