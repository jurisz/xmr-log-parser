package org.juz.xmrig

import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.util.*


fun main(args: Array<String>) {

	val threadCount = 3
	val userHome = System.getProperty("user.home")
	val folder = userHome + "/me/xmrig/build/"
	val fileName = "xmrig.log"

	val logFile = File(folder + fileName)

	val statResults = mutableListOf<StatisticData>()
	var hourStats: Optional<StatisticData> = Optional.empty()
	var currentHour = -1


	logFile.forEachLine {

		val date = try {
			LocalDate.parse(it.subSequence(1, 11))
		} catch (e: DateTimeParseException) {
			null
		}
		val time = try {
			LocalTime.parse(it.subSequence(12, 20))
		} catch (e: DateTimeParseException) {
			null
		}
		
		if (date == null || time == null) {
			return@forEachLine
		}
		
		val hour = time.hour

		val hourChanged = isHourChanged(hour, currentHour)
		if (hourChanged) {
			hourStats = Optional.of(StatisticData(threadCount = threadCount, date = date, hour = hour))
			statResults.add(hourStats.get())
		}

		hourStats.ifPresent { stats ->
			val dataLine = it.substring(22)
			// println("$date h: $hour: time: $time data: $dataLine")

			if (dataLine.startsWith("new job from")) {
				stats.newJobStarted(time)
				val diff = dataLine.substring(dataLine.indexOf("diff ") + 5).toInt()
				stats.totalDifficulty += diff
			}

			if (dataLine.startsWith("sending job result")) {
				val nonce = extractLong(dataLine, "nonceInt: ")
				val actualDiff = extractLong(dataLine, "actualDiff: ")
				stats.newShare(nonce, actualDiff)
			}
		}

		currentHour = hour
//		println(it)
	}

	println("==== RESULTS ====")
	//println(statResults)
	println(statResults[0].csvHeaders())
	statResults.forEach { r -> println(r.csvData()) }
}

fun extractLong(dataLine: String, search: String): Long {
	var dataStart = dataLine.indexOf(search)
	if (dataStart == -1) {
		return 0
	}
	dataStart += search.length
	var dataEnd = dataLine.indexOf(" ", dataStart)
	if (dataEnd == -1) {
		dataEnd = dataLine.length
	}
	return dataLine.substring(dataStart, dataEnd).toLong()
}

fun isHourChanged(hour: Int, currentHour: Int): Boolean {
	if (currentHour == -1) {
		return false
	}
	return (hour - 1 == currentHour)
}
