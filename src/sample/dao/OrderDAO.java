package sample.dao;

import sample.connection.ConnectionFactory;
import sample.model.Client;
import sample.model.Order;
import sample.model.Product;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.List;

public class OrderDAO extends AbstractDAO<Order>{


    /**
     * Metoda va cauta in tabelul Order toate randurile specifice unui anumit client si le va sterge.
     * @param client
     */
    public void removeAllByClient(Client client)
    {
        List<Order> ordersList = this.selectAll();
        for (Order order: ordersList) {
            if(client.getId() == order.getIdClient())
                remove(order);
        }
    }

    /**
     * Metoda va cauta in tabelul Order toate randurile specifice unui anumit produs si le va sterge.
     * @param product
     */
    public void removeAllByProduct(Product product)
    {
        List<Order> ordersList = this.selectAll();
        for (Order order: ordersList) {
            if(product.getId() == order.getIdProduct())
                remove(order);
        }
    }

}
