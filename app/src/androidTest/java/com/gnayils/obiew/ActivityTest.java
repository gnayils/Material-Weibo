package com.gnayils.obiew;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Gnayils on 19/11/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void changeText_sameActivity() {
        /**
        onView(withId(R.id.button_test)).perform(click());
        onView(withId(R.id.button_test)).check(matches(withText("button")));
         */
    }

}
