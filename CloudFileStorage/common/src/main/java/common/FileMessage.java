package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Класс-обертка для отправки файлов
 */

public class FileMessage extends AbstractMessage {
    private String userLogin;
    private String filename;
    private byte[] data;

    public FileMessage(String userLogin, Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
        this.userLogin = userLogin;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public String getUserLogin() {
        return userLogin;
    }
}
