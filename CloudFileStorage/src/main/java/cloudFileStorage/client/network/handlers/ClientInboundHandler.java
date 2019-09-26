package cloudFileStorage.client.network.handlers;

import cloudFileStorage.client.network.Controllers;
import cloudFileStorage.common.FileMessage;
import cloudFileStorage.common.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Класс  для обрабоки полученного с сервера объекта
 */

public class ClientInboundHandler extends ChannelInboundHandlerAdapter {
    private final String storageCatalogPath = "client_storage/";

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
                ResponseMessage rm = (ResponseMessage) msg;
                Controllers.getMainController().updateServerTableView(rm.getFilesMap());
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
}
