package net.openhft.chronicle.queue;

import net.openhft.lang.io.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MappedMemoryTest {


    long BLOCK_SIZE = 1L << 27L;

    @Test
    public void withMappedNativeBytesTest() throws IOException {

        File tempFile = File.createTempFile("chronicle", "q");
        try {

            final MappedFile mappedFile = new MappedFile(tempFile.getName(), BLOCK_SIZE, 0);
            final MappedNativeBytes bytes = new MappedNativeBytes(mappedFile, true);
            bytes.writeLong(1, 1);
            long startTime = System.nanoTime();
            for (long i = 0; i < BLOCK_SIZE; i += 8) {
                bytes.writeLong(i);
            }

            System.out.println("With MappedNativeBytes,\t time=" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + ("ms, number of longs written=" + BLOCK_SIZE / 8));

        } finally {
            tempFile.delete();
        }

    }

    @Test
    public void withRawNativeBytesTess() throws IOException {

        File tempFile = File.createTempFile("chronicle", "q");
        try {

            MappedFile mappedFile = new MappedFile(tempFile.getName(), BLOCK_SIZE, 0);
            Bytes bytes1 = mappedFile.acquire(1).bytes();


            long startTime = System.nanoTime();
            for (long i = 0; i < BLOCK_SIZE; i += 8L) {
                bytes1.writeLong(i);
            }

            System.out.println("With NativeBytes,\t\t time=" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + ("ms, number of longs written=" + BLOCK_SIZE / 8));


        } finally {
            tempFile.delete();
        }

    }

    @Test
    public void mappedMemoryTest() throws IOException {

        File tempFile = File.createTempFile("chronicle", "q");
        try {
            int shift = 3;
            int blockSize = 1 << shift;
            final MappedFile mappedFile = new MappedFile(tempFile.getName(), BLOCK_SIZE, 0);
            final MappedNativeBytes bytes = new MappedNativeBytes(mappedFile, true);
            bytes.writeUTF("hello this is some very long text");

            bytes.clear();

            bytes.position(100);
            bytes.writeUTF("hello this is some more long text...................");

            bytes.position(100);
            System.out.println("result=" + bytes.readUTF());
        } finally {
            tempFile.delete();
        }

    }

    /**
     * ensure a IllegalStateException is throw if the block size is not a power of 2
     *
     * @throws IOException
     */
    @Test(expected = IllegalStateException.class)
    public void checkBlockSizeIsPowerOfTwoTest() throws IOException {
        File tempFile = File.createTempFile("chronicle", "q");
        MappedFile mappedFile = new MappedFile(tempFile.getName(), 10, 0);
        new ChronicleUnsafe(mappedFile);
    }


}
