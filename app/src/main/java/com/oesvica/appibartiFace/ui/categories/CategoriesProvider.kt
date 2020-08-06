package com.oesvica.appibartiFace.ui.categories

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class CategoriesProvider {

    @ContributesAndroidInjector
    abstract fun provideCategoryFragment(): CategoriesFragment

}