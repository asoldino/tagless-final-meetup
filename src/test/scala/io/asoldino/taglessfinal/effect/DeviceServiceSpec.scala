package io.asoldino.taglessfinal.effect

import org.scalatest.{FlatSpec, Matchers}

class DeviceServiceSpec extends FlatSpec with Matchers {

  import io.asoldino.taglessfinal.algebra.{NavigationAlgebra, VisualisationAlgebra}
  import monix.eval.Task

  import scala.concurrent.Future

  behavior of "DeviceService[Task]"

  object SimpleVisualisationAlgebraTask extends VisualisationAlgebra[Task] {

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

  object SimpleNavigationAlgebraTask extends NavigationAlgebra[Task] {

    val logger = org.log4s.getLogger

    import io.asoldino.taglessfinal.Tile

    override def getNavigationTile(coords: (Int, Int)): Task[Tile] = Task.pure("a navigation tile")
  }


  it should "execute all the logic" in {
    import io.asoldino.taglessfinal.enhanced.EnhancedVisualisationAlgebra

    import scala.concurrent.duration.Duration

    val visualisationService = new EnhancedVisualisationAlgebra[Task](SimpleVisualisationAlgebraTask)

    import monix.execution.Scheduler.Implicits.global

    val subject = new DeviceService[Task](visualisationService, SimpleNavigationAlgebraTask)

    subject.renderScene((1, 1, 4)).runSyncUnsafe(Duration.Inf)
  }


  behavior of "DeviceService[Future]"

  import cats.effect.Effect

  import scala.concurrent.ExecutionContext.Implicits.global

  object SimpleVisualisationAlgebraFuture extends VisualisationAlgebra[Future] {

    val logger = org.log4s.getLogger

    import io.asoldino.taglessfinal.Tile

    override def getVisualisationTile(coords: (Int, Int, Int)): Future[Tile] = Future {
      logger.info("getting visualisation tile")

      "a tile"
    }

    override def getStreetNamesTile(coords: (Int, Int, Int)): Future[Tile] = Future {
      logger.info("getting streets name tile")

      "a streets tile"
    }
  }

  object SimpleNavigationAlgebraFuture extends NavigationAlgebra[Future] {

    val logger = org.log4s.getLogger

    import io.asoldino.taglessfinal.Tile

    override def getNavigationTile(coords: (Int, Int)): Future[Tile] = Future {
      logger.info("getting navigation tile")

      "a navigation tile"
    }
  }

  /**
    * Just for didactic purposes. Futures are not suitable as Effects
    */
  implicit object FutureEffect extends Effect[Future] {

    import cats.effect.IO

    // Here we use a future to lift the computation to a new thread
    // Can you catch the bug here?
    override def suspend[A](thunk: => Future[A]): Future[A] = Future(()) flatMap(_ => thunk)

    override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa flatMap f

    override def pure[A](x: A): Future[A] = Future(x)

    // ******** Intentionally left unimplemented
    override def runAsync[A](fa: Future[A])(cb: Either[Throwable, A] => IO[Unit]): IO[Unit] = ???

    override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Future[A] = ???

    override def tailRecM[A, B](a: A)(f: A => Future[Either[A, B]]): Future[B] = ???

    override def raiseError[A](e: Throwable): Future[A] = ???

    override def handleErrorWith[A](fa: Future[A])(f: Throwable => Future[A]): Future[A] = ???
  }

  it should "execute all the logic" in {
    import io.asoldino.taglessfinal.enhanced.EnhancedVisualisationAlgebra

    import scala.concurrent.duration.Duration

    val visualisationService: VisualisationAlgebra[Future] = new EnhancedVisualisationAlgebra[Future](SimpleVisualisationAlgebraFuture)

    import scala.concurrent.Await

    val subject = new DeviceService[Future](visualisationService, SimpleNavigationAlgebraFuture)

    Await.result(subject.renderScene((1, 1, 4)), Duration.Inf)
  }
}
