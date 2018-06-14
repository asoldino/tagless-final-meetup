package io.asoldino.taglessfinal
package api

import cats.effect.Effect
import io.asoldino.taglessfinal.algebra.{NavigationAlgebra, VisualisationAlgebra}

class Api[F[_] : Effect](visualisationService: VisualisationAlgebra[F],
                           navigationService: NavigationAlgebra[F]) {

  import cats.Monad
  import org.http4s._
  import org.http4s.dsl.Http4sDsl

  object FDsl extends Http4sDsl[F]

  import FDsl._

  val tileApi: HttpService[F] = HttpService[F] {
    case GET -> Root / "displaytile" / IntVar(level) / IntVar(x) / IntVar(y) =>
      Monad[F].flatMap(invokeService(level, x, y))(Ok(_))
  }

  def invokeService(level: Int, x: Int, y: Int): F[Tile] = {
    Effect[F].map2(visualisationService.getStreetNamesTile((x, y, level)), navigationService.getNavigationTile((x, y)))(
      (visTile, navTile) => s"$visTile ++++ $navTile"
    )
  }

}
