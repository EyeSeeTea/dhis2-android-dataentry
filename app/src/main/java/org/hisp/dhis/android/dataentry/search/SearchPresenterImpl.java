package org.hisp.dhis.android.dataentry.search;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

final class SearchPresenterImpl implements SearchPresenter {

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final SearchRepository repository;

    @NonNull
    private final CompositeDisposable compositeDisposable;

    SearchPresenterImpl(@NonNull SchedulerProvider schedulerProvider,
                        @NonNull SearchRepository repository) {
        this.schedulerProvider = schedulerProvider;
        this.repository = repository;
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onAttach(@NonNull View view) {
        if (view instanceof SearchView) {
            SearchView searchView = (SearchView) view;

            compositeDisposable.add(repository.search()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(searchView.renderViewModels(), Timber::e));
        }
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }
}
