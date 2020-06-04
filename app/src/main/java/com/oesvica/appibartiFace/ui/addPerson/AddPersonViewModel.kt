package com.oesvica.appibartiFace.ui.addPerson

import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.base.BaseViewModel
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.schedulers.SchedulerProvider
import javax.inject.Inject

class AddPersonViewModel
@Inject constructor(
    private val maestrosRepository: MaestrosRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val categories by lazy { maestrosRepository.findCategories() }
    val statuses by lazy { maestrosRepository.findStatuses() }

    fun addPerson(
        cedula: String,
        category: String,
        status: String,
        client: String,
        device: String,
        date: String,
        photo: String
    ) {

        debug(
            "addPerson(\n" +
                    "        $cedula: String,\n" +
                    "        $category: String,\n" +
                    "        $status: String,\n" +
                    "        $client: String,\n" +
                    "        $device: String,\n" +
                    "        $date: String,\n" +
                    "        $photo: String"
        )
    }

}