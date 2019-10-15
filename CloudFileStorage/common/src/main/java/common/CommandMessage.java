package common;

/**
 * Класс-обертка для отправки комад на сервер
 */

public class CommandMessage extends AbstractMessage {
    private Command command;
    private String filename;
    private String login;
    private int password;

    public CommandMessage(String login, Command command){
        this.command = command;
        this.login = login;
    }

    public CommandMessage(String login, String filename, Command command){
        this.command = command;
        this.filename = filename;
        this.login = login;
    }

    public CommandMessage(String login, int password, Command command){
        this.login = login;
        this.password = password;
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

    public String getLogin() {
        return login;
    }

    public int getPassword() {
        return password;
    }
}
