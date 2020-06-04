/*
 * Copyright (C) 2018 Sneyder Angulo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oesvica.appibartiFace.di.builder

import com.oesvica.appibartiFace.ui.addPerson.AddPersonActivity
import com.oesvica.appibartiFace.ui.addStatus.AddStatusActivity
import com.oesvica.appibartiFace.ui.categories.CategoriesProvider
import com.oesvica.appibartiFace.ui.main.MainActivity
import com.oesvica.appibartiFace.ui.persons.PersonsProvider
import com.oesvica.appibartiFace.ui.standby.StandByProvider
import com.oesvica.appibartiFace.ui.statuses.StatusesProvider
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [CategoriesProvider::class, PersonsProvider::class, StatusesProvider::class, StandByProvider::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun bindAddStatusActivity(): AddStatusActivity

    @ContributesAndroidInjector()
    abstract fun bindAddPersonActivity(): AddPersonActivity

}