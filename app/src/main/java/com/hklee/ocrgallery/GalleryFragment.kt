package com.hklee.ocrgallery

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.adapters.GalleryAdapter
import com.hklee.ocrgallery.databinding.FragmentGalleryBinding
import com.hklee.ocrgallery.viewmodels.TessViewModel
import com.jakewharton.rxbinding4.widget.queryTextChanges
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class GalleryFragment :
    BaseFragment<FragmentGalleryBinding, TessViewModel>(R.layout.fragment_gallery) {

    private val adapter = GalleryAdapter()
    private var job: Job? = null

    //    private val viewModel: GalleryViewModel by viewModels()
    private val compositeDisposable = CompositeDisposable()
    private val SEARCH_REFRESH_MILLSEC = 300L
    override val mainViewModel by activityViewModels<TessViewModel>()

    override fun init() {
        binding.photoList.adapter = adapter
        initSearchObservable()
    }

    private fun initSearchObservable() {
        var disposable = binding.editTextTextPersonName2.queryTextChanges()
            .debounce(SEARCH_REFRESH_MILLSEC, TimeUnit.MILLISECONDS)
            .map(CharSequence::toString)
            .onErrorReturn { "" }
            .subscribe {
                search(it)
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
            mainViewModel.searchPhoto(word).collectLatest {
                adapter.submitData(it)
            }
        }
    }


}