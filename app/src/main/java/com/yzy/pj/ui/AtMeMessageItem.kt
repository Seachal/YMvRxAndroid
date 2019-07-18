package com.yzy.pj.ui

import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.yzy.baselibrary.base.BaseEpoxyHolder
import com.yzy.baselibrary.base.BaseEpoxyModel
import com.yzy.baselibrary.extention.load
import com.yzy.commonlibrary.repository.bean.FuliBean
import com.yzy.pj.R
import kotlinx.android.synthetic.main.main_item.view.*

@EpoxyModelClass(layout = R.layout.main_item)
abstract class AtMeMessageItem : BaseEpoxyModel<BaseEpoxyHolder>() {

    @EpoxyAttribute
    lateinit var messageBean: FuliBean

    override fun onBind(itemView: View) {
        super.onBind(itemView)
        itemView.iv_item_bg.load(messageBean.url, 0)
    }
}