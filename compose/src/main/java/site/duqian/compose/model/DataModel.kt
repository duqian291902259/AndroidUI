package site.duqian.compose.model

import android.os.SystemClock
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * 测试数据
 *协程必须在作用域中才能启动，作用域中定义了一些父子协程的规则，Kotlin协程通过作用域来管控域中的所有协程。
 * 父协程被取消，所有子协程均被取消；
父协程需等待子协程执行完毕后才会最终进入完成状态，而不管父协程本身的协程体是否已执行完；
子协程会继承父协程上下文中的元素，如果自身有相同Key的成员，则覆盖对应Key，覆盖效果仅在自身范围内有效。
Thread实例的 stop() 或 suspend()
isAlive()
interrupt()方法来中断该等待；
±---------+　　　　　　　　±---------------------+
| START |----------------------->| SUSPENDED |
±---------+　　　　　　　　±---------------------+
　　　　　　　　　　　　　　　|　　^
　　　　　　　　　　　　　　　V　　|
　　　　　　　　　　　　　　±-----------+ completion invoked ±----------------+
　　　　　　　　　　　　　　|RUNNING|------------------------->| COMPLETED |
　　　　　　　　　　　　　　±-----------+　　　　　　　　　±--------------------+

 */
class DataModel {

    @OptIn(DelicateCoroutinesApi::class)
    fun test(){
        runBlocking {
            repeat(10){
                launch {
                    println("launch ${System.currentTimeMillis()}")
                    delay(1000)
                    val status = getUserStatus("123456", "杜小菜")
                    println("getUserStatus111 $status")
                }
            }
        }

        //GlobalScope.launch {
        val launch = GlobalScope.launch(Dispatchers.Default) {
            //val status = getUserStatus("123456", "杜小菜")
            val status = async {
                getUserStatus("123456", "杜小菜")
            }
            println("getUserStatus222 ${status.await()}")
        }
        println("getUserStatus22 ${launch.isCancelled}")

        //launch.cancel()
    }
    /**
     * Kotlin 编译器会把挂起函数使用有限状态机转换为一种优化版回调,帮实现回调！
     */
    suspend fun getUserStatus(userId: String, password: String): Int {
        //val completion: Continuation<Any?> = Continuation(GlobalScope.coroutineContext,null)
        //logUserIn(completion)
        return logUserIn(userId, password)
    }

    // UserLocalDataSource.kt
    private suspend fun logUserIn(completion: Continuation<Any?>) {
        println("logUserIn")
        //return "result"
        completion.resume("杜小菜")
    }

    private suspend fun logUserIn(userId: String, password: String): Int {
        println("logUserIn start ${System.currentTimeMillis()}")
        SystemClock.sleep(2000)
        println("logUserIn end ${System.currentTimeMillis()}")
        return 100
    }
}