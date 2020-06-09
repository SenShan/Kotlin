package com.view.kotlin

//默认一个主构造函数 可省略 参数也放里面
class GouZao constructor(name: String) {
    init {

    }

    //有主构造函数  二级构造函数必须代理主构造函数
    constructor(name: String, age: Int) : this(name) {
    }

    constructor(name: String, age: Int, len: Int) : this(name) {

    }

    open fun jump() {

    }

    class Demo {

    }
}