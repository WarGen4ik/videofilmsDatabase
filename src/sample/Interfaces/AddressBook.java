package sample.Interfaces;

import sample.Objects.VideoFilm;

/**
 * Created by Илья on 13.03.2017.
 */
public interface AddressBook {
    void add(VideoFilm person);

    void change(VideoFilm person);

    void delete(VideoFilm person);
}
