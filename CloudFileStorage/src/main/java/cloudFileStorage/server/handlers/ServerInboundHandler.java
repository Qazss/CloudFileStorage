package cloudFileStorage.server.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import cloudFileStorage.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ServerInboundHandler extends ChannelInboundHandlerAdapter {
    private final String storageCatalogPath = "server_storage/";
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
                }
            }
            if(msg instanceof CommandMessage){
                CommandMessage cm = (CommandMessage) msg;

                if(cm.getCommand() == Command.GET_FILE_LIST){
                    ResponseMessage rm = new ResponseMessage();
                    List<File> filesList = Files.walk(Paths.get(storageCatalogPath)).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());

                    for(File file: filesList){
                        rm.getFilesMap().put(file.getName(), file.length() + " bytes");
                    }
                    ctx.writeAndFlush(rm);
                }
                if(cm.getCommand() == Command.DELETE_FILE){
                    String path = storageCatalogPath + cm.getFilename();
                    if(Files.exists(Paths.get(path))) {
                        Files.delete(Paths.get(path));
                    }
                }
                if(cm.getCommand() == Command.GET_FILE){
                    if (Files.exists(Paths.get(storageCatalogPath + cm.getFilename()))) {
                        FileMessage fm = new FileMessage(Paths.get(storageCatalogPath + cm.getFilename()));
                        ctx.writeAndFlush(fm);
                    }
                }
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
