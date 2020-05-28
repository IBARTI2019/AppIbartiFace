package com.oesvica.appibartiFace.ui.categories

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CategoriesProvider {

    @ContributesAndroidInjector
    abstract fun provideCategoryFragment(): CategoriesFragment

}