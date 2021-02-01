package com.github.grieey.wow

import android.os.Bundle
import com.github.grieey.coreui.CoreActivity

class MainActivity : CoreActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
}