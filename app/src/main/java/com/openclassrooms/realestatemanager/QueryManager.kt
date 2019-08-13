package com.openclassrooms.realestatemanager

import kotlinx.android.synthetic.main.activity_search.*

class QueryManager(var stringList: List<String>, var arrayArgs: Array<String>) {

    public fun createQuery():String{

        val sb: StringBuilder = java.lang.StringBuilder()

        sb.append("SELECT * FROM RealEstate WHERE ")
        for(string in stringList){
        if(string.isNotEmpty()) {

        }
            sb.append("agent = ? AND ")
          //  bindArgs.add(agent_et.text.toString())
        }

        var query: String?=null
        return ""
    }
}