package server.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import server.DataBaseService;



public class ServerInboundHandler extends ChannelInboundHandlerAdapter {
    private final String storageCatalogPath = "server" + File.separator + "server_storage" + File.separator;
    private ResponseMessage response;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }

            if(msg instanceof FileMessage){
                FileMessage fm = (FileMessage) msg;
                String clientStoragePath = storageCatalogPath + fm.getUserLogin() + File.separator + fm.getFilename();

                if(!Files.exists(Paths.get(clientStoragePath))){
                    FileOutputStream fos = new FileOutputStream(clientStoragePath);
                    fos.write(fm.getData());
                    fos.flush();
                    fos.close();
                }
            }
            if(msg instanceof CommandMessage){
                CommandMessage cm = (CommandMessage) msg;
                System.out.println("Command: " + cm.getCommand());
                String clientStoragePath = storageCatalogPath + cm.getLogin() + File.separator;
                System.out.println("clientStoragePath: " + clientStoragePath);

                if(cm.getCommand() == Command.AUTHORIZATION){
                    tryToAuthorize(ctx, cm);
                }
                if(cm.getCommand() == Command.REGISTRATION){
                    tryToRegister(ctx, cm);
                }
                if(cm.getCommand() == Command.GET_FILE_LIST){
                    getFileList(ctx, clientStoragePath);
                }
                if(cm.getCommand() == Command.DELETE_FILE){
                    String path = clientStoragePath + cm.getFilename();
                    if(Files.exists(Paths.get(path))) {
                        Files.delete(Paths.get(path));
                    }
                }
                if(cm.getCommand() == Command.GET_FILE){
                    System.out.println("GET_FILE: " + clientStoragePath + cm.getFilename());
                    if (Files.exists(Paths.get(clientStoragePath + cm.getFilename()))) {
                        FileMessage fm = new FileMessage(cm.getLogin(), Paths.get(clientStoragePath + cm.getFilename()));
                        ctx.writeAndFlush(fm);
                    }
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void getFileList(ChannelHandlerContext ctx, String clientStoragePath){
        ServerFileListMessage rm = new ServerFileListMessage();

        try {
            List<File> filesList = Files.walk(Paths.get(clientStoragePath)).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());

            for (File file : filesList) {
                rm.getFilesMap().put(file.getName(), file.length() + " bytes");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        ctx.writeAndFlush(rm);
    }

    private void tryToRegister(ChannelHandlerContext ctx, CommandMessage cm){
        try {
            DataBaseService.connect();
            DataBaseService.registerNewUser(cm.getLogin(), cm.getPassword());
            Files.createDirectories(Paths.get(storageCatalogPath + cm.getLogin()));

            response = new ResponseMessage(Response.REGISTRATION_SUCCESS);
            DataBaseService.disconnect();
        } catch (SQLException | IOException e){
            response = new ResponseMessage(Response.REGISTRATION_FAILED);
            e.printStackTrace();
        }
        ctx.writeAndFlush(response);
    }

    private void tryToAuthorize(ChannelHandlerContext ctx, CommandMessage cm) {
        try {
            DataBaseService.connect();
            boolean isAuthSuccess = DataBaseService.checkAuthorization(cm.getLogin(), cm.getPassword());

            if (isAuthSuccess) {
                response = new ResponseMessage(Response.AUTHORIZATION_SUCCESS);
            } else {
                response = new ResponseMessage(Response.AUTHORIZATION_FAILED);
            }
            ctx.writeAndFlush(response);
            DataBaseService.disconnect();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
