package com.example.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.responses.ListStoryItem
import com.example.storyapp.ui.adapters.StoryListAdapter
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.MainDispatcherRule
import com.example.storyapp.utils.PagingTestDataSource
import com.example.storyapp.utils.getOrAwait
import com.example.storyapp.utils.noopListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryRepository

    @Test
    fun listStory() = runTest {
        val dummyList = DataDummy.dummyDataEntity()
        val pagingData : PagingData<ListStoryItem> = PagingTestDataSource.snapshot(dummyList)

        val storyList = MutableLiveData<PagingData<ListStoryItem>>()
        storyList.value = pagingData

        Mockito.`when`(repository.getStory(TOKEN)).thenReturn(storyList)

        val viewModel = MainViewModel(repository)
        val trueStory: PagingData<ListStoryItem> = viewModel.listStory(TOKEN).getOrAwait()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(trueStory)
        advanceUntilIdle()

        assertNotNull(differ.snapshot())
        assertEquals(dummyList[0], differ.snapshot()[0])
        assertEquals(dummyList.size, differ.snapshot().size)

    }

    companion object {
        private const val TOKEN = "Bearer TOKEN"
    }

}