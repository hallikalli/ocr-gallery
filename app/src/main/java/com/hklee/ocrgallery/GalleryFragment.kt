package com.hklee.ocrgallery

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.adapters.GalleryAdapter
import com.hklee.ocrgallery.adapters.ListItemClickListener
import com.hklee.ocrgallery.databinding.FragmentGalleryBinding
import com.hklee.ocrgallery.viewmodels.TessViewModel
import com.jakewharton.rxbinding4.widget.queryTextChanges
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit


class GalleryFragment :
    BaseFragment<FragmentGalleryBinding, TessViewModel>(R.layout.fragment_gallery),
    ListItemClickListener {

    private val adapter = GalleryAdapter(this)
    override val mainViewModel by activityViewModels<TessViewModel>()

    private var job: Job? = null
    private val compositeDisposable = CompositeDisposable()
    private val SEARCH_REFRESH_MILLSEC = 300L

    override fun init() {
        binding.photoList.adapter = adapter
        observeSearch()
        observeScrollPosition()
    }

    override fun onListItemClick(position: Int) {
        Timber.tag("page select onListItemClick").d("${position}")
        findNavController().navigate(GalleryFragmentDirections.toPhotoSliderFragment(position))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun observeScrollPosition() {
        // 다른 Fragment에 있는 ViewPager에 맞춰 위치를 scroll
        mainViewModel.currentPosition.observe(viewLifecycleOwner, {
            Timber.tag("page select scrollToPosition").d("$it}")
            binding.photoList.scrollToPosition(it)
        })
    }

    private fun observeSearch() {
        var disposable = binding.searchView.queryTextChanges()
            .debounce(SEARCH_REFRESH_MILLSEC, TimeUnit.MILLISECONDS)
            .map(CharSequence::toString)
            .onErrorReturn { "" }
            .subscribe {
                search(it)
            }
        compositeDisposable.add(disposable)
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