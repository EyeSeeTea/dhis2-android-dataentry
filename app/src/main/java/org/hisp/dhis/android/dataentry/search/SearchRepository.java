package org.hisp.dhis.android.dataentry.search;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Flowable;

interface SearchRepository {

    @NonNull
    Flowable<List<SearchResultViewModel>> search();
}
