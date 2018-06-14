package io.asoldino.taglessfinal.enhanced

import org.scalatest.{FlatSpec, Matchers}

/**
  * End-of-the-world scenario: unit test.
  *
  * Using [[cats.Id]] we can just focus on the business logic
  */
class EnhancedVisualisationAlgebraSpec extends FlatSpec with Matchers {

  import cats.Id
  import io.asoldino.taglessfinal.algebra.VisualisationAlgebra

  behavior of "EnhancedVisualisationService[Id]"

  object SimpleVisualisationAlgebra$ extends VisualisationAlgebra[Id] {

    import io.asoldino.taglessfinal.Tile

    override def getVisualisationTile(coords: (Int, Int, Int)): Id[Tile] = "a tile"

    override def getStreetNamesTile(coords: (Int, Int, Int)): Id[Tile] = ???
  }

  it should "enhance in blue an even tile" in {
    val subject = new EnhancedVisualisationAlgebra[Id](SimpleVisualisationAlgebra$)

    subject.getVisualisationTile((1, 1, 0)) shouldBe "a tile in blue"
  }

  it should "enhance in red an odd tile" in {
    val subject = new EnhancedVisualisationAlgebra[Id](SimpleVisualisationAlgebra$)

    subject.getVisualisationTile((1, 2, 0)) shouldBe "a tile in red"
  }
}
