import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SocketChannelUtils {

    public static void sendString(SocketChannel socketChannel, String message) throws IOException {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + messageBytes.length);

        // Write the length of the message as the first 4 bytes
        buffer.putInt(messageBytes.length);
        buffer.put(messageBytes);
        buffer.flip();

        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }

    public static String receiveString(SocketChannel socketChannel) throws IOException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);

        // Read the length of the incoming message
        while (lengthBuffer.hasRemaining()) {
            socketChannel.read(lengthBuffer);
        }

        lengthBuffer.flip();
        int messageLength = lengthBuffer.getInt();

        ByteBuffer messageBuffer = ByteBuffer.allocate(messageLength);

        // Read the message content
        while (messageBuffer.hasRemaining()) {
            socketChannel.read(messageBuffer);
        }

        messageBuffer.flip();
        byte[] messageBytes = new byte[messageLength];
        messageBuffer.get(messageBytes);

        return new String(messageBytes, StandardCharsets.UTF_8);
    }
}