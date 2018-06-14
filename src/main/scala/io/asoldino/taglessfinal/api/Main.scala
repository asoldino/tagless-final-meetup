package io.asoldino.taglessfinal.api

import fs2.StreamApp
import io.asoldino.taglessfinal.algebra.{NavigationAlgebra, VisualisationAlgebra}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

object Main extends StreamApp[Task] {

  import org.http4s.server.blaze.BlazeBuilder

  object SimpleVisualisationAlgebra$ extends VisualisationAlgebra[Task] {

    val logger = org.log4s.getLogger

    import io.asoldino.taglessfinal.Tile

    override def getVisualisationTile(coords: (Int, Int, Int)): Task[Tile] = Task.pure {
      logger.info("getting visualisation tile")

      "a tile"
    }

    override def getStreetNamesTile(coords: (Int, Int, Int)): Task[Tile] = Task.pure {
      logger.info("getting streets name tile")

      "a streets tile"
    }
  }

  object SimpleNavigationAlgebra$ extends NavigationAlgebra[Task] {

    val logger = org.log4s.getLogger

    import io.asoldino.taglessfinal.Tile

    override def getNavigationTile(coords: (Int, Int)): Task[Tile] = Task.pure {
      logger.info("getting navigation tile")

      "a navigation tile"
    }
  }

  val navigationApi = new Api[Task](SimpleVisualisationAlgebra$, SimpleNavigationAlgebra$)

  val instance: BlazeBuilder[Task] = BlazeBuilder[Task]
    .bindHttp(5000, "0.0.0.0")
    .mountService(navigationApi.tileApi, "/api")

  override def stream(args: List[String], requestShutdown: Task[Unit]): fs2.Stream[Task, StreamApp.ExitCode] = instance.serve
}
