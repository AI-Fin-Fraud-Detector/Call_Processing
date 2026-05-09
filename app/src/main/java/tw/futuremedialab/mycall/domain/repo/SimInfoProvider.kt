package tw.futuremedialab.mycall.domain.repo

import tw.futuremedialab.mycall.domain.entity.SimInfo

interface SimInfoProvider {
    fun getSimsInfo(): List<SimInfo>
}