package com.github.grieey.wow

import android.os.Bundle
import com.github.grieey.coreui.CoreActivity
import com.github.grieey.wow.databinding.NavMainActivityBinding

/**
 * description: navigation的activity
 * @date: 2021/2/18 11:31
 * @author: Grieey
 */
class NavMainActivity : CoreActivity() {

  private lateinit var binding: NavMainActivityBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = NavMainActivityBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }
}