package com.view.kotlin.mvp

/**
 * @author zhaosengshan
 */
interface BasePresenter<V : BaseView?> {
    /**
     * @param view 视图
     */
    fun attachView(view: V)
    fun detachView()
}