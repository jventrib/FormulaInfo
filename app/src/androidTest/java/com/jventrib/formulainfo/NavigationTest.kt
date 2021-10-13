package com.jventrib.formulainfo

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.google.common.truth.Truth.assertThat
import com.jventrib.formulainfo.model.db.Race
import com.jventrib.formulainfo.model.db.FullRace
import com.jventrib.formulainfo.ui.season.RaceListFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.hamcrest.Matchers.*
import org.hamcrest.core.AllOf
import org.junit.Test
import org.junit.runner.RunWith

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class NavigationTest {
//    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun testNavigationToAboutScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
        }

        val scenario = launchActivity<MainActivity>()
        scenario.onActivity {
            val view = it.findViewById<View>(R.id.nav_host_fragment)
            Navigation.setViewNavController(view, navController)
        }
        val home = navController.currentDestination?.id
        assertThat(home).isEqualTo(R.id.fragment_race_list)

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext);
        onView(withText("About")).perform(click())

        val about = navController.currentDestination?.id
        assertThat(about).isEqualTo(R.id.aboutFragment)
    }

    @Test
    fun navToRaceDetailScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
        }

        val scenario = launchFragmentInContainer<RaceListFragment>()
        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }

        val home = navController.currentDestination?.id
        assertThat(home).isEqualTo(R.id.fragment_race_list)

        onData(
            AllOf.allOf(
                `is`(instanceOf(FullRace::class.java)),
                hasProperty("race", hasProperty<Race>("round", equalTo(2)))
            )
        ).perform(click())

    }

    @Test
    fun navFromAboutToHomeScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setViewModelStore(ViewModelStore())
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.aboutFragment)
        }
        val scenario = launchActivity<MainActivity>()
        scenario.onActivity {
            Navigation.setViewNavController(it.findViewById(R.id.nav_host_fragment), navController)
        }

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext);
        onView(withText("About")).perform(click())

        onView(withId(R.id.text_title)).check(ViewAssertions.matches(withText("Formula Info")))
        pressBack()
        onView(withId(R.id.text_title)).check(ViewAssertions.matches(withText("Formula Info")))

    }
}