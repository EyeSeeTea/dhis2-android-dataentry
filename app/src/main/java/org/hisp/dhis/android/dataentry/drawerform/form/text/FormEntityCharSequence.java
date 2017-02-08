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

package org.hisp.dhis.android.dataentry.drawerform.form.text;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.drawerform.form.common.FormEntity;
import org.hisp.dhis.android.dataentry.drawerform.form.common.OnFormEntityChangeListener;

public abstract class FormEntityCharSequence extends FormEntity {
    private static final String EMPTY_STRING = "";

    @Nullable
    private OnFormEntityChangeListener onFormEntityChangeListener;

    @NonNull
    private CharSequence value;

    public FormEntityCharSequence(String id, String label) {
        this(id, label, null);
    }

    public FormEntityCharSequence(String id, String label, Object tag) {
        super(id, label, tag);

        this.value = EMPTY_STRING;
    }

    @Nullable
    public OnFormEntityChangeListener getOnFormEntityChangeListener() {
        return onFormEntityChangeListener;
    }

    public void setOnFormEntityChangeListener(@Nullable OnFormEntityChangeListener listener) {
        this.onFormEntityChangeListener = listener;
    }

    @NonNull
    public CharSequence getValue() {
        return value;
    }

    public void setValue(@Nullable CharSequence value, boolean notifyListeners) {
        CharSequence newValue = value;

        // we need to make sure that we never nullify value
        if (newValue == null) {
            newValue = EMPTY_STRING;
        }

        if (!this.value.equals(newValue)) {
            this.value = newValue;

            if (onFormEntityChangeListener != null && notifyListeners) {
                this.onFormEntityChangeListener.onFormEntityChanged(this);
            }
        }
    }

    public void setValue(@Nullable CharSequence value) {
        setValue(value, false);
    }
}
