package io.asoldino.taglessfinal.algebra
package examples

import org.scalatest.{FlatSpec, Matchers}

class NavigationAlgebraOptionInterpreterSpec extends FlatSpec with Matchers {

  behavior of classOf[NavigationAlgebraOptionInterpreter].getSimpleName

  it should "return None when x + y is odd" in {
    val subject: NavigationAlgebra[Option] = new NavigationAlgebraOptionInterpreter {}

    subject.getNavigationTile((1, 2)) shouldBe None
  }

  it should "return Some(tile) when x + y is even" in {
    val subject: NavigationAlgebra[Option] = new NavigationAlgebraOptionInterpreter {}

    subject.getNavigationTile((1, 1)) shouldBe a[Some[_]]
  }
}
