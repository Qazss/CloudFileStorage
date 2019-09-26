package cloudFileStorage.client.network;

import cloudFileStorage.client.network.handlers.ClientInboundHandler;
import cloudFileStorage.common.Command;
import cloudFileStorage.common.CommandMessage;
import cloudFileStorage.common.FileMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Класс для установки соединения с сервером
 * Используется для отпраки команд и файлов
 */

public class Network {
    private final String storageCatalogPath = "client_storage/";

    private Channel currentChannel;
    private String host = "localhost";
    private int port = 8185;

    private static Network ourInstance = new Network();

    public static Network getInstance() {
        return ourInstance;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            clientBootstrap.remoteAddress(new InetSocketAddress(host, port));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                            new ObjectEncoder(),
                            new ClientInboundHandler()
                    );
                    currentChannel = socketChannel;
                }
            });
            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для отправки файлов
     * @param fileName - имя файла для отправки
     */
    public void sendFile(String fileName){
        if (Files.exists(Paths.get(storageCatalogPath + fileName)) && isConnectionOpened()) {
            try {
                FileMessage fm = new FileMessage(Paths.get(storageCatalogPath + fileName));
                currentChannel.writeAndFlush(fm);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод отправки команды серверу
     * @param command
     */
    public void sendCommand(Command command){
        if(isConnectionOpened()) {
            CommandMessage cm = new CommandMessage(command);
            currentChannel.writeAndFlush(cm);
        }
    }

    /**
     * Метод отпраки команды серверу с именем файла
     * используется для удаления, получения файла с сервера
     * @param command
     * @param fileName
     */
    public void sendCommand(Command command, String fileName){
        if(isConnectionOpened()) {
            CommandMessage cm = new CommandMessage(fileName, command);
            currentChannel.writeAndFlush(cm);
        }
    }

    public boolean isConnectionOpened() {
        return currentChannel != null && currentChannel.isActive();
    }

    public void closeConnection() {
        currentChannel.close();
    }
}
