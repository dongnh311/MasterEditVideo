package com.dongnh.mastereditvideo.model

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class TabModel {
    var nameTab: String? = ""
    var iconName: String? = ""
    var iconId: Int? = null

    fun createTabModels(): MutableList<TabModel> {
        val listTab = mutableListOf<TabModel>()

        val tabFilter = TabModel()
        tabFilter.nameTab = "Filter"
        listTab.add(tabFilter)

        val tabEffect = TabModel()
        tabEffect.nameTab = "Effect"
        listTab.add(tabEffect)

        val tabGraph = TabModel()
        tabGraph.nameTab = "Graph"
        listTab.add(tabGraph)

        val tabTransition = TabModel()
        tabTransition.nameTab = "Transition"
        listTab.add(tabTransition)

        val tabSpecial = TabModel()
        tabSpecial.nameTab = "Special"
        listTab.add(tabSpecial)

        return listTab
    }
}