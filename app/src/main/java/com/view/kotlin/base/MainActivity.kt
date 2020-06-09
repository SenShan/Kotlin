package com.view.kotlin.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.view.kotlin.CameraActivity
import com.view.kotlin.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var hello: Hello? = null
    private var button: Button? = null
    private var btnString: Button? = null
    private var text: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.btnAdd)
        btnString = findViewById(R.id.btnString)
        button!!.setOnClickListener(this)
        btnString!!.setOnClickListener(this)

        btnSection.setOnClickListener(this)
        text = findViewById(R.id.textContent)
        hello = Hello()
        hello?.setData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAdd -> {
                val intent = Intent()
                intent.setClass(this,CameraActivity().javaClass)
                startActivity(intent)
//                Log.e("e", "Hello=" + hello?.sum(7, 8))
//                hello?.vars(1,2,3,4,5)
            }
            R.id.btnString -> {
                var a = 1
                val s1 = "a is $a"
                Log.e("s1", "s1=" + s1)
                a = 2
                val s2 = "${s1.replace("is", "was")}, but now is $a"
                Log.e("s2", "s2=" + s2)

                val i = 10
                val s3 = "i = $i ${3*4}"
                Log.e("s3",s3)

                val m=3
                val x=m
                val y=m
                Log.e("==",(m==m).toString())
                val text="""www"""
                Log.e("==",(x==y).toString()+" "+(x===y).toString())
            }
            R.id.btnSection -> {
                section()
            }
        }
    }
    fun fall():Int{
        return 3*2
    }

    //null检查机制
    //类型后门加？表示可为空
    var age: String? = "23"

    //抛出空指针异常
    val ages = age!!.toInt()

    //不做处理返回null
    val ages1 = age?.toInt()

    //age为空返回-1
    val ages2 = age?.toInt() ?: -1

    //    fun parseInt(str: String): Int? {
    //当一个引用可能为 null 值时, 对应的类型声明必须明确地标记为可为 null。
    //当 str 中的字符串内容不是一个整数时, 返回 null
//    }
//    我们可以使用 is 运算符检测一个表达式是否某类型的一个实例(类似于Java中的instanceof关键字)。
    fun getStringLength(obj: Any): Int? {
        if (obj is String) {
            // 做过类型判断以后，obj会被系统自动转换为String类型
            return obj.length
        }

        //在这里还有一种方法，与Java中instanceof不同，使用!is
        // if (obj !is String){
        //   // XXX
        // }

        // 这里的obj仍然是Any类型的引用
        return null
    }


    //区间
    private fun section() {
        //输出 1 2 3 4
        for (i in 1..12) {
            Log.e("in", i.toString())
            inTest(i)
        }
        //输出 4 3 2 1
        for (i in 4 downTo 1) {
            Log.e("in", i.toString())
        }
        // 使用 step 指定步长  默认步长为1   就是1一个位置输出一个  step=2就是每隔2个输出所以为 1  3
        for (i in 1..4 step 2) { // 输出“1和3”
            Log.e("step", i.toString())
        }
        for (i in 4 downTo 1 step 2) { // 输出“4   2”
            Log.e("step downTo", i.toString())
        }
        // 使用 until 函数排除结束元素
        for (i in 1 until 10) {   // i in [1, 10) 排除了 10
            Log.e("until", i.toString())
        }
    }

    private fun inTest(i: Int) {
        if (i in 1..10) { // 等同于 1 <= i && i <= 10
            Log.e("在里面", i.toString())
        } else {
            Log.e("不在里面", i.toString())
        }
    }

    //数组
    fun num(){
        //[1,2,3]
        val a= arrayOf(1,2,3)
        //[0,2,4]  i从0到2为3个值   数组结果为i*2
        val b=Array(3,{i->(i*2)})
       // 除了类Array，还有ByteArray, ShortArray, IntArray
        val x: IntArray = intArrayOf(1, 2, 3)
        x[0] = x[1] + x[2]
        val z:CharArray= charArrayOf('C','C')
        byteArrayOf(1,2)
        shortArrayOf(1,2)

        //二维数组
        Array(3,{Array(3,{it -> 0})})
    }
}
