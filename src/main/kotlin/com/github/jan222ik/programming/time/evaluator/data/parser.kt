package com.github.jan222ik.programming.time.evaluator.data

import java.io.File
import java.io.FileNotFoundException
import kotlin.streams.toList

object Parser {

    @Throws(FileNotFoundException::class)
    fun fromMailFile(file: File): WeekWrapper {
        if (!file.exists()) {
            throw FileNotFoundException()
        }
        val text = file.readText(charset = Charsets.UTF_8)
        val secret = System.getProperty("secret")
        println(secret)
        val weekRangeRegex: Regex =
            """Subject: (\[weekly] report|$secret Weekly Summary|\[$secret] weekly report) for (?<fromDate>\d\d\d\d-\d\d-\d\d) until (?<untilDate>\d\d\d\d-\d\d-\d\d)(?<noActivity> \(no coding\r\n activity\))*""".toRegex()
        val totalTimeRegex: Regex =
            """((?<totalTimeHours>\d?\d) hr(s)? )?(?<totalTimeMinutes>\d?\d?\d) min(s)?\r\nhttps""".toRegex()
        val projectsRegex: Regex =
            """Projects:\r\n([^:]+?: (\d?\d hr(s)? )?(\d?\d min(s)?)?)(\r\n[^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)*""".toRegex()
        val languagesRegex: Regex =
            """Languages:\r\n([^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)(\r\n[^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)*""".toRegex()
        val idesRegex: Regex =
            """Editors:\r\n([^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)(\r\n[^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)*""".toRegex()
        val ossRegex: Regex =
            """Operating Systems:\r\n([^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)(\r\n[^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)*""".toRegex()
        val machinesRegex: Regex =
            """Machines:\r\n([^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)(\r\n[^:]+?: (\d?\d hr(s)? )?\d?\d min(s)?)*""".toRegex()

        val weekRangeMatchGroups = weekRangeRegex.find(text)?.groups!!
        val startDate = weekRangeMatchGroups["fromDate"]?.value!!
        val endDate = weekRangeMatchGroups["untilDate"]?.value!!
        println("weekRangeMatchGroups[ noActivity ] = ${weekRangeMatchGroups["noActivity"]?.value}")
        if (weekRangeMatchGroups["noActivity"] != null) {
            return WeekWrapper(
                startDate = startDate,
                endDate = endDate,
                total = Duration(0L),
                projects = emptyList(),
                languages = emptyList(),
                ides = emptyList(),
                oss = emptyList(),
                machines = emptyList()
            )
        }

        val totalTimeMatchGroups = totalTimeRegex.find(text)?.groups!!
        val totalTimeMin = totalTimeMatchGroups["totalTimeHours"]?.value?.toInt()?.times(60)
            ?.plus(totalTimeMatchGroups["totalTimeMinutes"]?.value?.toInt()!!)

        val projects: List<ProjectEntry> = projectsRegex.find(text)?.value?.split("\r\n")!!.stream().skip(1).map {
            val (name, duration) = NameDuration.matchPattern(it)
            return@map ProjectEntry(name, duration)
        }.toList()
        val languages: List<LanguageEntry> = languagesRegex.find(text)?.value?.split("\r\n")!!.stream().skip(1).map {
            val (name, duration) = NameDuration.matchPattern(it)
            return@map LanguageEntry(name, duration)
        }.toList()
        val ides: List<IdeEntry> = idesRegex.find(text)?.value?.split("\r\n")!!.stream().skip(1).map {
            val (name, duration) = NameDuration.matchPattern(it)
            return@map IdeEntry(name, duration)
        }.toList()
        val oss: List<OsEntry> = ossRegex.find(text)?.value?.split("\r\n")!!.stream().skip(1).map {
            val (name, duration) = NameDuration.matchPattern(it)
            return@map OsEntry(name, duration)
        }.toList()
        val machines: List<MachineEntry> = machinesRegex.find(text)?.value?.split("\r\n")?.stream()?.skip(1)?.map {
            val (name, duration) = NameDuration.matchPattern(it)
            return@map MachineEntry(name, duration)
        }?.toList() ?: emptyList()


        return WeekWrapper(
            startDate = startDate,
            endDate = endDate,
            total = Duration(totalTimeMin!!.toLong()),
            projects = projects,
            languages = languages,
            ides = ides,
            oss = oss,
            machines = machines
        )
    }

    fun fromMailFolder(folder: String = "src/main/resources/emails/"): List<WeekWrapper> {
        val file = File(folder)
        println("file = ${file.absolutePath}")
        println("file = ${file.exists()}")
        return file.listFiles().map {
            println("it = ${it.absoluteFile.name}")
            fromMailFile(it)
        }.toList()
    }

    data class NameDuration(
        val name: String,
        val duration: Duration
    ) {
        companion object {
            private val nameTimeRegex: Regex =
                """(?<name>[^:]+?): ((?<hrs>\d?\d?\d) hr(s)? )?((?<min>\d?\d) min(s)?)?""".toRegex()

            fun matchPattern(text: String): NameDuration {
                val groups = nameTimeRegex.find(text)?.groups!!
                val hrsMin = (if (groups["hrs"] != null) groups["hrs"]?.value?.toInt() else 0)?.times(60)!!
                val min = (if (groups["min"] != null) groups["min"]?.value?.toInt() else 0)!!
                return NameDuration(groups["name"]?.value!!, Duration((hrsMin + min).toLong()))
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
       fromMailFolder()
    }
}
