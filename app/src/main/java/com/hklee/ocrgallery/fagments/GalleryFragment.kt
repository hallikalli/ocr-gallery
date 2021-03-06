package com.hklee.ocrgallery.fagments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
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
import java.util.concurrent.TimeUnit


class GalleryFragment :
    BaseFragment<FragmentGalleryBinding, TessViewModel>(R.layout.fragment_gallery),
    OnImageReadyListener,
    ListItemClickListener {
    override val mainViewModel by activityViewModels<TessViewModel>()
    private val adapter = GalleryAdapter(itemClickListener = this)
    private var job: Job? = null
    private val uiCompositeDisposable = CompositeDisposable()
    private val SEARCH_REFRESH_MILLSEC = 300L
    var oldSearchWord: String? = null

    override fun init() {
        binding.recycler.adapter = adapter
        binding.vm = mainViewModel
        (activity as AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity)?.supportActionBar?.title=""
        prepareTransitions()
        postponeEnterTransition()
        binding.recycler.doOnPreDraw {
            scrollToPosition()
            startPostponedEnterTransition()
        }
        observeSearch()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_setting ->
                findNavController().navigate(GalleryFragmentDirections.toSettingsFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onListItemClick(position: Int, viewItem: View) {
        val action = GalleryFragmentDirections.toPhotoSliderFragment(position)
        var extra = FragmentNavigatorExtras(viewItem to viewItem.transitionName)
        findNavController().navigate(action, extra)
    }

    override fun onImageReady(position: Int) {
    }


    override fun onDestroyView() {
        super.onDestroyView()
        uiCompositeDisposable.clear()
    }

    private fun observeSearch() {
        var disposable = binding.searchView.queryTextChanges()
            .debounce(SEARCH_REFRESH_MILLSEC, TimeUnit.MILLISECONDS)
            .map(CharSequence::toString)
            .filter { it != oldSearchWord }
            .onErrorReturn { "" }
            .subscribe {
                search(it)
                oldSearchWord = it
            }
        uiCompositeDisposable.add(disposable)
    }

    private fun search(word: String) {
        job?.cancel()
        job = lifecycleScope.launch {
            adapter.submitData(PagingData.empty())
            mainViewModel.searchPhoto(word).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun observeLoading() {
        mainViewModel.loading.observe(viewLifecycleOwner, {
            binding.progressbar.isIndeterminate = it
        })
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
                selectedViewHolder.itemView.findViewById<ImageView>(R.id.thumnailImage)?.let {
                    sharedElements[names[0]] = it
                }
            }
        })
    }


    private fun scrollToPosition() {
        //화면상에 Item이 보이지 않으면 스크롤
        mainViewModel.currentPosition.value?.let {
            binding.recycler.layoutManager?.let { manager ->
                if (manager.findViewByPosition(it) == null)
                    manager.scrollToPosition(it)
            }
        }

    }
}
