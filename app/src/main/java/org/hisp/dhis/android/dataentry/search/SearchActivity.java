package org.hisp.dhis.android.dataentry.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.reports.ReportsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchActivity extends AppCompatActivity {
    static final String ARG_FORM_NAME = "arg:formName";
    static final String ARG_SEARCH_QUERY = "arg:searchQuery";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static Intent create(@NonNull Activity activity, @NonNull String formName, @NonNull String searchQuery) {
        Intent intent = new Intent(activity, ReportsActivity.class);
        intent.putExtra(ARG_FORM_NAME, formName);
        intent.putExtra(ARG_SEARCH_QUERY, searchQuery);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        ButterKnife.bind(this);

        setUpToolbar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, SearchFragment.create(getFormName(), getSearchQuery()))
                .commitNow();
    }

    private void setUpToolbar() {
        toolbar.setTitle(getFormName());
    }

    private String getSearchQuery() {
        return getIntent().getExtras().getString(ARG_SEARCH_QUERY, "");
    }

    private String getFormName() {
        return getIntent().getExtras().getString(ARG_FORM_NAME, "");
    }

}
