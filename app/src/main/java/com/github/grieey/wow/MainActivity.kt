package com.github.grieey.wow

import android.content.res.Resources
import android.os.Bundle
import com.github.grieey.core_ext.gone
import com.github.grieey.core_ext.sp
import com.github.grieey.core_ext.visible
import com.github.grieey.coreui.CoreActivity
import com.github.grieey.coreui.CoreFragment
import com.github.grieey.wow.constant.Router
import com.github.grieey.wow.databinding.ActivityMainBinding
import com.github.grieey.wow.extension.applyColorTo
import com.github.grieey.wow.extension.getColor
import com.github.grieey.wow.items.textItem
import kotlin.reflect.KClass

class MainActivity : CoreActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initEpoxy()
  }

  override fun onBackPressed() {
    if (binding.container.visible) {
      showList()
    } else {
      super.onBackPressed()
    }
  }

  private fun showList() {
    binding.epoxy.visible()
    binding.container.gone()
  }

  private fun showContainer() {
    binding.epoxy.gone()
    binding.container.visible()
  }

  private fun initEpoxy() {
    binding.epoxy.withModels {
      Router.MAP.forEach {
        textItem {
          id(it.first)
          target(it.first)
          text(it.second)
          style {
            it.textSize = 12.sp
            R.color.purple_500 applyColorTo it::setTextColor
          }
          click { _, _ ->
            showTarget(it.third)
          }
        }
      }
    }
  }

  private fun showTarget(target: KClass<out CoreFragment>) {
    val container = supportFragmentManager.findFragmentById(binding.container.id)
    if (container != null) {
      supportFragmentManager.beginTransaction().apply {
        replace(binding.container.id, target.java.getDeclaredConstructor().newInstance())
        commitAllowingStateLoss()
      }
    } else {
      supportFragmentManager.beginTransaction().apply {
        add(binding.container.id, target.java.getDeclaredConstructor().newInstance())
        commitAllowingStateLoss()
      }
    }

    showContainer()
  }
}