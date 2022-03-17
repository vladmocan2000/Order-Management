package sample.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.dao.ClientDAO;
import sample.dao.OrderDAO;
import sample.model.Client;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClientController extends AbstractController<Client> {
    public TextField idSearchTF;
    public TextField idTF;
    public TextField nameTF;
    public TextField addressTF;
    public TableColumn idColumn;
    public TableView<Client> table;
    public TableColumn nameColumn;
    public TableColumn addressColumn;
    ClientDAO clientDAO = new ClientDAO();

    private void initTable() throws IllegalArgumentException, IllegalAccessException {

    }

    /**
     * Metoda creeaza cloanele necesare tabelului Client.
     * @param tableView
     */
    private void initTableColumns(TableView<Client> tableView)
    {
        idColumn = new TableColumn<>("Id");
        idColumn.setMinWidth(60);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(100);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        addressColumn = new TableColumn<>("Address");
        addressColumn.setMinWidth(100);
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        tableView.getColumns().addAll(idColumn,nameColumn,addressColumn);
    }

    /**
     * Metoda afiseaza toate datele din tabelul din baza de date in tabelul din interfata grafica.
     */
    public void showTable() throws IllegalAccessException {
        table.getItems().clear();
        table.getColumns().clear();
        table.setEditable(true);


        List<Client> clients = clientDAO.selectAll();
        createTable(clients, table);


//        TableColumn asd =  table.getColumns().get(0);
//        System.out.println(asd.getText());
//        asd =  table.getColumns().get(1);
//        System.out.println(asd.getText());
//        asd =  table.getColumns().get(2);
//        System.out.println(asd.getText());
//
//        Client asdd = table.getItems().get(1);
//        System.out.println(asdd.getName() + " " + asdd.getAddress() + " " + asdd.getId());

    }

    /**
     * Metoda adauga un nou client bazat pe datele introduse de utilizator in campurile din interfata grafica.
     */
    public void addClient() throws IllegalAccessException {
        int id = Integer.parseInt(idTF.getText());
        String name = nameTF.getText();
        String address = addressTF.getText();

        Client client = new Client();
        client.setId(id);
        client.setName(name);
        client.setAddress(address);

        clientDAO.add(client);
        showTable();
    }

    /**
     * Metoda actualizeaza un client selectat din interfata grafica.
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void updateClient() throws IllegalAccessException, ClassNotFoundException {
        int id = Integer.parseInt(idTF.getText());
        String name = nameTF.getText();
        String address = addressTF.getText();

        Client client = new Client();
        client.setId(id);
        client.setName(name);
        client.setAddress(address);

        clientDAO.update(client, id);
        showTable();
    }

    /**
     * Metoda introduce datele unui rand selectat din interfata grafica in TextField-uri.
     */
    public void clickOnRow()
    {
        Client client = table.getSelectionModel().getSelectedItem();
        idTF.setText(Integer.toString(client.getId()));
        nameTF.setText(client.getName());
        addressTF.setText(client.getAddress());
    }

    /**
     * Metoda delete() sterge un client selectat din interfata grafica.
     */
    public void delete() throws IllegalAccessException {
        Client client = table.getSelectionModel().getSelectedItem();

        OrderDAO orderDAO = new OrderDAO();
        orderDAO.removeAllByClient(client);
        clientDAO.remove(client);
        showTable();
    }

    /**
     * Metoda findById() foloseste metoda omonima din clasa AbstractDAO pentru a gasi un anumit client.
     */
    public void findById() {
        int id = Integer.parseInt(idSearchTF.getText());
        Client client = clientDAO.findById(id);
        table.getItems().clear();

        List<Client> clientsList = new ArrayList<>();
        clientsList.add(client);
        final ObservableList<Client> data = FXCollections.observableArrayList(clientsList);
        table.setItems(data);
    }
}


