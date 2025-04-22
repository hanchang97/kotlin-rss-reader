package rssmission.controller

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import rssmission.model.Post
import rssmission.service.PostService
import rssmission.view.RssView
import kotlin.math.min

class RssController(
    val postServiceList: List<PostService>,
    val rssView: RssView,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    var originalPosts = listOf<Post>()

    suspend fun getPosts(checksUpdate: Boolean = false): List<Post> =
        coroutineScope {
            /** 의도적으로 별도의 Context를 사용하면
             * Test 함수와 다른 Context에서 동작하여 delay가 실제로 동작
             * (원래 Test함수는 delay를 무시하게 설계됨,
             * delay 이후에 currentTime으로 시간 출력 시 delay 설정한 시간 값만큼 더해서 출력은 된다!)
             * */

            withContext(ioDispatcher) {
                // delay(10000L)

                /** test 코드에서 주입한 dispatcher 사용 시 delay 만큼 실제 기다리는 동작은 무시된다!(가상으로 시간을 지나가게 함)*/

                val totalList =
                    postServiceList.map { async { it.getPosts() } }
                        .awaitAll()
                        .flatten()

                if (!checksUpdate) {
                    originalPosts = totalList
                }

                totalList
            }
        }

    suspend fun getFilteredPosts(keyword: String): List<Post> {
        val filteredByKeyWord =
            getPosts()
                .filter { it.title.contains(keyword) }

        return filteredByKeyWord
            .sortedByDescending { it.date }
            .take(min(10, filteredByKeyWord.size))
    }

    fun printInputMessage() {
        rssView.printInputMessage()
    }

    fun printPosts(postList: List<Post>) {
        rssView.printPostList(postList)
    }

    fun printNewPosts(postList: List<Post>) {
        rssView.printNewPostList(postList)
    }

    fun readInputContent(): String {
        return rssView.readInputContent()
    }

    suspend fun hasOtherPosts(newPosts: List<Post>): List<Post> {
        return newPosts.filter { !originalPosts.contains(it) }
    }
}
