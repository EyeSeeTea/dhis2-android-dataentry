package org.hisp.dhis.android.dataentry.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;


public class SearchFragment extends Fragment implements SearchView {

    private static final String ARG_FORM_NAME = "arg:formName";
    private static final String ARG_SEARCH_QUERY = "arg:searchQuery";

    private Unbinder unbinder;

    @BindView(R.id.search_recyclerview)
    RecyclerView recyclerViewSearchResults;

    private SearchResultAdapter searchResultAdapter;

    @Inject
    SearchPresenter searchPresenter;


    static SearchFragment create(String formName, String searchQuery) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();

        args.putString(ARG_FORM_NAME, formName);
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        searchFragment.setArguments(args);

        return searchFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // inject dependencies
        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new SearchModule(getSearchQuery(), getFormName()))
                .inject(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        searchPresenter.onDetach();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchPresenter.onAttach(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        searchResultAdapter = new SearchResultAdapter(getContext());
        recyclerViewSearchResults.setLayoutManager(layoutManager);
        recyclerViewSearchResults.setAdapter(searchResultAdapter);
    }

    private String getFormName() {
        return getArguments().getString(ARG_FORM_NAME, "");
    }

    private String getSearchQuery() {
        return getArguments().getString(ARG_SEARCH_QUERY, "");
    }

    @NonNull
    @Override
    public Consumer<List<SearchResultViewModel>> renderViewModels() {
        return searchResultViewModels -> searchResultAdapter.swapData(searchResultViewModels);
    }
}
