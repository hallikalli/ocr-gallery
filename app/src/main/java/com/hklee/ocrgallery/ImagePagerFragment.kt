package com.hklee.ocrgallery

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.hklee.musicplayer.base.BaseFragment
import com.hklee.ocrgallery.adapters.ImagePagerAdapter
import com.hklee.ocrgallery.databinding.FragmentPagerBinding
import com.hklee.ocrgallery.viewmodels.TessViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ImagePagerFragment :
    BaseFragment<FragmentPagerBinding, TessViewModel>(R.layout.fragment_pager) {

    private val adapter = ImagePagerAdapter()
    override val mainViewModel by activityViewModels<TessViewModel>()

    override fun init() {
        binding.viewPager.adapter = adapter
        initAdapterData()
        initPagerPosition()
        observePagerPosition()
    }

    private fun observePagerPosition() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 다른 Fragment에 있는 RecyclerView 위치를 scroll 하기 위함
                Timber.tag("page select pager change").d("${mainViewModel.currentPosition.value}")
                mainViewModel.currentPosition.value = position
            }
        })
    }
//TODO : 페이저 에서 마지막 이미지 클릭시버그 발생
    private fun initPagerPosition() {
        Timber.tag("page select pager").d("${mainViewModel.currentPosition.value}")
//        binding.viewPager.currentItem = mainViewModel.currentPosition.value ?: 0
    }

    private fun initAdapterData() {
        lifecycleScope.launch {
            mainViewModel.searchFlow?.collectLatest {
                adapter.submitData(it)
            }
        }
    }

}
