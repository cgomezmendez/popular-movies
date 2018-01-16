package me.cristiangomez.popularmovies.movie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.cristiangomez.popularmovies.BaseFragment;
import me.cristiangomez.popularmovies.R;
import me.cristiangomez.popularmovies.data.pojo.Cast;
import me.cristiangomez.popularmovies.data.pojo.Movie;
import me.cristiangomez.popularmovies.data.pojo.MovieReview;
import me.cristiangomez.popularmovies.data.pojo.MovieVideo;
import me.cristiangomez.popularmovies.data.pojo.Photo;
import me.cristiangomez.popularmovies.movie.movieheader.MovieHeaderView;
import me.cristiangomez.popularmovies.network.responses.MovieImage;
import me.cristiangomez.popularmovies.photoviewer.PhotoViewerActivity;
import me.cristiangomez.popularmovies.util.Constants;
import me.cristiangomez.popularmovies.util.DataError;
import me.cristiangomez.popularmovies.util.Utils;

public class MovieFragment extends BaseFragment implements MovieContract.View {
    //    @BindView(R.id.tv_movie_duration)
//    TextView mMovieDurationTv;
//    @BindView(R.id.tv_movie_rating)
    TextView mMovieRatingTv;
    private Unbinder mUnbinder;
    private Picasso mPicasso;
    private MovieContract.Presenter mPresenter;
    private Snackbar mErrorSnb;
    @BindView(R.id.vp_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.mh_header)
    MovieHeaderView mMovieHeaderView;
    @BindView(R.id.sliding_tabs)
    TabLayout mTabsTl;
    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    MovieDetailViewPagerAdapter mViewPagerAdapter;
    private ShareActionProvider mShareActionProvider;
    private Uri movieUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        mScrollView.setFillViewport(true);
        mViewPagerAdapter = new MovieDetailViewPagerAdapter(getContext());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabsTl.setupWithViewPager(mViewPager);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

//    @OnClick(R.id.fl_movie_poster_container)
//    public void onMoviePosterClick() {
//        mPresenter.onMoviePosterClick();
//    }

    public void showMoviePosterViewer(Photo photo) {
        Intent intent = new Intent(getContext(), PhotoViewerActivity.class);
        intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO, photo);
        startActivity(intent);
    }

    @Override
    public void onMovie(Movie movie) {
        if (movie != null) {
            movieUri = Utils.getTheMovieDbUri(movie.getId(),
                    movie.getTitle());
            mMovieHeaderView.bind(movie);
            if (mViewPagerAdapter != null) {
                mViewPagerAdapter.getMovieOverview().bind(movie);
            }
//            if (movie.getRuntime() != 0) {
//                mMovieDurationTv.setText(getString(R.string.movie_runtime, movie.getRuntime()));
//            }
//            mMovieRatingTv.setText(getString(R.string.movie_rating, movie.getVoteAverage()));
//            mMoviePoster.setContentDescription(getString(R.string.content_description_movie_poster,
//                    movie.getTitle()));
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat
                .getActionProvider(shareItem);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, movieUri.toString());
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_menu, menu);
    }

    public void setPresenter(MovieContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onError(DataError dataError) {
        dismissError();
        String errorString = null;
        switch (dataError) {
            case NETWORK_NOT_AVAILABLE:
                errorString = getString(R.string.error_network_not_available);
                break;
            case INVALID_API_KEY:
                errorString = getString(R.string.error_invalid_api_key);
        }
        mErrorSnb = Snackbar.make(getView(), errorString,
                Snackbar.LENGTH_INDEFINITE);
        mErrorSnb.setAction(R.string.error_network_not_available_action_retry,
                v -> {
                    if (mPresenter != null) {
                        mPresenter.retryMovieLoad();
                    }
                });
        mErrorSnb.show();
    }

    @Override
    public void dismissError() {
        if (mErrorSnb != null) {
            mErrorSnb.dismiss();
            mErrorSnb = null;
        }
    }

    @Override
    public void onMovieImages(List<MovieImage> movieImages) {
        mViewPagerAdapter.getMovieOverview().bindMovieImages(movieImages);
    }

    @Override
    public void onMovieCast(List<Cast> casts) {
        mViewPagerAdapter.getMovieOverview().bindCast(casts);
    }

    @Override
    public void onMovieVideos(List<MovieVideo> movieVideos) {
        mViewPagerAdapter.getMovieOverview().bindMovieVideos(movieVideos);
    }

    @Override
    public void onMovieRecommendations(List<Movie> movies) {
        mViewPagerAdapter.getMovieOverview().binMovieRecomendations(movies);
    }

    @Override
    public void onMovieReviews(List<MovieReview> movieReviews) {
        mViewPagerAdapter.getMovieReviews().bind(movieReviews);
    }

    @Override
    public void showMovieImagePhotoViewer(MovieImage movieImage) {
        Intent intent = new Intent(getActivity(), PhotoViewerActivity.class);
        intent.putExtra(PhotoViewerActivity.EXTRA_PHOTO,
                new Photo(Utils.getImageUri(movieImage.getFilePath(),
                        Constants.IMAGE_SIZE_PHOTO_VIEWER)));
        getActivity().startActivity(intent);
    }
}
