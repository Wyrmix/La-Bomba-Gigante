package com.wyrmix.giantbombvideoplayer.di

import android.app.Application
import android.content.Context
import net.lachlanmckee.timberjunit.TimberTestRule
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.log.PrintLogger
import org.koin.test.KoinTest
import org.koin.test.checkModules
import org.mockito.Mockito.mock


/**
 * Created by kylea
 *
 * 1/12/2019 at 12:20 AM
 */
class GiantbombAppModuleTest: KoinTest {
    @Rule @JvmField var logAllAlwaysRule = TimberTestRule.logAllAlways()

    private val printLogger = PrintLogger(true)

    val mockedAndroidContext = module {
        single { mock(Application::class.java) }
        single { mock(Context::class.java) }
    }

    @Test fun `check object graph`() {
        checkModules(listOf(networkModule, databaseModule, appModule, mockedAndroidContext), printLogger)
    }

    @Test fun `check networkModule`() {
        checkModules(listOf(networkModule, mockedAndroidContext), printLogger)
    }

    @Test fun `check databaseModule`() {
        checkModules(listOf(databaseModule, mockedAndroidContext), printLogger)
    }
}