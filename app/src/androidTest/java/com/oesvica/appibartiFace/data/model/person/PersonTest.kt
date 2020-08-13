package com.oesvica.appibartiFace.data.model.person

import com.oesvica.appibartiFace.getParcel
import org.junit.Assert.*
import org.junit.Test

class PersonTest {

    @Test
    fun testPersonIsParcelable(){
        val person = Person(id = "65413223")
        val personFromParcel = Person.createFromParcel(person.getParcel())
        assertEquals(person, personFromParcel)
    }

}