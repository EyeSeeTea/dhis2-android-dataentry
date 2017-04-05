package org.hisp.dhis.android.dataentry.search;/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final List<SearchResultViewModel> searchResultViewModels;

    SearchResultAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.searchResultViewModels = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.recyclerview_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchResultViewModel item = searchResultViewModels.get(position);
        holder.update(item);

    }

    void swapData(@NonNull List<SearchResultViewModel> reports) {
        searchResultViewModels.clear();
        searchResultViewModels.addAll(reports);

        // ToDo: improve performance of RecyclerView
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return searchResultViewModels.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textview_report_value_labels)
        TextView textViewValues;

        //TODO Bind views
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void update(@NonNull SearchResultViewModel searchResultViewModel) {
            SpannableString spannableString = highlightSearchResult(searchResultViewModel);

            textViewValues.setText(spannableString);
        }

        @NonNull
        private SpannableString highlightSearchResult(@NonNull SearchResultViewModel searchResultViewModel) {
            SpannableString spannableString = new SpannableString(searchResultViewModel.label());
            BackgroundColorSpan[] backgroundColorSpens = spannableString.getSpans(
                    0, spannableString.length(), BackgroundColorSpan.class
            );

            for (BackgroundColorSpan backgroundColorSpen : backgroundColorSpens) {
                spannableString.removeSpan(backgroundColorSpen);
            }

            int indexOfSearchQuery = spannableString.toString().indexOf(searchResultViewModel.searchQuery());

            while (indexOfSearchQuery > 0) {
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), indexOfSearchQuery,
                        indexOfSearchQuery + searchResultViewModel.searchQuery().length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                indexOfSearchQuery = spannableString.toString().indexOf(
                        searchResultViewModel.searchQuery(),
                        indexOfSearchQuery + searchResultViewModel.searchQuery().length()
                );
            }
            return spannableString;
        }
    }
}