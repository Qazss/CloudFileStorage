package common;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.HashMap;

/**
 * Класс-обертка для получения списка
 * актуальных файлов на сервере
 */

public class ServerFileListMessage extends AbstractMessage {
    private HashMap<String, String> filesMap;

    public ServerFileListMessage(HashMap<String, String> filesMap) {
        this.filesMap = filesMap;
    }

    public ServerFileListMessage(){
        filesMap = new HashMap<>();
    }

    public HashMap<String, String> getFilesMap() {
        return filesMap;
    }

    public void setFilesMap(HashMap<String, String> filesMap) {
        this.filesMap = filesMap;
    }
}
