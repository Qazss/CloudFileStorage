package client.network.handlers;

import client.Controllers;
import client.network.Network;
import common.FileMessage;
import common.Response;
import common.ResponseMessage;
import common.ServerFileListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Класс  для обрабоки полученного с сервера объекта
 */

public class ClientInboundHandler extends ChannelInboundHandlerAdapter {
    private final String storageCatalogPath = "client/client_storage/";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            if(msg instanceof FileMessage){
                FileMessage fm = (FileMessage) msg;
                if(!Files.exists(Paths.get(storageCatalogPath + fm.getFilename()))){
                    FileOutputStream fos = new FileOutputStream(storageCatalogPath + fm.getFilename());
                    fos.write(fm.getData());
                    fos.flush();
                    fos.close();
                    Controllers.getMainController().updateClientTableView();
                }
            }
            if(msg instanceof ResponseMessage){
                ResponseMessage responseMessage = (ResponseMessage) msg;
                tryToAuthorize(responseMessage.getResponse());
            }
            if(msg instanceof ServerFileListMessage){
                ServerFileListMessage fileMessage = (ServerFileListMessage) msg;
                Controllers.getMainController().updateServerTableView(fileMessage.getFilesMap());
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void tryToAuthorize(Response response){
        if(response == Response.AUTHORIZATION_SUCCESS){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Controllers.getAuthController().initMainWindow();
                }
            });
        }
        if (response == Response.AUTHORIZATION_FAILED){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new Alert(Alert.AlertType.WARNING, "Ошибка авторизации \n Проверьте корректность логина и пароля", ButtonType.CLOSE).showAndWait();
                }
            });
        }
        if(response == Response.REGISTRATION_SUCCESS){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new Alert(Alert.AlertType.INFORMATION, "Вы успешно зарегистрированы", ButtonType.CLOSE).showAndWait();
                }
            });
        }
        if(response == Response.REGISTRATION_FAILED){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    new Alert(Alert.AlertType.INFORMATION, "Данный логин уже занят, попробуйте другой", ButtonType.CLOSE).showAndWait();
                }
            });
        }
    }
}
