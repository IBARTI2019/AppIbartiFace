package com.oesvica.appibartiFace.data.model.standby

import com.oesvica.appibartiFace.getParcel
import org.junit.Assert.*
import org.junit.Test

class StandByTest {

    @Test
    fun testStandByIsParcelable(){
        val standBy = StandBy(client = "001", device = "0001", time = "12:04", date = "2020-08-11", url = "imgExample.jpg")
        val standByFromParcel = StandBy.createFromParcel(standBy.getParcel())
        assertEquals(standBy, standByFromParcel)
    }

}