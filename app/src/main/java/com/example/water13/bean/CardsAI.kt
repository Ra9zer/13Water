package com.example.water13.bean

import com.example.water13.component.toIndex

class CardsAI(val cards: MutableList<Card>) {

    val orgin = cards.toMutableList()

    val cardsInNumOrder = List(15) { mutableListOf<Card>() }

    val cardsInFlowerOrder = List(4) { mutableListOf<Card>() }

    var mergedList = mutableListOf<List<Card>>()

    var redCount = 0

    var blackCount = 0

    var duiziCount = 0

    var sanCount = 0

    var zhaCount = 0

    val solved = mutableListOf<String>()

    init {
        getCount()
        getShunzi()
    }

    fun getCount() {
        cards.forEach {
            cardsInNumOrder[it.num].add(it)
            cardsInFlowerOrder[it.flower.toIndex()].add(it)
        }
        redCount = cardsInFlowerOrder[0].size + cardsInFlowerOrder[1].size
        blackCount = cards.size - redCount
        cardsInNumOrder.forEach {
            when (it.size) {
                2 -> duiziCount++
                3 -> sanCount++
                4 -> zhaCount++
            }
        }
    }

    fun getShunzi() {
        val shunziList = mutableListOf<MutableList<Card>>()
        val copy: MutableList<Card> = cards.reversed().toMutableList()
        while (copy.size != 0) {
            val temp = mutableListOf<Card>()
            temp.add(copy[0].copy())
            for (i in 1..copy.lastIndex) {
                if (copy[i].num == temp.last().num - 1) {
                    temp.add(copy[i])
                }
            }
            shunziList.add(temp)
            copy -= temp
        }
        println("找连续：")
        shunziList.forEach { println(it) }
        outer@ for (i in 0..shunziList.lastIndex) {
            if (shunziList[i].size > 5) {
                for (j in i + 1..shunziList.lastIndex) {
                    val sumSize = shunziList[i].size + shunziList[j].size
                    if (sumSize == 10) {
                        val index = 5 - shunziList[j].size
                        if (shunziList[i].subList(index, 5) == shunziList[j]) {
                            val subList = shunziList[i].subList(0, index)
                            shunziList[j] = (subList + shunziList[j]).toMutableList()
                            shunziList[i].minusAssign(subList)
                            break@outer
                        }
                    } else if (sumSize == 8) {
                        if (shunziList[j].size == 1) {
                            val num = shunziList[j][0].num
                            if (num == shunziList[i][0].num + 1) {
                                val subList = shunziList[i].subList(0, 2)
                                shunziList[j] = (shunziList[j] + subList).toMutableList()
                                shunziList[i].minusAssign(subList)
                                break@outer
                            } else if (num == shunziList[i].last().num - 1) {
                                val last = shunziList[i].lastIndex
                                val subList = shunziList[i].subList(last - 1, last + 1)
                                shunziList[j] = (shunziList[j] + subList).toMutableList()
                                shunziList[i].minusAssign(subList)
                                break@outer
                            }
                        } else {
                            val sub1 = shunziList[i].subList(1, 3)
                            val sub2 = shunziList[i].subList(3, 5)
                            if (sub1 == shunziList[j]) {
                                val subList = shunziList[i].subList(0, 1)
                                shunziList[j] = (subList + shunziList[j]).toMutableList()
                                shunziList[i].minusAssign(subList)
                                break@outer
                            } else if (sub2 == shunziList[j]) {
                                val last = shunziList[i].lastIndex
                                val subList = shunziList[i].subList(last, last + 1)
                                shunziList[j] = (shunziList[j] + subList).toMutableList()
                                shunziList[i].minusAssign(subList)
                                break@outer
                            }
                        }
                    }
                }

            }
        }
        println("********")
        println("试图合并：")
        shunziList.forEach {
            println(it)
            if (it.size == 8) {
                mergedList.add(it.subList(0, 5))
                mergedList.add(it.subList(5, 8))
            } else if (it.size == 3 || it.size == 5) {
                mergedList.add(it)
            } else if (it.size > 5) {
                mergedList.add(it.subList(0, 5))
            }
        }
        println("****====找顺子完毕====****")
        mergedList.forEach { println(it) }
        println("##########################")
    }

    /* 1.至尊青龙 全部同花 */
    fun isZhizun(): Boolean = cardsInFlowerOrder.any { it.size == 13 }

    /* 2.一条龙 2-A */
    fun isYitiaoLong(): Boolean = cardsInNumOrder.all { it.size == 1 }

