package com.github.grieey.wow

import com.github.grieey.coreui.CoreApplication

/**
 * description:
 * @date: 2021/2/8 12:36
 * @author: Grieey
 */
class App : CoreApplication() {

  override fun onCreate() {
    super.onCreate()
    INSTANCE = this
  }

  companion object {
    lateinit var INSTANCE: CoreApplication
      private set
  }
}