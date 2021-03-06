package me.cristiangomez.popularmovies.movies;

import java.util.List;

import me.cristiangomez.popularmovies.BasePresenter;
import me.cristiangomez.popularmovies.BasePresenterState;
import me.cristiangomez.popularmovies.BaseView;
import me.cristiangomez.popularmovies.data.pojo.Movie;
import me.cristiangomez.popularmovies.util.DataError;

public interface MoviesContract {
    interface View extends BaseView {
        void onMovies(List<Movie> movies);

        void onError(DataError networkNotAvailable);

        void showLoading();

        void dismissError();

        void onMovieClick(Movie movie);
    }

    interface PresenterState extends BasePresenterState {
        List<Movie> getMovies();
        MovieSortOption getMovieSortOption();
    }

    interface Presenter extends BasePresenter<View, PresenterState> {
        void onSortChanged(MovieSortOption movieSortOption);

        void retryMoviesLoad();
    }
}
