package com.github.imcamilo
package effects

import zio.*

import scala.io.StdIn

object ZIOEffects {

  val currentAge: ZIO[Any, Nothing, Int] = ZIO.succeed(30) //success
  val aFailure: ZIO[Any, String, Nothing] =
    ZIO.fail("Something went wrong") //failure
  //suspension/delay
  val aSuspendedZIO: ZIO[Any, Throwable, Int] = ZIO.suspend(currentAge)

  //map + flatMap
  val newAge = currentAge.map(_ + 1)
  val printingCA = currentAge.flatMap(ca => ZIO.succeed(println(ca)))

  val small = for {
    _ <- ZIO.succeed("how old are u?")
    name <- ZIO.succeed(StdIn.readLine())
    _ <- ZIO.succeed(println("uhh $name"))
  } yield ()

  //zip, zipWith
  val anotherAge: ZIO[Any, Nothing, Int] = ZIO.succeed(100)
  val tupledZIO: ZIO[Any, Nothing, (Int, Int)] = currentAge zip anotherAge
  val combinedZIO = currentAge.zipWith(anotherAge)(_ * _)

  // type aliases
  // UIO => UIO[A] => ZIO[Any, Nothing, A] => no requirements, cannot fail, produces A
  val uio: UIO[Int] = ZIO.succeed(432)

  // URIO => URIO[R, A] => ZIO[R, Nothing, A] => cannot fail
  val aURIO: URIO[Int, Int] = ZIO.succeed(12)

  // RIO => RIO[R, A] => ZIO[R, Throwable, A] => can fail with a Throwable
  val aRIO: RIO[Int, Int] = ZIO.succeed(76)
  val aFailedRIO: RIO[Int, Int] = ZIO.fail(new RuntimeException("bad"))

  // Task => Task[A] => ZIO[Any, Throwable, A] => no requirements, can fail with Throwable, produces A
  val aSuccessfulTask: Task[Int] = ZIO.succeed(54)
  val aFailedTask: Task[Int] = ZIO.fail(new RuntimeException("bad"))

  // IO[E, A] => ZIO[Any, E, A] => no requirements
  val aSuccessfulIO: IO[String, Int] = ZIO.succeed(32)
  val aFailedIO: IO[String, Int] = ZIO.fail("bad")

  def main(args: Array[String]): Unit = {}

}
