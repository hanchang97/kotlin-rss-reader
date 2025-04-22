import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import rssmission.controller.RssController
import rssmission.model.Post
import rssmission.service.NaverPostService
import rssmission.service.WoowahanPostService
import rssmission.view.RssView

class RssTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @kotlin.ExperimentalStdlibApi
    @Test
    @DisplayName("지정 시간 지난 후 메서드 호출 수행하는지 테스트")
    fun rssTimeTest() =
        runTest {
            // val dispatcher = StandardTestDispatcher() -> error 발생!
            val dispatcher = StandardTestDispatcher(testScheduler)

            /** testScheduler : 테스트 시 시간 조작을 위해 testScheduler 필요함 */

            val controller =
                RssController(
                    postServiceList = listOf(WoowahanPostService(), NaverPostService()),
                    rssView = RssView(),
                    ioDispatcher = dispatcher,
                )

            controller.originalPosts = listOf<Post>(Post())

            val job =
                async(dispatcher) {
                    delay(5000L)
                    controller.getPosts(true)
                }

            // advanceTimeBy(10000)

            val newPosts = job.await()

            println("size!! : ${newPosts.size}")
            newPosts.size shouldBe controller.hasOtherPosts(newPosts).size
        }
}
