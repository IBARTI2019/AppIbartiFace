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

package com.oesvica.appibartiFace.di.module;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.oesvica.appibartiFace.ViewModelProviderFactory;
import com.oesvica.appibartiFace.di.ViewModelKey;
import com.oesvica.appibartiFace.ui.addPerson.AddPersonViewModel;
import com.oesvica.appibartiFace.ui.addStatus.AddStatusViewModel;
import com.oesvica.appibartiFace.ui.personAsistencia.DocsViewModel;
import com.oesvica.appibartiFace.ui.asistencia.AsistenciaViewModel;
import com.oesvica.appibartiFace.ui.categories.CategoriesViewModel;
import com.oesvica.appibartiFace.ui.editPerson.EditPersonViewModel;
import com.oesvica.appibartiFace.ui.main.MainViewModel;
import com.oesvica.appibartiFace.ui.persons.PersonsViewModel;
import com.oesvica.appibartiFace.ui.standby.StandByViewModel;
import com.oesvica.appibartiFace.ui.statuses.StatusesViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@SuppressWarnings(value = "unused")
@Module
public abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(CategoriesViewModel.class)
  abstract ViewModel bindCategoriesViewModel(CategoriesViewModel viewModel);

  @Binds
  @IntoMap
  @ViewModelKey(PersonsViewModel.class)
  abstract ViewModel bindPersonsViewModel(PersonsViewModel PersonsViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(StatusesViewModel.class)
  abstract ViewModel bindStatusesViewModel(StatusesViewModel StatusesViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(AddStatusViewModel.class)
  abstract ViewModel bindAddStatusViewModel(AddStatusViewModel AddStatusViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(StandByViewModel.class)
  abstract ViewModel bindStandByViewModel(StandByViewModel StandByViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(AddPersonViewModel.class)
  abstract ViewModel bindAddPersonViewModel(AddPersonViewModel AddPersonViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(EditPersonViewModel.class)
  abstract ViewModel bindEditPersonViewModel(EditPersonViewModel EditPersonViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(AsistenciaViewModel.class)
  abstract ViewModel bindAsistenciaViewModel(AsistenciaViewModel AsistenciaViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(DocsViewModel.class)
  abstract ViewModel bindDocsViewModel(DocsViewModel DocsViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel.class)
  abstract ViewModel bindMainViewModel(MainViewModel MainViewModel);

  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory factory);

}
