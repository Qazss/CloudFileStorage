package client;

import client.authWindow.AuthController;
import client.mainWindow.MainController;


/**
 * Класс, хранящий статическую ссылку на контроллеры
 * используется для вызова методов контроллера из других классов
 */

public class Controllers {
    private static MainController mainController;
    private static AuthController authController;

    public static MainController getMainController() {
        return mainController;
    }

    public static void setMainController(MainController mainController) {
        Controllers.mainController = mainController;
    }

    public static AuthController getAuthController() {
        return authController;
    }

    public static void setAuthController(AuthController authController) {
        Controllers.authController = authController;
    }
}
