package org.hisp.dhis.android.dataentry.search;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.functions.Consumer;

public interface SearchView extends View {

    @NonNull
    @UiThread
    Consumer<List<SearchResultViewModel>> renderViewModels();
}
