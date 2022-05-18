package com.jventrib.formulainfo.di

import android.app.Application
import android.content.Context
import com.karumi.shot.ShotTestRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : ShotTestRunner() {
    override fun newApplication(
        cl: ClassLoader,
        appName: String,
        context: Context
    ): Application {
        return super.newApplication(
            cl, HiltTestApplication::class.java.name, context
        )
    }
}
