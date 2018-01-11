package me.cristiangomez.popularmovies.movies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.cristiangomez.popularmovies.data.MoviesRepository;
import me.cristiangomez.popularmovies.data.pojo.Movie;
import me.cristiangomez.popularmovies.util.Constants;
import me.cristiangomez.popularmovies.util.DataError;

public class MoviesPresenter implements MoviesContract.Presenter {
    private MoviesRepository mMoviesRepository;
    private MoviesContract.View mView;
    private MovieSortOption mMoviesSortOption = Constants.DEFAULT_MOVIE_SORT;
    private List<Movie> mMovies;
    private Scheduler mObserverScheduler;
    private Scheduler mSubscriberScheduler;

    public MoviesPresenter(MoviesRepository mMoviesRepository, Scheduler mObserverScheduler, Scheduler mSubscriberScheduler) {
        this.mMoviesRepository = mMoviesRepository;
        this.mObserverScheduler = mObserverScheduler;
        this.mSubscriberScheduler = mSubscriberScheduler;
    }

    public MoviesPresenter(MoviesRepository mMoviesRepository) {
        this.mMoviesRepository = mMoviesRepository;
        mObserverScheduler = AndroidSchedulers.mainThread();
        mSubscriberScheduler = Schedulers.io();
    }

    @Override
    public void takeView(MoviesContract.View view) {
        this.mView = view;
        if (mMovies == null) {
            loadMovies();
        } else {
            mView.onMovies(mMovies);
        }
    }

    @Override
    public void retryMoviesLoad() {
        loadMovies();
    }

    @Override
    public void dropView() {
        mView = null;
    }

    @Override
    public void onSortChanged(MovieSortOption movieSortOption) {
        if (movieSortOption != mMoviesSortOption) {
            mMoviesSortOption = movieSortOption;
            loadMovies();
        }
    }

    private void loadMovies() {
        if (mView != null) {
            mView.showLoading();
            mView.dismissError();
        }
        mMoviesRepository.getMovies(mMoviesSortOption).subscribeOn(mSubscriberScheduler)
                .observeOn(mObserverScheduler)
                .doOnNext(movies -> {
                    if (mView != null) {
                        mView.onMovies(movies);
                    }
                })
                .doOnError(throwable -> {
                    if (throwable instanceof IOException) {
                        if (mView != null) {
                            mView.onError(DataError.NETWORK_NOT_AVAILABLE);
                        }
                    }
                })
                .onErrorReturn(throwable -> new ArrayList<>())
                .subscribe();
    }
}
