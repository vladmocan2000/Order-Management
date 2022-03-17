package sample.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.dao.ProductDAO;
import sample.dao.OrderDAO;
import sample.model.Client;
import sample.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductController extends AbstractController{
    public TextField idSearchTF;
    public TextField idTF;
    public TextField nameTF;
    public TextField priceTF;
    public TextField stockTF;
    public TableView<Product> table;
    ProductDAO productDAO = new ProductDAO();


    /**
     * Metoda afiseaza toate datele din tabelul din baza de date in tabelul din interfata grafica.
     */
    public void showTable()
    {
        table.getItems().clear();
        table.getColumns().clear();
        table.setEditable(true);

        List<Product> products = productDAO.selectAll();
        createTable(products, table);

    }

    /**
     * Metoda adauga un nou produs bazat pe datele introduse de utilizator in campurile din interfata grafica.
     */
    public void addProduct()
    {
        int id = Integer.parseInt(idTF.getText());
        String name = nameTF.getText();
        int price = Integer.parseInt(priceTF.getText());
        int stock = Integer.parseInt(stockTF.getText());

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);

        productDAO.add(product);
        showTable();
    }

    /**
     * Metoda actualizeaza un produs selectat din interfata grafica.
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void updateProduct() throws IllegalAccessException, ClassNotFoundException {
        int id = Integer.parseInt(idTF.getText());
        String name = nameTF.getText();
        int price = Integer.parseInt(priceTF.getText());
        int stock = Integer.parseInt(stockTF.getText());

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);

        productDAO.update(product, id);
        showTable();
    }

    /**
     * Metoda introduce datele unui rand selectat din interfata grafica in TextField-uri.
     */
    public void clickOnRow()
    {
        Product product = table.getSelectionModel().getSelectedItem();
        idTF.setText(Integer.toString(product.getId()));
        nameTF.setText(product.getName());
        priceTF.setText(Integer.toString(product.getPrice()));
        stockTF.setText(Integer.toString(product.getStock()));
    }

    /**
     * Metoda sterge un produs selectat din interfata grafica.
     */
    public void delete() {
        Product product = table.getSelectionModel().getSelectedItem();
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.removeAllByProduct(product);
        productDAO.remove(product);
        showTable();
    }

    /**
     *Metoda foloseste metoda omonima din clasa AbstractDAO pentru a gasi un anumit produs.
     */
    public void findById() {
        int id = Integer.parseInt(idSearchTF.getText());
        Product product = productDAO.findById(id);
        table.getItems().clear();

        List<Product> productsList = new ArrayList<>();
        productsList.add(product);
        final ObservableList<Product> data = FXCollections.observableArrayList(productsList);
        table.setItems(data);
    }
}


