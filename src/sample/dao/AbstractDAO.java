package sample.dao;

import sample.connection.ConnectionFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractDAO<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;

    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Metoda creeaza un String ce contine comanda SQL pentru selectarea elementelor dintr-un tabel anume.
     * @param field
     * @return
     */
    private String createSelectQuery(String field)
    {

        StringBuilder sb =new StringBuilder();
        sb.append("SELECT * FROM ");
            sb.append("order_management.");
        sb.append(type.getSimpleName().toLowerCase());
        sb.append(" WHERE ").append(field).append(" = ?");
        return sb.toString();
    }

    /**
     * Metoda creeaza o lista de obiecte avand ca elemente rezultatele unei comenzi SQL.
     * @param resultSet
     * @return
     */
    private List<T> createObjects(ResultSet resultSet)
    {
        List<T> list = new ArrayList<T>();
        try{
            while(resultSet.next())
            {
                T instance = type.newInstance();
                for(Field field: type.getDeclaredFields())
                {
                    Object value = resultSet.getObject(field.getName());
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance,value);
                }
                list.add(instance);
            }
        } catch (SQLException | IllegalAccessException | InstantiationException | IntrospectionException | InvocationTargetException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    /**
     * Metoda returneaza un rand dintr-un tabel care are id-ul egal cu cel transmis ca parametru.
     * @param id
     * @return
     */
    public T findById(int id)
    {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        System.out.println(query);
        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1,id);
            resultSet = statement.executeQuery();
            
            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "dao/AbstractDAO: " + e.getMessage());
        }finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Metoda returneaza o lista cu toate elementele unui tabel.
     * @return
     */
    public List<T> selectAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM order_management." + type.getSimpleName().toLowerCase() + ";";
        System.out.println(query);

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "dao/AbstractDAO: " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Metoda adauga in tabelul dorit ur rand nou.
     * @param t
     */
    public void add(T t) {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        //INSERT INTO `order_management`.`client` (`id`, `name`, `address`) VALUES ('7', 'g', 'ga');

        String s = "INSERT INTO " + "`" + "order_management" + "`" + "." + "`" + type.getSimpleName().toLowerCase() + "`" + " (";

        try {
            Class cls = Class.forName("sample.model." + type.getSimpleName());
            Field[] fields = cls.getDeclaredFields();
            int i=0;
            while(i < fields.length) {

                fields[i].setAccessible(true);
                s = s + "`" + fields[i].getName() + "`";
                if (i < fields.length - 1) {
                    s = s + ", ";

                } else {
                    s = s + ") ";
                }

                i++;
            }
            s = s + "VALUES ( ";

            i=0;
            while(i < fields.length) {

                fields[i].setAccessible(true);
                try {
                    s = s + "\'" + fields[i].get(t) + "\'";
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (i < fields.length - 1) {
                    s = s + ", ";

                } else {
                    s = s + ") ";
                }

                i++;
            }
            s = s + ';';

        } catch (ClassNotFoundException e) {
            e.printStackTrace();

        }

        try {
            System.out.println(s);

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(s);
            statement.execute();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "dao/AbstractDAO: " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Metoda va fi folosita penru a sterge din tabel un rand selectat din interfata grafica.
     * @param t
     */
    public void remove(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String s = "DELETE FROM  order_management." + type.getSimpleName().toLowerCase() + " WHERE `id` = ";
        try {
            Field field = Class.forName("sample.model." + type.getSimpleName()).getDeclaredField("id");
            field.setAccessible(true);
            s = s +"\'" + field.get(t)+"\'";
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(s);

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(s);
            statement.execute();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "dao/AbstractDAO: " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Metoda va fi folosita pentru a actualiza un rand selectat din interfata grafica.
     * @param t
     * @param id
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    public T update(T t, int id) throws ClassNotFoundException, IllegalAccessException {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String s="UPDATE  order_management." + type.getSimpleName().toLowerCase() + " SET ";
        Class cls2 = Class.forName("sample.model." + type.getSimpleName());
        Field[] fields = cls2.getDeclaredFields();
        int i=0;
        while(i < fields.length) {

            fields[i].setAccessible(true);
            if(i<fields.length-1)
                s = s + "`" + fields[i].getName() + "` = '" +fields[i].get(t)+ "', ";
            else
                s = s + "`" + fields[i].getName() + "` = '" +fields[i].get(t)+ "'";

            i++;
        }
        s = s + " WHERE (`id` = '" + id + "'); ";

        try {
            System.out.println(s);

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(s);

            statement.execute();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "dao/AbstractDAO: " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

}
