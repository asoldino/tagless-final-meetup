package io.asoldino.taglessfinal
package algebra
package examples

/**
  * Our map provider decided to play a little joke here, and gave us an incomplete map which does not provide
  * odd tiles (oddity regarding the sum of x + y coordinates).
  */
class NavigationAlgebraOptionInterpreter extends NavigationAlgebra[Option] {

  override def getNavigationTile(coords: (Int, Int)): Option[Tile] =
    if ((coords._1 + coords._2) % 2 == 0)
      Some("a tile")
    else
      None
}
