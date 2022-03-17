package sample.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.connection.ConnectionFactory;
import sample.dao.AbstractDAO;
import sample.dao.OrderDAO;
import sample.dao.ProductDAO;
import sample.model.Order;
import sample.model.Product;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderController extends AbstractController{
    public TextField idSearchTF;
    public TextField idTF;

    public TableColumn idColumn;
    public TableView<Order> table;
    public TableColumn idClientColumn;
    public TableColumn idProductColumn;
    public TableColumn quantity;

    public TextField idClientTF;
    public TextField idProductTF;
    public TextField quantityTF;
    OrderDAO orderDAO = new OrderDAO();
    ProductDAO productDAO = new ProductDAO();

    private static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    /**
     * Metoda afiseaza toate datele din tabelul din baza de date in tabelul din interfata grafica.
     */
    public void showTable()
    {
        table.getItems().clear();
        table.getColumns().clear();
        table.setEditable(true);

        List<Order> orders = orderDAO.selectAll();
        createTable(orders, table);

//        final ObservableList<Order> data = FXCollections.observableArrayList(orders);
//        table.setItems(data);
    }

    /**
     * Metoda adauga o noua comanda bazata pe datele introduse de utilizator in campurile din interfata grafica.
     */
    public void addOrder()
    {
        int id = Integer.parseInt(idTF.getText());
        int idClient= Integer.parseInt(idClientTF.getText());
        int idProduct = Integer.parseInt(idProductTF.getText());
        int quantity = Integer.parseInt(quantityTF.getText());

        Product product = productDAO.findById(idProduct);
        if(quantity > product.getStock())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Stoc Insuficient");
            alert.showAndWait();
            return;
        }
        else {
            product.setStock(product.getStock()-quantity);
        }

        Order order = new Order();
        order.setId(id);
        order.setIdClient(idClient);
        order.setIdProduct(idProduct);
        order.setQuantity(quantity);

        orderDAO.add(order);
        showTable();
    }

    /**
     *Metoda actualizeaza o comanda selectata din interfata grafica.
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void updateOrder() throws IllegalAccessException, ClassNotFoundException {
        int id = Integer.parseInt(idTF.getText());
        int idClient= Integer.parseInt(idClientTF.getText());
        int idProduct = Integer.parseInt(idProductTF.getText());
        int quantity = Integer.parseInt(quantityTF.getText());

        Order order = new Order();
        order.setId(id);
        order.setIdClient(idClient);
        order.setIdProduct(idProduct);
        order.setQuantity(quantity);

        orderDAO.update(order, id);
        showTable();
    }

    /**
     * Metoda introduce datele unui rand selectat din interfata grafica in TextField-uri.
     */
    public void clickOnRow()
    {
        Order order = table.getSelectionModel().getSelectedItem();
        idTF.setText(Integer.toString(order.getId()));
        idClientTF.setText(Integer.toString(order.getIdClient()));
        idProductTF.setText(Integer.toString(order.getIdProduct()));
        quantityTF.setText(Integer.toString(order.getQuantity()));
    }

    /**
     * Metoda sterge o comanda selectata din interfata grafica.
     */
    public void delete() {
        Order order = table.getSelectionModel().getSelectedItem();

        orderDAO.remove(order);
        showTable();
    }

    /**
     * Metoda foloseste metoda omonima din clasa AbstractDAO pentru a gasi o anumita comanda.
     */
    public void findById() {
        int id = Integer.parseInt(idSearchTF.getText());
        Order order = orderDAO.findById(id);
        table.getItems().clear();

        List<Order> ordersList = new ArrayList<>();
        ordersList.add(order);
        final ObservableList<Order> data = FXCollections.observableArrayList(ordersList);
        table.setItems(data);
    }

    /**
     * Metoda creeaza un fisier .txt cu numele format din id-ul comenzii si id-ul clientului care a comandat-o.
     * @throws IOException
     * @throws SQLException
     */
    public void printSelectedOrder() throws IOException, SQLException {
        int id = Integer.parseInt(idTF.getText());
        int idClient= Integer.parseInt(idClientTF.getText());

        String path = System.getProperty("user.dir") + "\\bills\\";
        String fileName = id + "_" + idClient + ".txt";
        path += fileName;

        File file = new File(path);
        if(file.exists())
        {
            file.delete();
        }
        file.createNewFile();
        System.out.println(fileName);
        System.out.println(path);
        writeFile(id,file);
    }

    /**
     * Metoda scrie in fisierul creat cu metoda printSelectedOrder(). Aceasta creeaza un query pentru a face rost de toate informatiile necesare facturii.
     * @param id
     * @param file
     * @throws IOException
     * @throws SQLException
     */
    public void writeFile(int id, File file) throws IOException, SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query =
                "SELECT "+
                        "`order`.`id` AS `Order ID`, `client`.`name` AS `Customer Name`, `client`.`address` AS `Address`, "+
                        "`product`.`id` AS `Product ID`, `product`.`name` AS `Product Name`, `product`.`price` AS `Product Price`, `order`.`quantity` AS `Quantity purchased` "+
                        "FROM ((`order` JOIN `client` ON ((`order`.`idClient` = `client`.`id`))) JOIN `product` ON (((`order`.`idProduct` = `product`.`id`) "+
                        " AND (`order`.`id` = ?))))";
        try {
//            System.out.println(query);
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1,id);
            rs = statement.executeQuery();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "dao/OrderController: " + e.getMessage());
        }

        if(rs==null)
        {
            System.out.println("No order found!");
            return;
        }

        FileWriter fw = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fw);

        rs.next();
        out.write("Order ID: " + rs.getString(1) + "\n");
        out.write(" Customer Name: " + rs.getString(2) + "\n");
        out.write(" Address: " + rs.getString(3) + "\n");
        out.write("     Product Name: " + rs.getString(5) + "\n");//4
        out.write("     Product Price: " + rs.getString(6) + "\n");//5
        out.write("     Quantity: " + rs.getString(4) + "\n");//6
//        int price = Integer.parseInt(rs.getString(5));
//        int quantity = Integer.parseInt(rs.getString(6));
//        int total = price * quantity;
//        out.write("                 Total:" + rs.getInt(5) * rs.getInt(6) + "\n");

        out.write("\n");

        out.flush();
        out.close();
        ConnectionFactory.close(rs);
        ConnectionFactory.close(statement);
        ConnectionFactory.close(connection);
        //fw.write();
    }

}


