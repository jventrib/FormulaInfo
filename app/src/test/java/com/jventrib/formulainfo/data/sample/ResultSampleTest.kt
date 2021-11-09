package com.jventrib.formulainfo.data.sample

import junit.framework.TestCase
import org.junit.Test
import java.io.File

class ResultSampleTest : TestCase() {

    @Test
    fun testtestFile() {
        val results = ResultSample.getLapsPerResults()
        println(results)
    }
}