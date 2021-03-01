package com.github.grieey.wow

import org.junit.Test

/**
 * description:
 * @date: 2021/3/1 21:59
 * @author: Grieey
 */
interface A {
  fun a()
}

class B : A {

  @Test
  fun test() {
    val c = C(this::a)
    c.print()
  }

  override fun a() {
    println("创特")
  }

}

class C(private val call: () -> Unit) {
  fun print() {
    call()
  }
}