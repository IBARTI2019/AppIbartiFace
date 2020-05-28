package com.oesvica.appibartiFace.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCategoriesRepository
@Inject constructor() : CategoriesRepository() {

    override fun insertCategory(): Completable {
        TODO("Not yet implemented")
    }

    override fun updateCategory(transaction: String): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(transaction: String): Completable {
        TODO("Not yet implemented")
    }

    override fun findCategories(): Flowable<List<String>> {
        TODO("Not yet implemented")
    }
}