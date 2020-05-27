package com.view.kotlin.mvp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.reflect.ParameterizedType

/**
 * @author zhaosengshan
 */
abstract class MVPBaseActivity<V : BaseView?, T : BasePresenterImpl<V>?> :
    AppCompatActivity(), BaseView {
    var mPresenter: T? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = getInstance(this, 1)
        mPresenter!!.attachView(this as V)
    }

    override val context: Context
        get() {
            return this@MVPBaseActivity
        }

    fun <T> getInstance(o: Any, i: Int): T? {
        try {
            return ((o.javaClass.genericSuperclass as ParameterizedType?)!!.actualTypeArguments[i] as Class<T>).newInstance()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detachView()
    }
}