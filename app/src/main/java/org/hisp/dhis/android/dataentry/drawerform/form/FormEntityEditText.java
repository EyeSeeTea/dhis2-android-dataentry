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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.InputType;

public abstract class FormEntityEditText extends FormEntityCharSequence {

    /* number of lines for TEXT */
    public static final int SHORT_TEXT_LINE_COUNT = 1;

    /* number of lines for LONG_TEXT */
    public static final int LONG_TEXT_LINE_COUNT = 3;

    private final String hint;

    FormEntityEditText(String id, String label, String hint, Object tag) {
        super(id, label, tag);
        this.hint = hint;
    }

    FormEntityEditText(String id, String label) {
        this(id, label, null, null);
    }

    FormEntityEditText(String id, String label, Object tag) {
        this(id, label, null, tag);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.EDITTEXT;
    }

    @Nullable
    public String getHint() {
        return hint;
    }

    public int getAndroidInputType() {
        return InputType.TYPE_CLASS_TEXT;
    }

    public int getMaxLines() {
        return SHORT_TEXT_LINE_COUNT;
    }

    /**
     * This is used for precaching hint texts in the ViewHolder in case one is not provided
     */
    @StringRes
    public abstract int getHintResourceId();

}
