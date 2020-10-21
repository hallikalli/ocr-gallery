package com.hklee.ocrgallery

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.adapters.GalleryAdapter
import com.hklee.ocrgallery.databinding.FragmentGalleryBinding
import com.hklee.ocrgallery.viewmodels.GalleryViewModel
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit


class GalleryFragment :
    BaseFragment<FragmentGalleryBinding, GalleryViewModel>(R.layout.fragment_gallery) {

    private val adapter = GalleryAdapter()
    private var job: Job? = null

    //    private val viewModel: GalleryViewModel by viewModels()
    private val compositeDisposable = CompositeDisposable()
    private val SEARCH_REFRESH_MILLSEC = 300L
    override val mainViewModel by activityViewModels<GalleryViewModel>()

    override fun init() {
        binding.photoList.adapter = adapter
        initSearchObservable()
    }

    private fun initSearchObservable() {
        //300밀리세즈에 한번씩 검색
        val observable = binding.editTextTextPersonName2.textChanges()
        var disposable = observable
            .debounce(SEARCH_REFRESH_MILLSEC, TimeUnit.MILLISECONDS)
            .onErrorReturn { "" }
            .subscribe {
                search(it.toString())
            }
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun search(word: String) {
        job?.cancel()
        job = lifecycleScope.launch {
//            mainViewModel.searchPhoto(word)
//            Timber.d("main view mode " + mainViewModel)
//            mainViewModel.searchPhoto(word)
             mainViewModel.searchPhoto(word).collectLatest {
                adapter.submitData(it)
            }
        }
    }


}