package com.hklee.ocrgallery.fagments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.adapters.GalleryAdapter
import com.hklee.ocrgallery.base.ListItemClickListener
import com.hklee.ocrgallery.base.OnImageReadyListener
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
    OnImageReadyListener,
    ListItemClickListener {
    override val mainViewModel by activityViewModels<TessViewModel>()
    private val adapter = GalleryAdapter(itemClickListener = this)
    private var job: Job? = null
    private val compositeDisposable = CompositeDisposable()
    private val SEARCH_REFRESH_MILLSEC = 300L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ON CREATE")
        sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(R.transition.shared_element_transition)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("ON CREATE VIEW")
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.recycler.adapter = adapter
        prepareTransitions()
        postponeEnterTransition()
        binding.recycler.doOnPreDraw {
            startPostponedEnterTransition()
        }
        return binding.root
    }

    override fun init() {
        observeSearch()
        scrollToPosition()
    }


    override fun onListItemClick(position: Int, viewItem: View) {
        val action = GalleryFragmentDirections.toPhotoSliderFragment(position)
        var extra = FragmentNavigatorExtras(viewItem to viewItem.transitionName)
        findNavController().navigate(action, extra)
    }

    override fun onImageReady(position: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ON DESTROY")
        compositeDisposable.clear()
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
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.shared_element_transition)

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                val position = mainViewModel.currentPosition.value ?: -1
                val selectedViewHolder = binding.recycler
                    .findViewHolderForAdapterPosition(position)
                    ?: return
                selectedViewHolder?.itemView?.findViewById<ImageView>(R.id.imageView_photo)?.let {
                    sharedElements[names[0]] = it
                }
            }
        })
    }


    /**
     * copy from  https://github.com/android/animation-samples/tree/main/GridToPager
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back from the grid.
     */

    // TODO : 슬라이드 애니메이션 문제 해결
    private fun scrollToPosition() {
        mainViewModel.currentPosition.observe(viewLifecycleOwner, {
            val layoutManager: RecyclerView.LayoutManager =
                binding.recycler.layoutManager!!
            val viewAtPosition = mainViewModel.currentPosition.value?.let {
                layoutManager.findViewByPosition(
                    it
                )
            }
            // Scroll to position if the view for the current position is null (not currently part of
            // layout manager children), or it's not completely visible.
            if (viewAtPosition == null || layoutManager
                    .isViewPartiallyVisible(viewAtPosition, false, true)
            ) {
                binding.recycler.post(Runnable {
                    mainViewModel.currentPosition.value?.let {
                        layoutManager.scrollToPosition(
                            it
                        )
                    }
                })
            }
        })
    }
}
