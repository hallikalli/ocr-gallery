package com.hklee.ocrgallery.fagments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.adapters.GalleryAdapter
import com.hklee.ocrgallery.base.ListItemClickListener
import com.hklee.ocrgallery.databinding.FragmentGalleryBinding
import com.hklee.ocrgallery.extensions.waitForTransition
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

    override val mainViewModel by activityViewModels<TessViewModel>()
    private val adapter = GalleryAdapter(this)
    private var job: Job? = null
    private val compositeDisposable = CompositeDisposable()
    private val SEARCH_REFRESH_MILLSEC = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(R.transition.shared_element_transition)
    }

    override fun init() {
        binding.recycler.adapter = adapter
        observeSearch()
        observeScrollPosition()
        prepareTransitions()
        waitForTransition(binding.recycler)
    }


    override fun onResume() {
        super.onResume()
        prepareTransitions()
        waitForTransition(binding.recycler)
    }

    override fun onListItemClick(position: Int, viewItem: View?) {
        val action = GalleryFragmentDirections.toPhotoSliderFragment(selectedPosition = position)
        var extra = FragmentNavigatorExtras(viewItem!! to viewItem.transitionName)
        findNavController().navigate(action, extra)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun observeScrollPosition() {
        // 다른 Fragment에 있는 ViewPager에 맞춰 위치를 scroll
//        mainViewModel.currentPosition.observe(viewLifecycleOwner, {
//            Timber.tag("page select scrollToPosition").d("$it}")
//            binding.recycler.scrollToPosition(it)
//        })
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

    private fun prepareTransitions() {
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                val position = mainViewModel.currentPosition.value ?: -1
                val selectedViewHolder =
                    binding.recycler.findViewHolderForAdapterPosition(position)
                selectedViewHolder?.itemView?.findViewById<ImageView>(R.id.image)?.let {
                    sharedElements[names[0]] = it
                }
            }
        })
    }

}
