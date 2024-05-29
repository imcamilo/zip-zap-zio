package com.github.imcamilo
package effects

import scala.concurrent.Future

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
   * - Embodies a computational comcept (e.g. side effects, absence of a value)
   * - is referentially transparent
   * Effect properties:
   * - It describes what kind of computation it will perform
   * - The type signature describes the value it will calculate
   * - It separates effect description from effect execution (when externally visible side effects are produced)
   * */

}
