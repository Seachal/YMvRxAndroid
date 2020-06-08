package com.yzy.example.component

import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.LogUtils
import com.yzy.baselibrary.app.BaseApplication
import com.yzy.baselibrary.app.ExceptionTokenViewModel
import com.yzy.baselibrary.base.NoViewModel
import com.yzy.baselibrary.base.ViewModelFactory
import com.yzy.example.R
import com.yzy.example.component.comm.CommFragment
import com.yzy.example.component.home.HomeFragment
import com.yzy.example.component.me.MeFragment
import com.yzy.example.component.project.ProjectFragment
import com.yzy.example.component.publicNumber.PublicNumberFragment
import com.yzy.example.component.tree.TreeArrFragment
import com.yzy.example.databinding.FragmentMainBinding
import com.yzy.example.repository.TokenStateManager
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : CommFragment<NoViewModel, FragmentMainBinding>() {
    var fragments = arrayListOf<Fragment>()
    private val homeFragment: HomeFragment by lazy { HomeFragment() }
    private val projectFragment: ProjectFragment by lazy { ProjectFragment() }
    private val treeArrFragment: TreeArrFragment by lazy { TreeArrFragment() }
    private val publicNumberFragment: PublicNumberFragment by lazy { PublicNumberFragment() }
    private val meFragment: MeFragment by lazy { MeFragment() }

    init {
        fragments.apply {
            add(homeFragment)
            add(projectFragment)
            add(treeArrFragment)
            add(publicNumberFragment)
            add(meFragment)
        }
    }

    override fun initContentView() {
        selectFragment(0)
        mainNavigation.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_main -> selectFragment(0)
                    R.id.menu_project -> selectFragment(1)
                    R.id.menu_system -> selectFragment(2)
                    R.id.menu_public -> selectFragment(3)
                    R.id.menu_me -> selectFragment(4)
                }
                true
            }
        }

    }

    //当前页面
    private var currentFragment: Fragment? = null

    //设置选中的fragment
    private fun selectFragment(@IntRange(from = 0, to = 4) index: Int) {
        //需要显示的fragment
        val fragment = fragments[index]
        //和当前选中的一样，则不再处理
        if (currentFragment == fragment) return
        //先关闭之前显示的
        currentFragment?.let { FragmentUtils.hide(it) }
        //设置现在需要显示的
        currentFragment = fragment
        if (!fragment.isAdded) { //没有添加，则添加并显示
            val tag = fragment::class.java.simpleName
            FragmentUtils.add(childFragmentManager, fragment, mainContainer.id, tag, false)
        } else { //添加了就直接显示
            FragmentUtils.show(fragment)
        }
    }


    override fun getLayoutId(): Int = R.layout.fragment_main

}