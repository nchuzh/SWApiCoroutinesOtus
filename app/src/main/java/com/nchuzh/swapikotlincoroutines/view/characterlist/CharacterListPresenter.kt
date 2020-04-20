package com.nchuzh.swapikotlincoroutines.view.characterlist

import com.nchuzh.swapikotlincoroutines.domain.model.CharacterDetails
import com.nchuzh.swapikotlincoroutines.domain.model.MovieCharacter
import com.nchuzh.swapikotlincoroutines.domain.repository.CharactersRepository
import kotlinx.coroutines.*

class CharacterListPresenter(private val view: CharacterListView) {
    private val repository: CharactersRepository = CharactersRepository()
    private val jobs: MutableList<Job> = mutableListOf()

    fun fetch() {
        view.showProgress()
        jobs.add(CoroutineScope(Dispatchers.IO).launch {
            val list = repository.getCharacterList()
            withContext(Dispatchers.Main) {
                view.hideProgress()
                list?.let {
                    view.setList(list)
                } ?: view.showError()
            }
        })
    }

    fun getDetails(character: MovieCharacter) {
        view.showDetailsProgress()
        jobs.add(CoroutineScope(Dispatchers.IO).launch {
            val planet = repository.getPlanet(character.planetUrl)
            withContext(Dispatchers.Main) {
                view.hideDetailsProgress()
                planet?.let {
                    val details = CharacterDetails(character, planet)
                    view.openDetails(details)
                } ?: view.showError()
            }
        })
    }

    fun cleanUp() {
        for (job in jobs) {
            job.cancel()
        }
    }
}