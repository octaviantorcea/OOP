package database;

import entertainment.Genre;

import java.util.HashMap;

public final class GenreDatabase {
    private final HashMap<Genre, Integer> genreDatabase = new HashMap<>();

    public GenreDatabase() {
        for (Genre genre : Genre.values()) {
            genreDatabase.put(genre, 0);
        }
    }

    public HashMap<Genre, Integer> getGenreDatabase() {
        return genreDatabase;
    }
}
