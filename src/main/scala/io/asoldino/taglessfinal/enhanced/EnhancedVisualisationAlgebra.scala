package io.asoldino.taglessfinal
package enhanced

import cats.Monad
import io.asoldino.taglessfinal.algebra.VisualisationAlgebra

/**
  * As a requirement, odd tiles (x + y is odd) are required to be highlighted in red, the other in blue.
  *
  * @param wrapped a VisualisationService supposed to get the requested tile
  * @tparam F this algebra is not dependent to a specific container, but only enforces that the functions
  *           are returning a container type. Here we are a little bit more specific, because we are requiring
  *           F[_] to be a Monad, to chain invocation from wrapped monadically.
  */
class EnhancedVisualisationAlgebra[F[_] : Monad](wrapped: VisualisationAlgebra[F]) extends VisualisationAlgebra[F] {

  // These import enable .flatMap on every F which has a Monad
  import cats.syntax.flatMap._

  override def getVisualisationTile(coords: Coords): F[Tile] =
    wrapped.getVisualisationTile(coords).flatMap(tile => enhanceColors(coords, tile))

  // Equivalent to the above can be:
  // Monad[F].flatMap(wrapped.getVisualisationTile(coords))(tile => enhanceColors(coords, tile))

  override def getStreetNamesTile(coords: Coords): F[Tile] = wrapped.getStreetNamesTile(coords)

  private[this] def enhanceColors(coords: Coords, tile: Tile): F[Tile] =
    Monad[F].ifM(shouldEnhanceInBlue(coords))(Monad[F].pure(tile + " in blue"), Monad[F].pure(tile + " in red"))

  private[this] def shouldEnhanceInBlue(coords: (Int, Int, Int)): F[Boolean] = Monad[F].pure((coords._1 + coords._2) % 2 == 0)
}
