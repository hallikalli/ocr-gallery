package com.hklee.ocrgallery.fagments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.adapters.ImagePagerAdapter
import com.hklee.ocrgallery.databinding.FragmentPagerBinding
import com.hklee.ocrgallery.viewmodels.TessViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


class ImagePagerFragment :
    BaseFragment<FragmentPagerBinding, TessViewModel>(R.layout.fragment_pager),
    ImagePagerAdapter.OnImageReadyListener {
    private val args: ImagePagerFragmentArgs by navArgs()
    override val mainViewModel by activityViewModels<TessViewModel>()
    private var adapter = ImagePagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("created")
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        prepareTransitions()
    }


    override fun init() {
        binding.viewPager.adapter = adapter
//        postponeEnterTransition()
        binding.viewPager.doOnPreDraw{
            initAdapterData()
            initPageChangeObserve()
            initPagerPosition()
        }

    }

    private fun prepareTransitions() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                val position = mainViewModel.currentPosition.value ?: -1
                binding.viewPager.findViewWithTag<ViewGroup>(position)
                    ?.findViewById<ImageView>(R.id.image)?.let { sharedElements[names[0]] = it }
            }
        })
    }

    private fun initAdapterData() {
        lifecycleScope.launch {
            mainViewModel.searchFlow?.collectLatest {
                Timber.d("${it}")
                adapter.submitData(it)
            }
        }
    }

    private fun initPagerPosition() {
        // viewPager.doOnPreDraw 보다 adapter에서 setCurrentItem를 진행하는것이 안정적
        adapter.addLoadStateListener {
            if (adapter.itemCount >= args.selectedPosition) {
                binding.viewPager.setCurrentItem(args.selectedPosition, false)
            }
            adapter.removeLoadStateListener { this } // Todo: removelodSataeListener 삭제
        }
    }

    private fun initPageChangeObserve() {
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    mainViewModel.currentPosition.value = position
                }
            })
    }

    override fun onImageReady(position: Int) {
        //    onBindViewHolder에서 startPostponedEnterTransition를 하는것보다 Glide에서 이미지를 전부 로딩하고 부르는것이 더 안정적임
        if (position == args.selectedPosition) {
            startPostponedEnterTransition()
        }
    }
}
