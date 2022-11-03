package v1.filter

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
  * Routes and URLs to the PostResource controller.
  */
class FilterRouter @Inject()(controller: FilterController) extends SimpleRouter {
  val prefix = "/v1/filters/"

  def link(filter: String): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix / filter
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
      controller.process

    case POST(p"/bulk/") =>
      controller.processBulk

    case GET(p"/$id") =>
      controller.show(id)
  }

}

class BulkFilterRouter @Inject()(controller: FilterController) extends SimpleRouter {
  val prefix = "/v1/bulk/"

  def link(filter: String): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix / filter
    url.toString()
  }

  override def routes: Routes = {
    case POST(p"/") =>
      controller.processBulk
  }

}
