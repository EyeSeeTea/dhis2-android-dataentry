package org.hisp.dhis.android.dataentry.dashboard;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import rx.schedulers.Schedulers;

class DashboardRepositoryIntegrationTest {

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    DashboardRepository dashboardRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();
        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "org_unit_uid");
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "program_uid");
        program.put(ProgramModel.Columns.DISPLAY_NAME, "Program");
        db.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, "program_stage_uid");
        programStage.put(ProgramStageModel.Columns.DISPLAY_NAME, "Program Stage 1");
        programStage.put(ProgramStageModel.Columns.SORT_ORDER, "1");
        programStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        db.insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues secondStage = new ContentValues();
        secondStage.put(ProgramStageModel.Columns.UID, "program_stage_uid_2");
        secondStage.put(ProgramStageModel.Columns.DISPLAY_NAME, "Program Stage 2");
        secondStage.put(ProgramStageModel.Columns.SORT_ORDER, "2");
        secondStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        db.insert(ProgramStageModel.TABLE, null, secondStage);

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.UID, "tracked_entity_uid");
        db.insert(TrackedEntityModel.TABLE, null, trackedEntity);

        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.UID, "tei_uid");
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.TRACKED_ENTITY, "tracked_entity_uid");
        db.insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);

        ContentValues enrollment = new ContentValues();
        enrollment.put(EnrollmentModel.Columns.UID, "enrollment_uid");
        enrollment.put(EnrollmentModel.Columns.DATE_OF_ENROLLMENT, "2016-05-11");
        enrollment.put(EnrollmentModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        enrollment.put(EnrollmentModel.Columns.PROGRAM, "program_uid");
        enrollment.put(EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE, "tei_uid");
        db.insert(EnrollmentModel.TABLE, null, enrollment);

        ContentValues event = new ContentValues();
        event.put(EventModel.Columns.UID, "event_uid");
        event.put(EventModel.Columns.PROGRAM, "program_uid");
        event.put(EventModel.Columns.PROGRAM_STAGE, "program_stage_uid");
        event.put(EventModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        event.put(EventModel.Columns.ENROLLMENT_UID, "enrollment_uid");
        event.put(EventModel.Columns.EVENT_DATE, "1999-12-31");
        event.put(EventModel.Columns.STATUS, EventStatus.ACTIVE.name());
        db.insert(EventModel.TABLE, null, event);

        ContentValues secondEvent = new ContentValues();
        secondEvent.put(EventModel.Columns.UID, "event_uid_2");
        secondEvent.put(EventModel.Columns.PROGRAM, "program_uid");
        secondEvent.put(EventModel.Columns.PROGRAM_STAGE, "program_stage_uid_2");
        secondEvent.put(EventModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        secondEvent.put(EventModel.Columns.ENROLLMENT_UID, "enrollment_uid");
        secondEvent.put(EventModel.Columns.EVENT_DATE, "2001-12-31");
        secondEvent.put(EventModel.Columns.STATUS, EventStatus.SKIPPED.name());
        db.insert(EventModel.TABLE, null, secondEvent);

        dashboardRepository = new DashboardRepositoryImpl(databaseRule.briteDatabase());

    }

    @Test
    public void events() throws Exception {
        dashboardRepository.events("enrollment_uid");
    }

}