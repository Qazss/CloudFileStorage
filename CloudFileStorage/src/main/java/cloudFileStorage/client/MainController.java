package cloudFileStorage.client;

import cloudFileStorage.client.network.Controllers;
import cloudFileStorage.client.network.Network;
import cloudFileStorage.common.Command;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    TableView<TableLine> clientTableView;

    @FXML
    TableView<TableLine> serverTableView;

    @FXML
    TableColumn<TableLine, String> clientFileNameColumn;

    @FXML
    TableColumn<TableLine, String> clientFileSieColumn;

    @FXML
    TableColumn<TableLine, String> serverFileNameColumn;

    @FXML
    TableColumn<TableLine, String> serverFileSizeColumn;


    private ObservableList<TableLine> clientFileList;
    private ObservableList<TableLine> serverFileList;

    private HashMap<String, String>  serverFilesMap;

    private TableLine clientSelectedLine;
    private TableLine serverSelectedLine;

    @FXML
    public void initialize(){
        Controllers.setMainController(this);

        Thread t = new Thread(() -> {
            Network.getInstance().start();
        });
        t.setDaemon(true);
        t.start();

        clientFileList = FXCollections.observableArrayList();
        serverFileList = FXCollections.observableArrayList();

        clientFileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        clientFileSieColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        serverFileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        serverFileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));

        updateClientTableView();
    }

    /**
     * обновление TableView клиента
     */
    public void updateClientTableView() {
        try {
            clientTableView.getItems().clear();
            List<File> filesList = Files.walk(Paths.get("client_storage")).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());

            for(File file: filesList){
                TableLine line = new TableLine(file.getName(), file.length() + " bytes");
                clientFileList.add(line);
            }
            clientTableView.setItems(clientFileList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обновление TableView сервера
     * @param map - используется для обработки полученного с сервера списка файов
     */
    public void  updateServerTableView(HashMap<String, String> map){
        serverFilesMap = map;
        serverTableView.getItems().clear();

        for(String key: serverFilesMap.keySet()){
            TableLine line = new TableLine(key, serverFilesMap.get(key));
            serverFileList.add(line);
        }
        serverTableView.setItems(serverFileList);
    }

    /**
     * отправка команды на получение актуальной Map со списком
     * файлов на стороне сервера
     */
    public void serverTableViewUpdateRequest(){
        Network.getInstance().sendCommand(Command.GET_FILE_LIST);
    }

    /**
     * вызов метода Network отправки на сервер,
     * выбранного на стороне клиента, файла
     */
    public void sendFileToServer(){
        initClientSelectedItem();
        Network.getInstance().sendFile(clientSelectedLine.getFileName());
        serverTableViewUpdateRequest();
    }

    /**
     * вызов метода Network для отправки команды-запроса
     * на получение файла с сервера
     */
    public void sendFileToClient(){
        initServerSelectedItem();
        Network.getInstance().sendCommand(Command.GET_FILE, serverSelectedLine.getFileName());
    }

    /**
     * вызов метода Network для отправки команды
     * на удаление файла на сервере
     */
    public void deleteFileOnServer(){
        initServerSelectedItem();
        Network.getInstance().sendCommand(Command.DELETE_FILE, serverSelectedLine.getFileName());
        serverTableViewUpdateRequest();
    }

    /**
     * удаление файла из репозитория клиента
     */
    public void deleteFileOnClient(){
        try {
            initClientSelectedItem();
            Files.delete(Paths.get("client_storage/" + clientSelectedLine.getFileName()));
            updateClientTableView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * инициализация объекта класса TableLine
     * для получения данных из TableView клиента
     */
    private void initClientSelectedItem(){
        clientSelectedLine = clientTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * инициализация объекта класса TableLine
     * для получения данных из TableView сервера
     */
    private void initServerSelectedItem(){
        serverSelectedLine = serverTableView.getSelectionModel().getSelectedItem();
    }
}
