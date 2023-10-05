package com.example.stack.util

import com.example.stack.R
import com.example.stack.util.Util.getString

enum class CurrentFragmentType(val value: String) {
    HOME(getString(R.string.home)),
    LOGIN(getString(R.string.login)),
    FINDBRO(getString(R.string.find_bro)),
    CHATROOM(getString(R.string.chatroom)),
    POSITION(getString(R.string.position)),
    PLAN(getString(R.string.plan)),
    USER(getString(R.string.user))
}