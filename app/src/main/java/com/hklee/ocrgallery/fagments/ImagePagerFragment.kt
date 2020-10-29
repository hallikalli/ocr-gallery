package com.hklee.ocrgallery.fagments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
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
    OnImageReadyListener {
    private val args: ImagePagerFragmentArgs by navArgs()
    override val mainViewModel by activityViewModels<TessViewModel>()
    private var adapter = ImagePagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ON CREATE")
        sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(R.transition.shared_element_transition)
        prepareTransitions()
    }


    override fun init() {
        binding.viewPager.adapter = adapter
        postponeEnterTransition()
        initPagerPosition()
        initPageChangeObserve()
        initAdapterData()

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
                Timber.d("$position 포지션 setEnterSharedElementCallback : $sharedElements")

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
                adapter.removeLoadStateListener { this } // Todo: removelodSataeListener 삭제
            }

        }
    }

    private fun initPageChangeObserve() {
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mainViewModel.currentPosition.value = position
                }
            })
    }

    override fun onImageReady(position: Int) {
        //Glide Completed
        if (position == args.selectedPosition) {
            startPostponedEnterTransition()
        }
    }


    override fun onAttach(context: Context) {
        // TODO Auto-generated method stub
        super.onAttach(context)
        Timber.d("ON ATTACH")
    }

    override fun onStart() {
        // TODO Auto-generated method stub
        super.onStart()
        Timber.d("ON START")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("ON CREATE VIEW")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState)
        Timber.d("ON VIEW CREATED")
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        Timber.d("ON RESUME")
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
        Timber.d("ON PAUSE")
    }

    override fun onStop() {
        // TODO Auto-generated method stub
        super.onStop()
        Timber.d("ON STOP")
    }

    override fun onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView()
        Timber.d("ON DESTORY VIEW")
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        Timber.d("ON DESTROY")
    }

    override fun onDetach() {
        // TODO Auto-generated method stub
        super.onDetach()
        Timber.d("ON DETACH")
    }
}
