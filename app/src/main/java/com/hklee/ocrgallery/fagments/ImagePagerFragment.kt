package com.hklee.ocrgallery.fagments

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.ImagePagerFragmentArgs
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.adapters.ImagePagerAdapter
import com.hklee.ocrgallery.databinding.FragmentPagerBinding
import com.hklee.ocrgallery.viewmodels.TessViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ImagePagerFragment :
    BaseFragment<FragmentPagerBinding, TessViewModel>(R.layout.fragment_pager) {
    private val args: ImagePagerFragmentArgs by navArgs()
    private val adapter = ImagePagerAdapter()
    override val mainViewModel by activityViewModels<TessViewModel>()

    override fun init() {
        binding.viewPager.adapter = adapter
        initAdapterData()
        initPagerPosition()
    }

    var isCurrentPosSetted = false;
    private fun initPagerPosition() {
        binding.viewPager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Timber.tag("page select pager change").d("${position}")
                    mainViewModel.currentPosition.value = position
                    if (!isCurrentPosSetted) {
                        // post 시 움직이지 않는 경우가 있어서 setCurrentItem 적용
                        setCurrentItem(args.selectedPosition, false)
                        isCurrentPosSetted = true;
                    }
                }
            })
        }
    }


    private fun initAdapterData() {
        lifecycleScope.launch {
            mainViewModel.searchFlow?.collectLatest {
                Timber.tag("page select initAdapterData").d("initAdapterData")
                adapter.submitData(it)
            }
        }
    }

}
