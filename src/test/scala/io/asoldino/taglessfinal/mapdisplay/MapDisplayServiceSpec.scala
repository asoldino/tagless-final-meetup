package io.asoldino.taglessfinal.mapdisplay

import org.scalatest.{FlatSpec, Matchers}

class MapDisplayServiceSpec extends FlatSpec with Matchers {

  import cats.Id
  import io.asoldino.taglessfinal.algebra.VisualisationAlgebra

  import scala.concurrent.Future

  behavior of "MapDisplayService[Id]"

  object SimpleVisualisationAlgebra$ extends VisualisationAlgebra[Id] {

    import io.asoldino.taglessfinal.Tile

    override def getVisualisationTile(coords: (Int, Int, Int)): Id[Tile] = "a tile"

    override def getStreetNamesTile(coords: (Int, Int, Int)): Id[Tile] = "a streets tile"
  }

  it should "render a tile on the screen" in {

    val subject = new MapDisplayService[Id](SimpleVisualisationAlgebra$)

    val result = subject.displayTile((1, 1, 1))

    result shouldBe a[Id[_]]
    result shouldBe ()
  }

  behavior of "MapDisplayService[Future]"

  import scala.concurrent.ExecutionContext.Implicits.global

  object FutureVisualisationAlgebra$ extends VisualisationAlgebra[Future] {

    import io.asoldino.taglessfinal.Tile

    val logger = org.log4s.getLogger

    override def getVisualisationTile(coords: (Int, Int, Int)): Future[Tile] = Future {
      logger.info("getting visualisation tile")

      "a tile"
    }

    override def getStreetNamesTile(coords: (Int, Int, Int)): Future[Tile] = Future {
      logger.info("getting street names tile")

      "a streets tile"
    }
  }

  it should "render a tile on the screen (and probably something more interesting)" in {

    // This will bring a Monad[Future] in scope
    import cats.instances.future._

    import scala.concurrent.Await
    import scala.concurrent.duration.Duration

    val subject = new MapDisplayService[Future](FutureVisualisationAlgebra$)

    Await.result(subject.displayTile((1, 1, 1)), Duration.Inf) shouldBe ()
  }
}
