package tw.futuremedialab.mycall.ui.home

import androidx.lifecycle.ViewModel
import tw.futuremedialab.mycall.domain.entity.SimInfo
import tw.futuremedialab.mycall.domain.repo.SimInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val simInfoProvider: SimInfoProvider
) : ViewModel() {

    fun getSimsInfo(): List<SimInfo> = simInfoProvider.getSimsInfo()
}
