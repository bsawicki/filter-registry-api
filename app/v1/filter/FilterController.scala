package v1.filter

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class FilterFormInput(data: String)

/**
  * Takes HTTP requests and produces JSON.
  */
class FilterController @Inject()(cc: FilterControllerComponents)(
    implicit ec: ExecutionContext)
    extends FilterBaseController(cc) {

  private val logger = Logger(getClass)

  private val form: Form[FilterFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "data" -> text
      )(FilterFormInput.apply)(FilterFormInput.unapply)
    )
  }

  def index: Action[AnyContent] = FilterAction.async { implicit request =>
    logger.trace("index: ")
    filterResourceHandler.find.map { posts =>
      Ok(Json.toJson(posts))
    }
  }

  def process: Action[AnyContent] = FilterAction.async { implicit request =>
    logger.trace("process: ")
    logger.trace("request: " + request.body.toString)
    processJsonPost()
  }

  def processBulk: Action[AnyContent] = FilterAction.async { implicit request =>
    logger.trace("processBulk: ")
    logger.trace("request: " + request.body.toString)
    processJsonPostBulk()
  }

  def show(filter: String): Action[AnyContent] = FilterAction.async {
    implicit request =>
      logger.trace(s"show: filter = $filter")
      filterResourceHandler.lookup(filter).map { post =>
        Ok(Json.toJson(post.get))
      }
  }

  private def processJsonPost[A]()(
      implicit request: FilterRequest[A]): Future[Result] = {
    def failure(badForm: Form[FilterFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: FilterFormInput) = {
      filterResourceHandler.create(input).map { post =>
        Created(Json.toJson(post)).withHeaders(LOCATION -> post.link)
      }
    }

    logger.trace(form.data.toString())
    form.bindFromRequest().fold(failure, success)
  }

  private def processJsonPostBulk[A]()(
    implicit request: FilterRequest[A]): Future[Result] = {
    def failure(badForm: Form[FilterFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: FilterFormInput) = {
      filterResourceHandler.createBulk(input).map { list =>
        //list.map(filter => {
          //Created(Json.toJson(filter)).withHeaders(LOCATION -> filter.link)
        //})
        MultiStatus(Json.toJson(list))
      }
    }

    logger.trace(form.data.toString())
    form.bindFromRequest().fold(failure, success)
  }
}
