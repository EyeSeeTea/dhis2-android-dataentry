package org.hisp.dhis.android.dataentry.search;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.utils.StringUtils;

import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

public class SearchRepositoryImpl implements SearchRepository {

    private static final String SEARCH_TRACKED_ENTITY_INSTANCES = "SELECT " +
            "  TrackedEntityInstance.uid," +
            "  InstanceAttribute.label," +
            "  TrackedEntityAttributeValue.value" +
            "FROM (TrackedEntityInstance" +
            "  INNER JOIN Program ON Program.trackedEntity = TrackedEntityInstance.trackedEntity)" +
            "  LEFT OUTER JOIN (" +
            "                    SELECT " +
            "                      TrackedEntityAttribute.uid                  AS tea," +
            "                      TrackedEntityAttribute.displayName          AS label," +
            "                      ProgramTrackedEntityAttribute.displayInList AS showInList," +
            "                      ProgramTrackedEntityAttribute.program       AS program" +
            "                    FROM ProgramTrackedEntityAttribute" +
            "                      INNER JOIN TrackedEntityAttribute" +
            "                        ON " +
            "                       TrackedEntityAttribute.uid = ProgramTrackedEntityAttribute.trackedEntityAttribute" +
            "                  ) AS InstanceAttribute ON InstanceAttribute.program = Program.uid " +
            "  LEFT OUTER JOIN TrackedEntityAttributeValue " +
            "    ON (TrackedEntityAttributeValue.trackedEntityAttribute = InstanceAttribute.tea " +
            "        AND TrackedEntityAttributeValue.trackedEntityInstance = TrackedEntityInstance.uid) " +
            "WHERE TrackedEntityInstance.trackedEntity = ? AND NOT TrackedEntityInstance.state = 'TO_DELETE' " +
            "      AND TrackedEntityAttributeValue.value LIKE 'first_value' " +
            "ORDER BY datetime(TrackedEntityInstance.created) " +
            "  DESC, " +
            "  TrackedEntityInstance.uid " +
            "  ASC;";
    @NonNull
    private final String searchQuery;

    @NonNull
    private final String formUid;

    @NonNull
    private final BriteDatabase briteDatabase;

    public SearchRepositoryImpl(@NonNull BriteDatabase briteDatabase,
                                @NonNull String searchQuery,
                                @NonNull String formUid) {
        this.briteDatabase = briteDatabase;
        this.searchQuery = searchQuery;
        this.formUid = formUid;
    }

    @NonNull
    @Override
    public Flowable<List<SearchResultViewModel>> search() {

        return toV2Flowable(briteDatabase.createQuery(TrackedEntityInstanceModel.TABLE,
                SEARCH_TRACKED_ENTITY_INSTANCES,
                formUid,
                searchQuery)
                .mapToList(this::mapToPairs))
                .switchMap(rows -> Flowable.fromIterable(rows)
                        .groupBy(Pair::val0, Pair::val1)
                        .concatMap(group -> group.toList().toFlowable()
                                .map(values ->
                                        SearchResultViewModel.create(
                                                group.getKey(),
                                                StringUtils.htmlify(values),
                                                searchQuery)))
                        .toList().toFlowable());
    }

    private Pair<String, String> mapToPairs(@NonNull Cursor cursor) {
        return Pair.create(cursor.getString(0),
                String.format(Locale.US, "%s: <strong>%s</strong>", cursor.getString(1), cursor.getString(2)));
    }
}
