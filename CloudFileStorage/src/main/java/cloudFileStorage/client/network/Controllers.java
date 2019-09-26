package cloudFileStorage.client.network;

import cloudFileStorage.client.MainController;


/**
 * Класс, хранящий статическую ссылку на контроллеры
 * используется для вызова методов контроллера из других классов
 */

public class Controllers {
    private static MainController mainController;

    public static MainController getMainController() {
        return mainController;
    }

    public static void setMainController(MainController mainController) {
        Controllers.mainController = mainController;
    }
}
