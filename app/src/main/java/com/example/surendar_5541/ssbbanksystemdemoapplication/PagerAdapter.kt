package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlin.collections.ArrayList

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var mFragments: ArrayList<Fragment> = arrayListOf(LoginFragment(),RegisterFragment())


    override fun getItem(position: Int): Fragment = mFragments[position]


    override fun getCount(): Int = mFragments.size
}