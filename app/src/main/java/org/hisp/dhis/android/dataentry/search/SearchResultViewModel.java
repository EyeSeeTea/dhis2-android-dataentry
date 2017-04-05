package org.hisp.dhis.android.dataentry.search;

import android.support.annotation.NonNull;
import android.text.Spanned;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SearchResultViewModel {

    @NonNull
    public abstract String id();

    @NonNull
    public abstract Spanned label();

    @NonNull
    public abstract String searchQuery();

    @NonNull
    public static SearchResultViewModel create(@NonNull String id,
                                               @NonNull Spanned label,
                                               @NonNull String searchQuery) {
        return new AutoValue_SearchResultViewModel(id, label, searchQuery);
    }
}
