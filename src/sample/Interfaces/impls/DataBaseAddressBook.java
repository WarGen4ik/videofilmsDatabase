package sample.Interfaces.impls;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Interfaces.AddressBook;
import sample.Objects.VideoFilm;

import java.sql.*;

/**
 * Created by Илья on 26.03.2017.
 */
public class DataBaseAddressBook implements AddressBook {
    @Override
    public void add(VideoFilm videoFilm) {
        String insertTableSQL = "insert into videofilms.videofilms"
                + " (Name, Genre, Year) " + "values "
                + videoFilm.toBDString();

        Statement statement = null;

        getDBConnection();
        try{
        statement = connection.createStatement();

        // выполнить SQL запрос
        statement.executeUpdate(insertTableSQL);
        videoFilm.setID(++MAX_ID);
        backupList.add(videoFilm); list.add(videoFilm);

    } catch (SQLException e) {
        System.out.println(e.getMessage());
        System.out.println("Не удалось добавить поле");
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void change(VideoFilm videoFilm) {
        String updateTableSQL = "UPDATE videofilms.videofilms SET Name = \"" + videoFilm.getName()
        + "\", Genre = \"" + videoFilm.getGenre() + "\", Year = " + videoFilm.getYear() + " WHERE id = " + videoFilm.getID();

        Statement statement = null;
        try {
            getDBConnection();
            statement = connection.createStatement();

            // выполняем запрос delete SQL
            statement.executeUpdate(updateTableSQL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void delete(VideoFilm videoFilm) {
        String deleteTableSQL = "DELETE FROM videofilms.videofilms WHERE id = " + videoFilm.getID();
        Statement statement = null;
        try {
            getDBConnection();
            statement = connection.createStatement();

            // выполняем запрос delete SQL
            statement.executeUpdate(deleteTableSQL);
            backupList.remove(videoFilm); list.remove(videoFilm);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }

    }

    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final String URL = "jdbc:mysql://localhost:3306/mysql?useSSL=false";
    public static int MAX_ID = 0;
    private static Connection connection = null;
    ObservableList<VideoFilm> list = FXCollections.observableArrayList();
    ObservableList<VideoFilm> backupList = FXCollections.observableArrayList();


    public DataBaseAddressBook(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println("Ошибка с поиском jdbc");
            e.printStackTrace();
            return;
        }

        Statement statement = null;

        try {
            getDBConnection();
            statement = connection.createStatement();
            String selectTableSQL = "SELECT * FROM videofilms.videofilms";

            // выбираем данные с БД
            ResultSet rs = statement.executeQuery(selectTableSQL);

            // И если что то было получено то цикл while сработает
            while (rs.next()) {
                VideoFilm videoFilm = new VideoFilm(rs.getInt("id"),
                        rs.getString("Name"),
                        rs.getString("Genre"),
                        Integer.toString(rs.getInt("Year")));
                if (MAX_ID < rs.getInt("id"))
                    MAX_ID = rs.getInt("id");

                list.add(videoFilm);
                backupList.add(videoFilm);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Не вижу данных!");
        }
    }

    public static void getDBConnection(){
        try{
            connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        }
        catch (SQLException e){
            System.out.println("Не конектится");
            e.printStackTrace();
            return;
        }
    }

    public ObservableList<VideoFilm> getList(){
        return list;
    }

    public void getListFromNameOrGenreOrYear(String text){
        list.clear();

        for (VideoFilm videoFilm : backupList){
            if (videoFilm.getName().toLowerCase().contains(text.toLowerCase()) ||
                    videoFilm.getGenre().toLowerCase().contains(text.toLowerCase()) ||
                    videoFilm.getYear().toLowerCase().contains(text.toLowerCase()))
                list.add(videoFilm);
        }
    }
}
