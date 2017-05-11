package sample.Objects;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Admin on 05.04.2017.
 */
public class VideoFilm {
    private SimpleStringProperty name;
    private SimpleStringProperty genre;
    private SimpleStringProperty year;
    private int ID;


    public VideoFilm(int id, String name, String genre, String year){
        this.ID = id;
        this.name = new SimpleStringProperty(name);
        this.genre = new SimpleStringProperty(genre);
        this.year = new SimpleStringProperty(year);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String number) {
        this.genre.set(number);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
    public SimpleStringProperty genreProperty() {
        return genre;
    }
    public SimpleStringProperty yearProperty() {
        return year;
    }

    public int getID(){return ID;}
    public void setID(int id){this.ID = id;}
    public String getYear(){return year.get();}

    public String toBDString() {
        return  "(\"" + name.get() + "\", \"" + genre.get() + "\", " + year.get() + ")";
    }

    public void setYear(String year) {
        this.year.set(year);
    }
}
