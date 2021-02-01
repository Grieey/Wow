package com.github.grieey.wow

import android.os.Bundle
import android.widget.Toast
import com.github.grieey.coreui.CoreActivity
import com.github.grieey.wow.databinding.ActivityMainBinding
import com.github.grieey.wow.items.textItem

class MainActivity : CoreActivity() {

  lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.epoxy.withModels {
      textItem {
        id("1")
        target(1)
        text("三角雷达坐标图")
        click { _, target ->
          Toast.makeText(this@MainActivity, "$target", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}