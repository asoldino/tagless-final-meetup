package io.asoldino.taglessfinal
package algebra

/**
  * We have a Trait here that describe the algebra of a map visualisation service.
  *
  * Map is divided into grids of size dependent on the zoom level, so to identify a specific tile
  * a tuple of x, y and level is sufficient.
  *
  * Why a container here? Because what this algebra wants to express, is that its computation will not be pure
  * and some side-effects are definitely to be expected.
  *
  * @tparam F this algebra is not dependent to a specific container, but only enforces that the functions
  *           are returning a container type
  */
trait VisualisationAlgebra[F[_]] {

  type Coords = (Int, Int, Int)

  def getVisualisationTile(coords: Coords): F[Tile]

  def getStreetNamesTile(coords: Coords): F[Tile]
}
