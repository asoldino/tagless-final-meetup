package io.asoldino.taglessfinal
package effect

import cats.effect.Effect
import io.asoldino.taglessfinal.algebra.{NavigationAlgebra, VisualisationAlgebra}

/**
  * Here we show two things:
  *
  * Applicative[F].map2() for non-sequential computation (we apply a pure function to two F[A] and F[B] by only manipulating
  * an a:A and a b:B
  *
  * lazy monadic computation effect using [[Effect]]
  * From Effect scaladoc: "A monad that can suspend side effects into the `F` context and that supports lazy and potentially asynchronous evaluation."
  */
class DeviceService[F[_] : Effect](visualisationService: VisualisationAlgebra[F],
                                   navigationService: NavigationAlgebra[F]) {

  import cats.instances.list._
  import cats.syntax.flatMap._
  import cats.{Monad, Traverse}

  val logger = org.log4s.getLogger

  def renderScene(coords: VisualisationAlgebra[F]#Coords): F[Unit] = Traverse[List].sequence(fetchVisTileAndNavTile(coords))
    .flatMap {
      case visTile :: navTile :: Nil => drawToScreen(visTile, navTile)

      case _ => drawErrorToScreen
    }

  def fetchVisTileAndNavTile(coords: VisualisationAlgebra[F]#Coords): List[F[Tile]] = List(
    prepareVisualisationScene(coords), navigationService.getNavigationTile(getNavigationCoordinates(coords))
  )

  def prepareVisualisationScene(coords: VisualisationAlgebra[F]#Coords): F[Tile] = Monad[F].map2(
    visualisationService.getVisualisationTile(coords), visualisationService.getStreetNamesTile(coords)
  )(mergeTiles)

  def mergeTiles(visTile: Tile, streetsTile: Tile): Tile = s"$visTile along with $streetsTile"

  def getNavigationCoordinates(coords: VisualisationAlgebra[F]#Coords): NavigationAlgebra[F]#Coords = (coords._1, coords._2)

  /**
    * Since this is a def, it will allocate a new Effect every time it is invoked
    *
    * @param visTile
    * @param navTile
    * @return
    */
  def drawToScreen(visTile: Tile, navTile: Tile): F[Unit] = Effect[F].delay {
    logger.info("====================================================================================")
    logger.info("+  Visualisation                                 +   Navigation                    +")
    logger.info("+==================================================================================+")
    logger.info(s"+  $visTile      +   $navTile             +")
    logger.info("====================================================================================")
    logger.info("ASCII artists, forgive me!")
  }

  /**
    * This is a constant/lazy effect, delay means that it is suspended until necessary.
    * What do you think it will happen if the runtime is not able to correctly delay the evaluation of the thunk body?
    */
  val drawErrorToScreen = Effect[F].delay {
    logger.info("Error, please restart the device or call customer care!")
  }
}
