/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.dataentry.drawerform.form;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.views.FontTextInputEditText;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hisp.dhis.android.dataentry.drawerform.form.FormEntityEditText.LONG_TEXT_LINE_COUNT;
import static org.hisp.dhis.android.dataentry.drawerform.form.FormEntityEditText.SHORT_TEXT_LINE_COUNT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EditTextRowViewTests {

    private final String id = "test_id";
    private final String label = "test_label";

    private EditTextRowView editTextRowView;
    @Mock
    private TextInputLayout textInputLayout;
    @Mock
    private FontTextInputEditText editText;
    @Mock
    private TextView textViewLabel;
    @Mock
    private Context context;

    private RecyclerView.ViewHolder viewHolder;

    @SuppressLint("InflateParams")
    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        editTextRowView = new EditTextRowView();

        LayoutInflater inflater = mock(LayoutInflater.class);
        View itemView = mock(View.class);

        when(inflater.inflate(
                R.layout.recyclerview_row_edittext, null, false)).thenReturn(itemView);

        when(itemView.findViewById(R.id.edittext_row_textinputlayout)).thenReturn(textInputLayout);
        when(itemView.findViewById(R.id.edittext_row_edittext)).thenReturn(editText);
        when(itemView.findViewById(R.id.textview_row_label)).thenReturn(textViewLabel);
        when(itemView.getContext()).thenReturn(context);

        viewHolder = editTextRowView.onCreateViewHolder(inflater, null);
    }

    @Test
    public void onBindViewHolder_setValueAndLabel() throws Exception {
        FormEntityEditText formEntity = new FormEntityShortEditText(id, label);
        String value = "test_value";
        formEntity.setValue(value);

        editTextRowView.onBindViewHolder(viewHolder, formEntity);
        verify(textViewLabel).setText(label);
        verify(editText).setText(value);
    }

    @Test
    public void onBindViewHolder_configureShortEditText() throws Exception {
        FormEntityEditText formEntity = new FormEntityShortEditText(id, label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(editText).setInputType(InputType.TYPE_CLASS_TEXT);
        verify(editText).setMaxLines(SHORT_TEXT_LINE_COUNT);
    }

    @Test
    public void onBindViewHolder_configureLongEditText() throws Exception {
        FormEntityEditText formEntity = new FormEntityLongEditText(id, label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(editText).setInputType(InputType.TYPE_CLASS_TEXT);
        verify(editText).setMaxLines(LONG_TEXT_LINE_COUNT);
    }

    @Test
    public void onBindViewHolder_configureNumberEditText() throws Exception {
        FormEntityEditText formEntity = new FormEntityNumberEditText(id, label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(editText).setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        verify(editText).setMaxLines(SHORT_TEXT_LINE_COUNT);
    }

    @Test
    public void onBindViewHolder_configureIntegerEditText() throws Exception {
        FormEntityEditText formEntity = new FormEntityIntegerEditText(id, label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(editText).setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        verify(editText).setMaxLines(SHORT_TEXT_LINE_COUNT);
    }

    @Test
    public void onBindViewHolder_configureIntegerPositiveEditText() throws Exception {
        FormEntityEditText formEntity = new FormEntityIntegerPositiveEditText(id, label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(editText).setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        verify(editText).setMaxLines(SHORT_TEXT_LINE_COUNT);
    }

    @Test
    public void onBindViewHolder_configureIntegerNegativeEditText() throws Exception {
        FormEntityEditText formEntity = new FormEntityIntegerNegativeEditText(id, label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(editText).setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        verify(editText).setMaxLines(SHORT_TEXT_LINE_COUNT);
    }

    @Test
    public void onBindViewHolder_configureIntegerZeroOrPositiveEditText() throws Exception {
        FormEntityEditText formEntity = new FormEntityIntegerZeroOrPositiveEditText(id, label);
        editTextRowView.onBindViewHolder(viewHolder, formEntity);

        verify(editText).setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        verify(editText).setMaxLines(SHORT_TEXT_LINE_COUNT);
    }
}