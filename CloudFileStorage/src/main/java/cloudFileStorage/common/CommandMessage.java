package cloudFileStorage.common;

/**
 * Класс-обертка для отправки комад на сервер
 */

public class CommandMessage extends AbstractMessage {
    private Command command;
    private String filename;

    public CommandMessage(Command command){
        this.command = command;
    }

    public CommandMessage(String filename, Command command){
        if(command == Command.GET_FILE_LIST){
            throw new RuntimeException("Illegal command argument " + command.name());
        }
        this.filename = filename;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
