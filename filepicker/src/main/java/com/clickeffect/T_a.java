package com.clickeffect;

import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

public final class T_a {

    public interface ClickAction {
        void onClick(@NonNull View view);
    }

    private static final int OVERLAY_COLOR =
            0xFF000000;

    private static final int PRESSED_ALPHA =
            28;

    private static final long PRESS_IN_DURATION_MS =
            65L;

    private static final long RELEASE_DURATION_MS =
            140L;

    private static final long TAP_HOLD_DURATION_MS =
            16L;

    private static final Interpolator PRESS_IN_INTERPOLATOR =
            new DecelerateInterpolator();

    private static final Interpolator RELEASE_INTERPOLATOR =
            new AccelerateDecelerateInterpolator();

    private T_a() {
    }

    public static void apply(
            @NonNull View view) {

        apply(
                view,
                null
        );
    }

    public static void apply(
            @NonNull View view,
            ClickAction clickAction) {

        view.setOnTouchListener(
                touchListener(clickAction)
        );
    }

    @NonNull
    public static View.OnTouchListener
    touchListener() {

        return touchListener(null);
    }

    @NonNull
    public static View.OnTouchListener
    touchListener(
            ClickAction clickAction) {

        return new SmoothTouchListener(
                clickAction
        );
    }

    private static boolean isInside(
            @NonNull View view,
            @NonNull MotionEvent event) {

        float x =
                event.getX();

        float y =
                event.getY();

        return x >= 0
                && x <= view.getWidth()
                && y >= 0
                && y <= view.getHeight();
    }

    private static final class SmoothTouchListener
            implements View.OnTouchListener {

        private final ClickAction clickAction;

        private final ColorDrawable overlay =
                new ColorDrawable(
                        OVERLAY_COLOR
                );

        private ValueAnimator alphaAnimator;
        private View boundView;
        private boolean overlayAttached;
        private boolean pressedInside;

        private final Runnable releaseRunnable =
                new Runnable() {

                    @Override
                    public void run() {

                        if (boundView != null) {
                            animateTo(
                                    boundView,
                                    0,
                                    RELEASE_DURATION_MS,
                                    RELEASE_INTERPOLATOR
                            );
                        }
                    }
                };

        private SmoothTouchListener(
                ClickAction clickAction) {

            this.clickAction =
                    clickAction;
        }

        @Override
        public boolean onTouch(
                View v,
                MotionEvent event) {

            ensureOverlay(v);

            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    pressedInside = true;
                    v.removeCallbacks(
                            releaseRunnable
                    );
                    animateTo(
                            v,
                            PRESSED_ALPHA,
                            PRESS_IN_DURATION_MS,
                            PRESS_IN_INTERPOLATOR
                    );
                    return true;

                case MotionEvent.ACTION_MOVE:
                    updatePressedState(
                            v,
                            isInside(v, event)
                    );
                    return true;

                case MotionEvent.ACTION_UP:
                    boolean click =
                            pressedInside
                                    && isInside(v, event);

                    release(
                            v,
                            click
                    );

                    if (click) {
                        v.performClick();

                        if (clickAction != null) {
                            clickAction.onClick(v);
                        }
                    }

                    return true;

                case MotionEvent.ACTION_CANCEL:
                    release(
                            v,
                            false
                    );
                    return true;

                default:
                    return false;
            }
        }

        private void ensureOverlay(
                @NonNull View view) {

            if (!overlayAttached
                    || boundView != view) {

                if (boundView != null
                        && overlayAttached) {
                    boundView.getOverlay()
                            .remove(overlay);
                }

                boundView = view;
                overlay.setAlpha(0);
                view.getOverlay().add(overlay);
                overlayAttached = true;
            }

            overlay.setBounds(
                    0,
                    0,
                    view.getWidth(),
                    view.getHeight()
            );
        }

        private void updatePressedState(
                @NonNull View view,
                boolean nextPressed) {

            if (pressedInside == nextPressed) {
                return;
            }

            pressedInside = nextPressed;

            animateTo(
                    view,
                    nextPressed
                            ? PRESSED_ALPHA
                            : 0,
                    nextPressed
                            ? PRESS_IN_DURATION_MS
                            : RELEASE_DURATION_MS,
                    nextPressed
                            ? PRESS_IN_INTERPOLATOR
                            : RELEASE_INTERPOLATOR
            );
        }

        private void release(
                @NonNull View view,
                boolean clicked) {

            pressedInside = false;

            view.removeCallbacks(
                    releaseRunnable
            );

            if (!clicked) {
                animateTo(
                        view,
                        0,
                        RELEASE_DURATION_MS,
                        RELEASE_INTERPOLATOR
                );
                return;
            }

            view.postDelayed(
                    releaseRunnable,
                    TAP_HOLD_DURATION_MS
            );
        }

        private void animateTo(
                @NonNull View view,
                int targetAlpha,
                long duration,
                @NonNull Interpolator interpolator) {

            if (alphaAnimator != null) {
                alphaAnimator.cancel();
            }

            int currentAlpha =
                    overlay.getAlpha();

            if (currentAlpha
                    == targetAlpha) {
                return;
            }

            alphaAnimator =
                    ValueAnimator.ofInt(
                            currentAlpha,
                            targetAlpha
                    );

            alphaAnimator.setDuration(
                    duration
            );

            alphaAnimator.setInterpolator(
                    interpolator
            );

            alphaAnimator.addUpdateListener(
                    new ValueAnimator
                            .AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(
                                ValueAnimator animation) {

                            overlay.setAlpha(
                                    (Integer) animation
                                            .getAnimatedValue()
                            );

                            view.postInvalidateOnAnimation();
                        }
                    });

            alphaAnimator.start();
        }
    }
}