    /* 3.十二皇族 12张>10 */
    fun isShierhuangzu(): Boolean = cardsInNumOrder.subList(11, 15).sumBy { it.size } == 12

    /* 4.三同花顺 */
    fun isSantonghuashun(): Boolean =
        cardsInFlowerOrder.filter { it.size != 0 }.all { issShunzi(it) }

    /* 5.三个炸 */
    fun isSangezha(): Boolean = zhaCount == 3

    /* 6.全>=8 */
    fun isQuanda(): Boolean = cardsInNumOrder.subList(1, 8).all { it.size == 0 }

    /* 7.全<8 */
    fun isQuanxiao(): Boolean = cardsInNumOrder.subList(8, 15).all { it.size == 0 }

    /* 全同色 */
    fun isCouyise(): Boolean = blackCount == 0 || redCount == 0

    /* 9.双怪冲三 2葫芦 + 1对子 */
    fun isShuangguaichongsan() = sanCount == 2 && duiziCount == 3

    /* 10.四套三条 4三条 */
    fun isSitaosantiao() = sanCount == 4

    /* 11.五对三条 5对子1三条 */
    fun isWuduisantiao() = duiziCount == 5 && sanCount == 1

    /* 12.六对半 6对子 */
    fun isLiuduiban() = duiziCount == 6

    /* 13.三顺子 3顺子 */
    fun isSanshunzi(): Boolean = mergedList.size == 3 && mergedList.flatten().size == 13


    /* 14.三同花 3同花 */
    fun isSantonghua() = cardsInFlowerOrder.all { it.size == 3 || it.size == 5 }

    /* 1.同花顺 */
    fun issTonghuashun(cards: List<Card>): Boolean {
        return issTonghua(cards)
    }

    /* 2.炸弹 */

    /* 3.葫芦 */

    /* 4.同花 */
    fun issTonghua(cards: List<Card>): Boolean {
        val flower = cards[0].flower
        return cards.all { it.flower == flower }
    }

    /* 5.顺子 */
    fun issShunzi(cards: List<Card>): Boolean {
        var min = cards[0].num
        return cards.all { it.num == min++ }
    }

    /* 6.三条 */

    /* 7.连对 */

    /* 8.二对 */

    /* 9.一对 */


    fun submitSpecial() {
        val front = cards.subList(0, 3).joinToString(" ")
        val mid = cards.subList(3, 8).joinToString(" ")
        val end = cards.subList(8, 13).joinToString(" ")
        solved.add(front)
        solved.add(mid)
        solved.add(end)
    }

