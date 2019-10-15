package client.mainWindow;

import javafx.beans.property.SimpleStringProperty;

/**
 * Класс-обертка для записи
 * данныз в TableView
 */

public class TableLine {
    private final SimpleStringProperty fileName;
    private final SimpleStringProperty fileSize;

    public TableLine(String fileName, String fileSize) {
        this.fileName = new SimpleStringProperty(fileName);
        this.fileSize = new SimpleStringProperty(fileSize);
    }

    public String getFileName() {
        return fileName.get();
    }

    public String getFileSize() {
        return fileSize.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public void setFileSize(String fileSize) {
        this.fileSize.set(fileSize);
    }
}
