// Why effects?
def divide(num: Int, den: Int): Int = num / den

divide(1, 2)
divide(2, 1)

divide(2, divide(2, 2))
divide(2, divide(1, 2))

// #1
// Errors
val snippet1 = {
  val a: Option[Int] = Some(1)
  val b: Option[Int] = None

  ()
}

def betterDivide(num: Int, den: Int): Option[Int] = (num, den) match {
  case (_, 0)          => None
  case (a, b)          => Some(a / b)
}

betterDivide(1, 2)
betterDivide(2, 1)
betterDivide(2, 0)

// Not possible, uh?
// betterDivide(2, betterDivide(2, 2))

// Option[+A] to the rescue!
betterDivide(2, betterDivide(2, 2).get)

// But it's quite not optimal, what happens if den is None?
betterDivide(2, betterDivide(2, 0).get)

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// #2
// Latency and errors
def resultOfTheCallToTheDatabase(secretKey: String): Option[String] =
  if (secretKey == "the meaning of life, the universe, everything") Some("42!")
  else None

def realCallToTheDatabase(secretKey: String): Future[Option[String]] =
  Future {
    Thread.sleep(500) // Easy way to simulate a bit of latency

    resultOfTheCallToTheDatabase(secretKey)
  }

// Does not compile until we provide an ExecutionContext, why?
//def realCallToTheDatabase(secretKey: String): Future[Option[String]] =
//  Future(resultOfTheCallToTheDatabase(secretKey))

realCallToTheDatabase("forgot!")
// Future(<not completed>)?

Await.result(realCallToTheDatabase("the meaning of life, the universe, everything"), Duration.Inf)

// #3
// Back to #1
betterDivide(2, 2).flatMap(den => betterDivide(4, den))

// Equivalent to
for {
  den <- betterDivide(2, 2)

  result <- betterDivide(4, den)
} yield result

// What happens here?
for {
  num <- betterDivide(2, 0)

  den <- betterDivide(42, 2)

  result <- betterDivide(num, den)
} yield result

//#4
//It was the monad!

import cats.Monad
import cats.instances.option._

Monad[Option].pure(42)
Monad[Option].flatMap(betterDivide(2, 3))(num => betterDivide(num, 42))

for {
  _ <- Monad[Option].pure(println("Starting..."))

  num <- betterDivide(2, 3)

  _ <- Monad[Option].pure(println(s"Numerator will be $num"))

  den <- betterDivide(42, 2)

  result <- betterDivide(num, den)
} yield result

// #5
// Combining monads

import cats.instances.future._
import cats.instances.list._

def doSomethingStupidWithAStringIntTuple(string: String, int: Int) = s"$string and $int"

val combined = for {
  _ <- Monad[Future].pure(println("Starting again..."))

  res <- realCallToTheDatabase("forgot!")

  somethingElse <- Monad[Future].pure(betterDivide(84, 2))
} yield Monad[Option].map2(res, somethingElse)(doSomethingStupidWithAStringIntTuple)

Await.result(combined, Duration.Inf)

val combined2 = for {
  _ <- Monad[Future].pure(println("Starting again..."))

  res <- realCallToTheDatabase("the meaning of life, the universe, everything")

  somethingElse <- Monad[Future].pure(betterDivide(84, 2))
} yield Monad[Option].map2(res, somethingElse)(doSomethingStupidWithAStringIntTuple)

Await.result(combined2, Duration.Inf)


// #6
// Monads are quite an abstraction

def sum(a: Int, b: Int) = a + b

def sumF[F[_] : Monad](a: Int, b: Int): F[Int] = Monad[F].pure(sum(a, b))

sumF[Option](4, 2)
sumF[Future](4, 2)
sumF[List](4, 2)

// Wait, List also has a monad?
Monad[List].flatMap(List(4,8))(x => List(x - 1, x, x + 1))