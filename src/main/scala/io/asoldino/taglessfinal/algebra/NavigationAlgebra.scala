package io.asoldino.taglessfinal
package algebra

/**
  * We have a Trait here that describe the algebra of a navigation service.
  *
  * Map is divided into grids of size dependent on the x and y coordinates.
  *
  * @tparam F this algebra is not dependent to a specific container, but only enforces that the functions
  *           are returning a container type
  */
trait NavigationAlgebra[F[_]] {

  type Coords = (Int, Int)

  def getNavigationTile(coords: Coords): F[Tile]
}
