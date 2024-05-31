package com.github.imcamilo
package effects

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Try

/*
 * Functional Programming
 * Local reasoning:
 *   Type Signature, describes the entire computation that will be performed
 * Referential transparency:
 *   Ability to replace functional program with the expression with the value that it evaluates to
 * */

object Effects {

  // expressions
  private def combine(a: Int, b: Int): Int = a + b
  val five = combine(3, 2)
  val five2 = 3 + 2
  val five3 = 5

  // not all expressions are referential transparent
  // are not isdentical
  val voidResult: Unit = println("")
  val voidResult2: Unit = ()
  var anInt = 0
  val change: Unit = (anInt = 2) // side effect
  val change2: Unit = () // not the same program

  // side effects are inevitable
  /*
   * With this concepts we can talk about Effects
   * Effect desires:
   * - the type signature describes what KIND of computation it will perform
   * - the type signature describes the TYPE of the value that it will perform
   * - if side effects are required.
   *    - construction of the data structure must be separated from the EXECUTION of the effect type
   * */

  /*
   * Option
   * - type signature says that the computation returns an A. If the computation produces something
   * - no side effects are needed
   * => Option is an Effect
   * */
  val anOption: Option[Int] = Option(30)

  /*
   * Future
   * - type signature describes an async computation. Produces a value of type A. If it finished and its successful
   * - side effects are required, construction is not separated from execution
   * => Future is not an Effect
   * */
  import scala.concurrent.ExecutionContext.Implicits.global
  val aFuture: Future[Int] = Future(30)

  /*
   * MyIO
   *    - describes a computation which might perform side effects
   *    - produces values of type A if the computation is successful
   *    - side effects are required, construction is separated from execution
   * => is an Effect
   * */
  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] = MyIO(() => f(unsafeRun()))
    def flatMap[B](f: A => MyIO[B]): MyIO[B] =
      MyIO(() => f(unsafeRun()).unsafeRun())
  }

  val anIOWithSideEffects: MyIO[Int] = MyIO(() => {
    println("effect")
    30
  })

  /*
   *
   * an Effect is
   * - Data type which:
   * - Embodies a computational concept (e.g. side effects, absence of a value)
   * - is referentially transparent
   * Effect properties:
   * - It describes what kind of computation it will perform
   * - The type signature describes the value it will calculate
   * - It separates effect description from effect execution (when externally visible side effects are produced)
   * */

}

object EffectsExercises {

  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] =
      MyIO(() => f(unsafeRun()))
    def flatMap[B](f: A => MyIO[B]): MyIO[B] =
      MyIO(() => f(unsafeRun()).unsafeRun())
  }

  /* Create some IO which
   * 1. measure the current time of the system
   * 2. measure the duration of a computation
   *  - use exercise one
   *  - use map/flatMap combination of MyIO
   * 3. read something from the console
   * 4. print something to the console (e.g. "how old are u?"), then read, then print a welcome message
   * */

  // 1
  val currentTime: MyIO[Long] = MyIO(() => System.currentTimeMillis())
  // 2
  def measure[A](computation: MyIO[A]): MyIO[(Long, A)] = for {
    startTime <- currentTime
    result <- computation
    endTime <- currentTime
  } yield (endTime - startTime, result)

  def measure2[A](computation: MyIO[A]): MyIO[(Long, A)] = {

    MyIO { () =>
      val startTime = currentTime.unsafeRun()
      val result = computation.unsafeRun()
      val endTime = currentTime.unsafeRun()
      (endTime - startTime, result)
    }

  }

  def demoMeasure(): Unit = {
    val computation = MyIO(() => {
      println("A")
      Thread.sleep(1000)
      println("B")
      30
    })

    println(measure(computation).unsafeRun())
    println(measure2(computation).unsafeRun())
  }
  // 3
  val readLine: MyIO[String] = MyIO(() => StdIn.readLine())

  // 4
  def putStrLn(line: String): MyIO[Unit] = MyIO(() => println(line))
  val program = for {
    _ <- putStrLn("how old are u?")
    age <- readLine
    _ <- putStrLn(s"uh $age!")
  } yield ()

  def main(args: Array[String]): Unit = {
    // demoMeasure()
    program.unsafeRun()
  }

  /*A simplified ZIO*/
  case class MyZIO[-R, +E, +A](unsafeRun: R => Either[E, A]) {
    def map[B](f: A => B): MyZIO[R, E, B] =
      MyZIO(r =>
        unsafeRun(r) match {
          case Left(e)  => Left(e)
          case Right(v) => Right(f(v))
        }
      )
    def flatMap[R1 <: R, E1 >: E, B](f: A => MyZIO[R1, E1, B]): MyZIO[R1, E1, B] =
      MyZIO(r => unsafeRun(r) match
        case  Left(e) =>Left(e)
        case Right(v) => f(v).unsafeRun(r)
      )
  }

}
