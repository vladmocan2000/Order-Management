package sample.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.model.Client;

import java.lang.reflect.Field;
import java.util.List;

public class AbstractController<T> {

    public void createTable(List<T> objects, TableView<T> tableView) throws IllegalArgumentException{
        //TableView<T> tableView = new TableView<T>();
        for(Field field : objects.get(0).getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String s = field.getName();

            TableColumn newColumn = new TableColumn<>(s);
            newColumn.setMinWidth(100);
            newColumn.setCellValueFactory(new PropertyValueFactory<>(s));
            //if(field.getType().equals(String.class))

            //else
                //newColumn = new TableColumn<T,Integer>(s);
            //if(field.getType().equals(String.class))

            //else
                //newColumn.setCellValueFactory(new PropertyValueFactory<T,Integer>(s));

            //System.out.println(s);

            tableView.getColumns().add(newColumn);

        }
        //System.out.println("asdddddddddddddddddddd");
        //System.out.println(tableView.getColumns().get(1));

        final ObservableList<T> data = FXCollections.observableArrayList(objects);
        tableView.setItems(data);

        //Client asd = (Client) tableView.getItems().get(0);
        //System.out.println(asd.getName());

    }


}
