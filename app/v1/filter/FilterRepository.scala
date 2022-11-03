package v1.filter

import javax.inject.{Inject, Singleton}
import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext
import play.api.{Logger, MarkerContext}
import scalacache._
import scalacache.memcached._
import scalacache.memoization._
import scalacache.CacheConfig
import scalacache.modes.try_._

import scala.concurrent.duration._
import scalacache.serialization.circe
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import scala.concurrent.Future
import scala.util.Try

import java.util.concurrent.atomic.AtomicInteger

final case class FilterData(data: String)

/*class FilterId private(val underlying: Long) extends AnyVal {
  override def toString: String = underlying.toString
}

object FilterId {
  def apply(raw: String): FilterId = {
    require(raw != null)
    new FilterId(raw.toLong)
  }
}*/

class PostExecutionContext @Inject()(actorSystem: ActorSystem)
    extends CustomExecutionContext(actorSystem, "repository.dispatcher")

/**
  * A pure non-blocking interface for the PostRepository.
  */
trait FilterRepository {
  /*var atomicId: AtomicInteger = new AtomicInteger(0)*/

  def create(data: FilterData)(implicit mc: MarkerContext): Future[Unit]

  def list()(implicit mc: MarkerContext): Future[Iterable[FilterData]]

  def get(filter: String)(implicit mc: MarkerContext): Future[Option[FilterData]]
}

/**
  * A trivial implementation for the Post Repository.
  *
  * A custom execution context is used here to establish that blocking operations should be
  * executed in a different thread than Play's ExecutionContext, which is used for CPU bound tasks
  * such as rendering.
  */
@Singleton
class FilterRepositoryImpl @Inject()()(implicit ec: PostExecutionContext)
    extends FilterRepository {

  private val logger = Logger(this.getClass)

  private var filterList = List[FilterData]()

/*  implicit val cacheConfig = CacheConfig(
    memoization = MemoizationConfig(MethodCallToStringConverter.includeClassConstructorParams)
  )

  implicit val filterDataEncoder: Encoder[FilterData] = deriveEncoder[FilterData]
  implicit val filterDataDecoder: Decoder[FilterData] = deriveDecoder[FilterData]

  //implicit val cache: Cache[Cat] = MemcachedCache("localhost:11211")(cacheConfig, scalacache.serialization.binary.anyRefBinaryCodec[Cat])
  implicit val cache: Cache[FilterData] = MemcachedCache("localhost:11211")(cacheConfig, circe.codec)
  val filter1 = FilterData(atomicId.getAndIncrement(), "memcached filter 1")
  val filter2 = FilterData(atomicId.getAndIncrement(), "memcached filter 2")
  create(filter1)
  //put("2")(filter2, ttl = Some(10.seconds))
  create(filter2)*/

  override def list()(
      implicit mc: MarkerContext): Future[Iterable[FilterData]] = {
    Future {
      logger.trace(s"list: ")
      filterList
      /*var list : List[FilterData] = List()
      for (a <- 0 to atomicId.get()) {
        var foo = cache.get(a)
        if (foo.isSuccess) {
          list = list :+ foo.get.get
        }
      }
      list*/
    }
  }

  override def get(filter: String)(
      implicit mc: MarkerContext): Future[Option[FilterData]] = {
    Future {
      logger.trace(s"get: filter = $filter")
      filterList.find(post => post.data == filter)
      //cache.get(id)
    }
  }

  def create(data: FilterData)(implicit mc: MarkerContext): Future[Unit] = {
    Future {
      logger.trace(s"create: data = $data")
      filterList = filterList :+ data
      //val filter = FilterData(data.id, data.data)
      //put(atomicId.getAndIncrement)(filter)
      //data.id
    }
  }
}
