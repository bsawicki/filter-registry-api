import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{ JsResult, Json }
import play.api.mvc.{ RequestHeader, Result }
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import v1.filter.FilterResource

import scala.concurrent.Future

class FilterRouterSpec extends PlaySpec with GuiceOneAppPerTest {

  "PostRouter" should {

    "render the list of posts" in {
      val request = FakeRequest(GET, "/v1/posts").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get

      val posts: Seq[FilterResource] = Json.fromJson[Seq[FilterResource]](contentAsJson(home)).get
      posts.filter(_.id == "1").head mustBe (FilterResource("1","/v1/filters/1", "filter 1" ))
    }

    "render the list of posts when url ends with a trailing slash" in {
      val request = FakeRequest(GET, "/v1/posts/").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get

      val posts: Seq[FilterResource] = Json.fromJson[Seq[FilterResource]](contentAsJson(home)).get
      posts.filter(_.id == "1").head mustBe (FilterResource("1","/v1/filters/1", "filter 1" ))
    }
  }

}