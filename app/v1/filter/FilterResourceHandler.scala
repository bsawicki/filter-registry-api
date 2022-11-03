package v1.filter

import javax.inject.{Inject, Provider}
import play.api.MarkerContext

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._

import scala.util.Try

/**
  * DTO for displaying post information.
  */
case class FilterResource(link: String, data: String)

object FilterResource {
  /**
    * Mapping to read/write a PostResource out as a JSON value.
    */
    implicit val format: Format[FilterResource] = Json.format
}


/**
  * Controls access to the backend data, returning [[FilterResource]]
  */
class FilterResourceHandler @Inject()(
                                     routerProvider: Provider[FilterRouter],
                                     filterRepository: FilterRepository)(implicit ec: ExecutionContext) {

  def create(postInput: FilterFormInput)(
      implicit mc: MarkerContext): Future[FilterResource] = {
    val data = FilterData(postInput.data)
    // We don't actually create the post, so return what we have
    filterRepository.create(data).map { filterId =>
      Console.println(filterId)
      createFilterResource(data)
    }
  }

  def createBulk(postInput: FilterFormInput)(
    implicit mc: MarkerContext): Future[List[FilterResource]] = {
    val dataArray = postInput.data.split(",")
    var resources = List[FilterResource]()
    dataArray.foreach(data => {
      filterRepository.create(FilterData(data)).map { filterId =>
        Console.println(filterId)
        resources = resources :+ createFilterResource(FilterData(data))
      }
    })
    // We don't actually create the post, so return what we have
    Future(resources)
  }

  def lookup(filter: String)(
      implicit mc: MarkerContext): Future[Option[FilterResource]] = {
    val filterFuture = filterRepository.get(filter)
    filterFuture.map { maybeFilterData =>
      maybeFilterData.map { filterData =>
        createFilterResource(filterData)
      }
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[FilterResource]] = {
    filterRepository.list().map { filterDataList =>
      filterDataList.map(filterData => createFilterResource(filterData))
    }
  }

  private def createFilterResource(filter: FilterData): FilterResource = {
    FilterResource(routerProvider.get.link(filter.data), filter.data)
  }

}