    fun solve(): CardsAI {
        if (isZhizun()
            || isYitiaoLong()
            || isShierhuangzu()
            || isSantonghuashun()
            || isSangezha()
            || isQuanda()
            || isQuanxiao()
            || isCouyise()
            || isShuangguaichongsan()
            || isSitaosantiao()
            || isWuduisantiao()
            || isLiuduiban()
            || isSanshunzi()
            || isSantonghua()
        ) {
            submitSpecial()
            println(mergedList)
            return this
        }
        //特殊牌型

        val tempSubmit = mutableListOf<MutableList<Card>>()

        cardsInFlowerOrder.filter { it.size >= 5 }.sortedByDescending { it.maxBy { it.num } }
            .forEach {
                CardsAI(it).mergedList.filter { it.size == 5 }.forEach {
                    it.let {
                        tempSubmit.add(it.toMutableList())
                    }
                }
            }
        restart("同花顺", tempSubmit, true)

        if (zhaCount > 0) {
            for (zhadan in cardsInNumOrder.filter { it.size == 4 }.reversed()) {
                if (tempSubmit.size < 3) {
                    tempSubmit.add(zhadan.toMutableList())
                }
            }
        }
        restart("炸弹", tempSubmit, true)

        while (sanCount > 1) {
            if (duiziCount == 0) {
                val sans = cardsInNumOrder.filter { it.size == 3 }.reversed()
                if (sanCount == 3) {
                    tempSubmit.add((sans[0] + sans[2].subList(0, 2)).toMutableList())
                } else if (sanCount == 2) {
                    tempSubmit.add((sans[0] + sans[1].subList(0, 2)).toMutableList())
                }
                refresh(tempSubmit)
            } else {
                while (sanCount > 0 && duiziCount > 0) {
                    val duis = cardsInNumOrder.filter { it.size == 2 }
                    val sans = cardsInNumOrder.filter { it.size == 3 }.reversed()
                    tempSubmit.add((sans[0] + duis[0]).toMutableList())
                    refresh(tempSubmit)
                }
            }
        }
        if (sanCount == 1) {
            if (duiziCount > 0) {
                val duis = cardsInNumOrder.filter { it.size == 2 }
                val sans = cardsInNumOrder.filter { it.size == 3 }.reversed()
                tempSubmit.add((sans[0] + duis[0]).toMutableList())
            }
        }
        restart("葫芦", tempSubmit, true)


        cardsInFlowerOrder.filter { it.size >= 5 }.sortedByDescending { it.maxBy { it.num } }
            .forEach {
                if (tempSubmit.size < 3) {
                    tempSubmit.add(it.subList(0, 5).toMutableList())
                    refresh(tempSubmit)
                }
            }
        restart("同花", tempSubmit, true)

        mergedList.filter { it.size == 5 }.sortedByDescending { it.maxBy { it.num } }.forEach {
            if (tempSubmit.size < 3) {
                tempSubmit.add(it.toMutableList())
                refresh(tempSubmit)
            }
        }
        restart("顺子", tempSubmit, true)

        if (sanCount > 0) {
            cardsInNumOrder.filter { it.size == 3 }.forEach {
                if (tempSubmit.size < 3) {
                    tempSubmit.add(it.toMutableList())
                    refresh(tempSubmit)
                }
            }
        }
        restart("三条", tempSubmit, true)

        if (duiziCount > 1) {
            val duis = cardsInNumOrder.filter { it.size == 2 }.reversed()
            println("duis==$duis")
            for (i in 0 until duis.lastIndex) {
                if (tempSubmit.size >= 3) break
                if (duis[i][0].num - 1 == duis[i + 1][0].num) {
                    tempSubmit.add((duis[i] + duis[i + 1]).toMutableList())
                    refresh(tempSubmit)
                    break
                }
            }
        }
        restart("连对", tempSubmit, true)

        while (duiziCount > 1) {
            if (tempSubmit.size >= 3) break
            cardsInNumOrder.filter { it.size == 2 }.reversed().subList(0, 2).flatten().let {
                tempSubmit.add(it.toMutableList())
            }
            refresh(tempSubmit)
        }
        restart("两对", tempSubmit, true)


        if (tempSubmit.size < 3) {
            val duizi = cardsInNumOrder.filter { it.size == 2 }.flatten()
            if (duizi.isNotEmpty()) {
                tempSubmit.add(duizi.toMutableList())
            }
        }
        restart("一对", tempSubmit, true)

        while (tempSubmit.size < 3) {
            tempSubmit.add(mutableListOf())
        }

        var reverseMode = false
        if (tempSubmit.filter { it.size == 0 }.size == 2) {
            reverseMode = true
        }

        if (reverseMode) {
            cards.sortDescending()
        } else {
            cards.sort()
        }

        for (i in 0..1) {
            while (tempSubmit[i].size != 5) {
                tempSubmit[i].add(cards[0])
                cards.removeAt(0)
            }
        }
        tempSubmit[2].addAll(cards)

        var a = 0
        tempSubmit.forEach {
            print("tempSubmit[${a++}]:$it\n")
            it.reversed().joinToString(" ").let {
                solved.add(it)
            }
        }
        solved.reverse()
        println(this)
        return this
    }


    private fun refresh(tempSubmit: MutableList<MutableList<Card>>) {
        restart("", tempSubmit, false)
    }

    private fun restart(after: String, tempSubmit: MutableList<MutableList<Card>>, show: Boolean) {
        if (show) println("找完${after}后: $tempSubmit")
        tempSubmit.forEach { it.forEach { remove(cards, it) } }
        if (show) println("剩余手牌: $cards")
        restart()
        if (show) {
            println(
                """
            顺子:$mergedList
            红色:$redCount
            黑色:$blackCount
            对子个数:$duiziCount
            三条个数:$sanCount
            炸弹个数:$zhaCount
        """.trimIndent()
            )
            for (i in 2..14) {
                val left = cardsInNumOrder[i].size
                if (left > 0) {
                    print("剩余${left}张[$i], ")
                }
            }
            println()
        }
    }

    fun restart() {
        cardsInNumOrder.forEach { it.clear() }
        cardsInFlowerOrder.forEach { it.clear() }
        mergedList.clear()
        redCount = 0
        blackCount = 0
        duiziCount = 0
        sanCount = 0
        zhaCount = 0
        getCount()
        getShunzi()
    }

    fun remove(cards: MutableList<Card>, target: Card) {
        for (i in cards.indices) {
            if (cards[i].reallyEquals(target)) {
                cards.removeAt(i)
                return
            }
        }
    }

    override fun toString(): String {
        return "origin: " + orgin.joinToString(" ") {
            it.origin
        } + "\nsolved: " + solved
    }
}