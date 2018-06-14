package io.asoldino.taglessfinal
package mapdisplay

import cats.Monad
import io.asoldino.taglessfinal.algebra.VisualisationAlgebra

/**
  * To display a tile (on the screen using a logger) it is necessary to fetch a visualisation tile, and a tile containing
  * street names. After that it is possible to merge them into the final tile that can be displayed.
  *
  * Here we have side-effects wrapped into F.
  */
class MapDisplayService[F[_] : Monad](visualisationService: VisualisationAlgebra[F]) {

  import org.log4s.getLogger

  // Enable flatMap and map so the for comprehension can work properly
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  val logger = getLogger

  def displayTile(coords: VisualisationAlgebra[F]#Coords): F[Unit] = for {
    visTile <- visualisationService.getVisualisationTile(coords)

    streetsTile <- visualisationService.getStreetNamesTile(coords)

    mergedTile <- mergeTiles(visTile, streetsTile)

    rendered <- render(mergedTile)
  } yield rendered

  def mergeTiles(visTile: Tile, streetsTile: Tile): F[Tile] = Monad[F].pure {
    s"$visTile along with $streetsTile"
  }

  def render(tile: Tile): F[Unit] = Monad[F].pure {
    logger.info(tile)
  }
}
