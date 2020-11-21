package database;

import entertainment.Genre;

import java.util.HashMap;

/**
 * All the genres are mapped to the total number of views (0 views initially).
 */
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
