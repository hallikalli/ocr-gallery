package com.hklee.ocrgallery.fagments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.CombinedLoadStates
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.commit451.elasticdragdismisslayout.ElasticDragDismissFrameLayout
import com.commit451.elasticdragdismisslayout.ElasticDragDismissListener
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.adapters.ImagePagerAdapter
import com.hklee.ocrgallery.base.OnImageReadyListener
import com.hklee.ocrgallery.databinding.FragmentPagerBinding
import com.hklee.ocrgallery.viewmodels.TessViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


class ImagePagerFragment :
    BaseFragment<FragmentPagerBinding, TessViewModel>(R.layout.fragment_pager),
    OnImageReadyListener, ElasticDragDismissListener {
    private val args: ImagePagerFragmentArgs by navArgs()
    override val mainViewModel by activityViewModels<TessViewModel>()
    private var adapter = ImagePagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(R.transition.shared_element_transition)
        prepareTransitions()
    }


    override fun init() {
        binding.viewPager.adapter = adapter
        postponeEnterTransition()
        observePagePosition()
        initPagerPosition()
        initAdapterData()
        binding.draggableFrame.addListener(this)
    }

    private fun prepareTransitions() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                val position = mainViewModel.currentPosition.value ?: -1
                binding.viewPager.findViewWithTag<ViewGroup>(position)
                    ?.findViewById<ImageView>(R.id.fullImage)?.let { sharedElements[names[0]] = it }
            }
        })
    }

    private fun initAdapterData() {
        lifecycleScope.launch {
            mainViewModel.searchFlow?.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    //TODO : 오류구문 추가
    var isCurrentItemSetted = false
    private var loadStateListener = { _: CombinedLoadStates ->
        if (!isCurrentItemSetted && adapter.itemCount >= args.selectedPosition) {
            binding.viewPager.setCurrentItem(args.selectedPosition, false)
            isCurrentItemSetted = true
        }
    }

    private fun initPagerPosition() {
        adapter.addLoadStateListener(loadStateListener)
    }

    private fun observePagePosition() {
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mainViewModel.currentPosition.value = position
                }
            })
    }

    override fun onImageReady(position: Int) {
        //when Glide Completed
        if (position == args.selectedPosition) {
            startPostponedEnterTransition()
        }
    }

    override fun onDrag(
        elasticOffset: Float,
        elasticOffsetPixels: Float,
        rawOffset: Float,
        rawOffsetPixels: Float
    ) {
        Timber.d("온 드래그 ")
    }

    override fun onDragDismissed() {
        findNavController().navigateUp()
    }

}
