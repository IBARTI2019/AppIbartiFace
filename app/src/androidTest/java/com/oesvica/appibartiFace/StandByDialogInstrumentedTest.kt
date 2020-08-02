package com.oesvica.appibartiFace

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.oesvica.appibartiFace.data.model.standby.StandBy
import com.oesvica.appibartiFace.ui.standby.StandByFragmentDirections
import com.oesvica.appibartiFace.utils.dialogs.StandByDialog
import com.oesvica.appibartiFace.utils.dialogs.StandByDialogArgs

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class StandByDialogInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.oesvica.appibartiFace", appContext.packageName)
    }

    @Test
    fun testStandByDialog(){
        val standBy = StandBy(
            url = "001-00001-31-07-2020.jpg",
            date = "01-07-2020",
            client = "001",
            time = "3:27",
            device = "00001"
        )
        val directions = StandByFragmentDirections.actionNavStandbyToDialogStandby(standBy)
        val scenario = launchFragmentInContainer<StandByDialog>(directions.arguments)
        scenario.onFragment { fragment->
            val arguments = fragment.arguments
            assertNotNull(arguments)
            val args = StandByDialogArgs.fromBundle(arguments!!)
            assert(args.standby == standBy)
        }

    }
}
