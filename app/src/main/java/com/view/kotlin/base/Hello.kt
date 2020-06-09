package com.view.kotlin.base

import android.util.Log

class Hello {
    var lastName: String = "zhang"
    init {

    }
    //无返回值可省略
    fun setData(): Unit {
        Log.e("e", "Hello")
    }

    fun sum(a: Int, b: Int): Int {
        return a + b
    }

    fun sum2(a: Int, b: Int) = a + b
    public fun sum3(a: Int, b: Int): Int = a + b

    fun  vars(vararg v:Int){
        Log.e("vars","可变长参数函数v="+v[0]+v.size)
        for (vt in v){
            Log.e("vars","可变长参数函数vt="+vt)
        }
    }
}