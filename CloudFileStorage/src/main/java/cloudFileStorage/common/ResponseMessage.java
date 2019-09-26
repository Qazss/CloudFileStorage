package cloudFileStorage.common;

import java.util.HashMap;

/**
 * Класс-обертка для получения списка
 * актуальных файлов на сервере
 */

public class ResponseMessage extends AbstractMessage {
    private HashMap<String, String> filesMap;

    public ResponseMessage(HashMap<String, String> filesMap) {
        this.filesMap = filesMap;
    }

    public ResponseMessage(){
        filesMap = new HashMap<>();
    }

    public HashMap<String, String> getFilesMap() {
        return filesMap;
    }

    public void setFilesMap(HashMap<String, String> filesMap) {
        this.filesMap = filesMap;
    }
}
