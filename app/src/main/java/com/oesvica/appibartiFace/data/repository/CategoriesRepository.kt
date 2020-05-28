package com.oesvica.appibartiFace.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

abstract class CategoriesRepository {

    abstract fun insertCategory(): Completable

    abstract fun updateCategory(transaction: String): Completable

    abstract fun deleteCategory(transaction: String): Completable

    abstract fun findCategories(): Flowable<List<String>>

}