package me.cristiangomez.popularmovies.movie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.cristiangomez.popularmovies.BaseFragment;
import me.cristiangomez.popularmovies.R;
import me.cristiangomez.popularmovies.data.pojo.Movie;
import me.cristiangomez.popularmovies.data.pojo.Photo;
import me.cristiangomez.popularmovies.photoviewer.PhotoViewerActivity;

public class MovieFragment extends BaseFragment implements MovieContract.View {
    @BindView(R.id.tv_movie_title)
    TextView mMovieTitleTv;
    @BindView(R.id.tv_movie_release_year)
    TextView mMovieReleaseYearTv;
    @BindView(R.id.tv_movie_duration)
    TextView mMovieDurationTv;
    @BindView(R.id.tv_movie_rating)
    TextView mMovieRatingTv;
    @BindView(R.id.tv_movie_plot)
    TextView mMoviePlotTv;
    @BindView(R.id.iv_movie_poster)
    ImageView mMoviePoster;
    private Unbinder mUnbinder;
    private Picasso mPicasso;
    private MovieContract.Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPicasso = Picasso.with(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @OnClick(R.id.fl_movie_poster_container)
    public void onMoviePosterClick() {
        mPresenter.onMoviePosterClick();
    }

    public void showMoviePosterViewer(Photo photo) {
        Intent intent = new Intent(getContext(), PhotoViewerActivity.class);
        intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO, photo);
        startActivity(intent);
    }

    @Override
    public void onMovie(Movie movie) {
        if (movie != null) {
            mMovieTitleTv.setText(movie.getTitle());
            mMovieReleaseYearTv.setText(String.valueOf(movie.getReleaseYear()));
            mMovieDurationTv.setText(movie.getDuration());
            mMovieRatingTv.setText(movie.getRating());
            mMoviePlotTv.setText(movie.getPlot());
            mPicasso.load(movie.getPosterPath())
                    .fit()
                    .centerCrop()
                    .into(mMoviePoster);
        }
    }

    public void setPresenter(MovieContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}