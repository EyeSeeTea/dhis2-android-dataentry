/*
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

package org.hisp.dhis.android.dataentry.login;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.home.HomeActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

import static android.text.TextUtils.isEmpty;
import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;

@SuppressWarnings({"PMD.ExcessiveImports"}) // This activity needs a lot of android.* imports
public class LoginActivity extends AppCompatActivity implements LoginView {
    private static final String ARG_LOGIN_ACTIVITY_LAUNCH_MODE = "arg:launchMode";
    private static final String ARG_LAUNCH_MODE_LOGIN_USER = "mode:loginUser";
    private static final String ARG_LAUNCH_MODE_CONFIRM_USER = "mode:confirmUser";

    private static final String ARG_SERVER_URL = "arg:serverUrl";
    private static final String ARG_USERNAME = "arg:username";
    private static final String IS_LOADING = "state:isLoading";

    @BindView(R.id.progress_bar_circular)
    CircularProgressBar progressBar;

    @BindView(R.id.layout_login_views)
    ViewGroup loginViewsContainer;

    @BindView(R.id.edittext_server_url)
    EditText serverUrl;

    @BindView(R.id.edittext_username)
    EditText username;

    @BindView(R.id.edittext_password)
    EditText password;

    @BindView(R.id.button_log_in)
    Button loginButton;

    @BindView(R.id.button_log_out)
    Button logoutButton;

    @Inject
    LoginPresenter loginPresenter;

    // LayoutTransition (for JellyBean+ devices only)
    LayoutTransition layoutTransition;

    // Animations for pre-JellyBean devices
    Animation layoutTransitionSlideIn;
    Animation layoutTransitionSlideOut;

    // Action which should be executed after animation is finished
    OnPostAnimationRunnable onPostAnimationAction;

    /**
     * Creates intent for LoginActivity to be launched in "User confirmation" mode.
     *
     * @param currentActivity Activity from which we want to fire LoginActivity
     * @param target          Implementation of LoginActivity
     * @param serverUrl       ServerUrl which will be set to serverUrl address and locked
     */
    public static Intent createIntent(Activity currentActivity, Class<? extends Activity> target,
            String serverUrl, String username) {
        isNull(currentActivity, "Activity must not be null");
        isNull(target, "Target activity class must not be null");
        isNull(serverUrl, "ServerUrl must not be null");
        isNull(username, "Username must not be null");

        Intent intent = new Intent(currentActivity, target);
        intent.putExtra(ARG_LOGIN_ACTIVITY_LAUNCH_MODE, ARG_LAUNCH_MODE_CONFIRM_USER);
        intent.putExtra(ARG_SERVER_URL, serverUrl);
        intent.putExtra(ARG_USERNAME, username);

        return intent;
    }

    private static boolean isGreaterThanOrJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Configuring progress bar (setting width of 6dp)
        float progressBarStrokeWidth = getResources()
                .getDimensionPixelSize(R.dimen.progressbar_stroke_width);
        progressBar.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this)
                .color(ContextCompat.getColor(this, R.color.color_primary))
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .strokeWidth(progressBarStrokeWidth)
                .rotationSpeed(1f)
                .sweepSpeed(1f)
                .build());

        logoutButton.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {
            String launchMode = getIntent().getExtras().getString(
                    ARG_LOGIN_ACTIVITY_LAUNCH_MODE, ARG_LAUNCH_MODE_LOGIN_USER);

            if (ARG_LAUNCH_MODE_CONFIRM_USER.equals(launchMode)) {
                String predefinedServerUrl = getIntent().getExtras().getString(ARG_SERVER_URL);
                String predefinedUsername = getIntent().getExtras().getString(ARG_USERNAME);

                serverUrl.setText(predefinedServerUrl);
                serverUrl.setEnabled(false);

                username.setText(predefinedUsername);
                username.setEnabled(false);

                loginButton.setText(R.string.confirm_user);
                logoutButton.setVisibility(View.VISIBLE);
            }
        }

        // Callback which will be triggered when animations are finished
        OnPostAnimationListener onPostAnimationListener = new OnPostAnimationListener();

        /* adding transition animations to root layout */
        if (isGreaterThanOrJellyBean()) {
            setLayoutTransitionOnJellyBeanAndGreater(onPostAnimationListener);
        } else {
            layoutTransitionSlideIn = AnimationUtils.loadAnimation(this, R.anim.in_up);
            layoutTransitionSlideOut = AnimationUtils.loadAnimation(this, R.anim.out_down);

            layoutTransitionSlideIn.setAnimationListener(onPostAnimationListener);
            layoutTransitionSlideOut.setAnimationListener(onPostAnimationListener);
        }

        hideProgress();
        onTextChanged();
    }

    @OnTextChanged(callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED, value = {
            R.id.edittext_server_url, R.id.edittext_username, R.id.edittext_password
    })
    public void onTextChanged() {
        loginButton.setEnabled(!isEmpty(serverUrl.getText()) &&
                !isEmpty(username.getText()) && !isEmpty(password.getText()));
    }

    @OnClick(value = {
            R.id.button_log_in, R.id.button_log_out
    })
    public void onButtonClicked(View view) {
        if (view.getId() == R.id.button_log_in) {
            ((DhisApp) getApplicationContext()).appComponent()
                    .plus(new LoginModule()).inject(this);

            loginPresenter.onAttach(this);
            loginPresenter.validateCredentials(serverUrl.getText().toString(),
                    username.getText().toString(), password.getText().toString());
        }
//        else if (view.getId() == R.id.button_log_out) {
//            // ToDo: log-out logic
//        }
    }

    /*
    * @RequiresApi annotation needed to pass lint checks run outside of Android Studio
    * */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setLayoutTransitionOnJellyBeanAndGreater(OnPostAnimationListener animationListener) {
        layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.addTransitionListener(animationListener);

        RelativeLayout loginLayoutContent = (RelativeLayout) findViewById(R.id.layout_content);
        loginLayoutContent.setLayoutTransition(layoutTransition);
    }

    @Override
    protected void onPause() {
        if (onPostAnimationAction != null) {
            onPostAnimationAction.run();
            onPostAnimationAction = null;
        }

        super.onPause();
    }

    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        if (onPostAnimationAction == null) {
            outState.putBoolean(IS_LOADING, progressBar.isShown());
        } else {
            outState.putBoolean(IS_LOADING, onPostAnimationAction.isProgressBarWillBeShown());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected final void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean(IS_LOADING, false)) {
            showProgress();
        } else {
            hideProgress();
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    public void navigateTo(final Class<? extends Activity> activityClass) {
        isNull(activityClass, "Target activity must not be null");

        Intent intent = new Intent(this, activityClass);
        ActivityCompat.startActivity(this, intent, null);
        overridePendingTransition(
                R.anim.activity_open_enter,
                R.anim.activity_open_exit);
        finish();
    }

    @Override
    public void showProgress() {
        if (layoutTransitionSlideOut != null) {
            loginViewsContainer.startAnimation(layoutTransitionSlideOut);
        }

        loginViewsContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if (layoutTransitionSlideIn != null) {
            loginViewsContainer.startAnimation(layoutTransitionSlideIn);
        }

        loginViewsContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showInvalidServerUrlError() {
        // stub
    }

    @Override
    public void showInvalidCredentialsError() {
        // stub
    }

    @Override
    public void showUnexpectedError() {
        // stub
    }

    @Override
    public void showServerError() {

    }

    @Override
    public void navigateToHome() {
        ActivityCompat.startActivity(this, HomeActivity.createIntent(this), null);
        finish();
    }

    private boolean isAnimationInProgress() {
        boolean layoutTransitionAnimationsInProgress =
                layoutTransition != null && layoutTransition.isRunning();
        boolean layoutTransitionAnimationSlideUpInProgress = layoutTransitionSlideIn != null &&
                layoutTransitionSlideIn.hasStarted() && !layoutTransitionSlideIn.hasEnded();
        boolean layoutTransitionAnimationSlideOutInProgress = layoutTransitionSlideOut != null &&
                layoutTransitionSlideOut.hasStarted() && !layoutTransitionSlideOut.hasEnded();

        return layoutTransitionAnimationsInProgress ||
                layoutTransitionAnimationSlideUpInProgress ||
                layoutTransitionAnimationSlideOutInProgress;
    }

    /**
     * Should be called in order to show progressbar.
     */
    protected final void onStartLoading() {
        if (isAnimationInProgress()) {
            onPostAnimationAction = new OnPostAnimationRunnable(null, this, true);
        } else {
            showProgress();
        }
    }

    protected final void onFinishLoading() {
        onFinishLoading(null);
    }

    /**
     * Should be called after the loading is complete.
     */
    protected final void onFinishLoading(OnAnimationFinishListener listener) {
        if (isAnimationInProgress()) {
            onPostAnimationAction = new OnPostAnimationRunnable(listener, this, false);
            return;
        }

        hideProgress();
        if (listener != null) {
            listener.onFinish();
        }
    }

    protected interface OnAnimationFinishListener {
        void onFinish();
    }

    /* since this runnable is intended to be executed on UI (not main) thread, we should
    be careful and not keep any implicit references to activities */
    private static class OnPostAnimationRunnable implements Runnable {
        private final OnAnimationFinishListener listener;
        private final LoginActivity loginActivity;
        private final boolean showProgress;

        OnPostAnimationRunnable(OnAnimationFinishListener listener,
                LoginActivity loginActivity, boolean showProgress) {
            this.listener = listener;
            this.loginActivity = loginActivity;
            this.showProgress = showProgress;
        }

        @Override
        public void run() {
            if (loginActivity != null) {
                if (showProgress) {
                    loginActivity.showProgress();
                } else {
                    loginActivity.hideProgress();
                }
            }

            if (listener != null) {
                listener.onFinish();
            }
        }

        boolean isProgressBarWillBeShown() {
            return showProgress;
        }
    }

    private class OnPostAnimationListener implements TransitionListener, AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            // stub implementation
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // stub implementation
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onPostAnimation();
        }

        @Override
        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int type) {
            // stub implementation
        }

        @Override
        public void endTransition(LayoutTransition transition, ViewGroup container, View view, int type) {
            if (LayoutTransition.CHANGE_APPEARING == type || LayoutTransition.CHANGE_DISAPPEARING == type) {
                onPostAnimation();
            }
        }

        private void onPostAnimation() {
            if (onPostAnimationAction != null) {
                onPostAnimationAction.run();
                onPostAnimationAction = null;
            }
        }
    }
}