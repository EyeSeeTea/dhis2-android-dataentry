package org.hisp.dhis.android.dataentry.search;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@PerActivity
@Module
public class SearchModule {
    private final String formUid;
    private final String searchQuery;

    public SearchModule(String formUid, String searchQuery) {
        this.formUid = formUid;
        this.searchQuery = searchQuery;
    }

    @Provides
    @PerActivity
    SearchRepository searchRepository(BriteDatabase briteDatabase) {
        return new SearchRepositoryImpl(briteDatabase, searchQuery, formUid);
    }

    @Provides
    @PerActivity
    SearchPresenter searchPresenter(SearchRepository searchRepository, SchedulerProvider schedulerProvider) {
        return new SearchPresenterImpl(schedulerProvider, searchRepository);
    }
}
