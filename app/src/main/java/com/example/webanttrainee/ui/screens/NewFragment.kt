package com.example.webanttrainee.ui.screens

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.webanttrainee.databinding.ContentFragmentBinding
import com.example.webanttrainee.ui.adapters.PictureAdapter
import com.example.webanttrainee.ui.viewModels.NewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewFragment : BaseFragment<ContentFragmentBinding, NewViewModel>(
    ContentFragmentBinding::inflate
) {

    private val viewModel1 by viewModels<NewViewModel>()
    override fun getViewModelClass(): NewViewModel = viewModel1

    // Todo: вынесение навигации во вью модель
    private val pictureAdapter by lazy {
        PictureAdapter {
            findNavController().navigate(NewFragmentDirections.actionNewFragmentToDescriptionNewFragment(it))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupListeners()
        initRecycler()
    }

    private fun initRecycler() = with(binding) {
        recycler.adapter = pictureAdapter
    }

    private fun observeViewModel() {
        viewModel.pictureList.observe(viewLifecycleOwner) {
            pictureAdapter.submitList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.customProgressBar.isVisible = it
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = it
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            binding.placeHolderContainer.isVisible = it
            binding.recycler.isVisible = !it
        }
    }

    private fun setupListeners() {
        binding.refreshLayout.setOnRefreshListener { viewModel.refresh(true) }
        binding.recycler.addOnScrollListener(onScrollListener())
    }

}