package org.juz.xmrig

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


data class StatisticData(val threadCount: Int,
						 var date: LocalDate,
						 var hour: Int = 0,
						 var jobsCount: Int = 0,
						 var submitShares: Int = 0,
						 var longestJobSec: Int = 0,
						 var maxSharesForJob: Int = 0,
						 var totalDifficulty: Long = 0,
		//based on found nonce range of 4294967295  / nrThreads  
						 var sharesPerThread: MutableMap<String, Int> = mutableMapOf(),
		//actualDiff used nonce
						 var best5Shares: MutableMap<Long, Long> = mutableMapOf()
) {

	var jobStartTime: LocalTime? = null
	var jobShareCounter = 0

	fun newJobStarted(time: LocalTime) {
		jobsCount++
		if (jobStartTime != null) {
			val duration = Duration.between(jobStartTime, time)
			if (duration.seconds > longestJobSec) {
				longestJobSec = duration.seconds.toInt()
			}
		}
		jobStartTime = time

		if (maxSharesForJob < jobShareCounter) {
			maxSharesForJob = jobShareCounter
		}

		jobShareCounter = 0
	}

	fun newShare(nonce: Long, actualDiff: Long) {
		submitShares++
		jobShareCounter++

		val bestSharesSize = best5Shares.size
		val minDiff = best5Shares.keys.min()
		if (minDiff == null || bestSharesSize < 5) {
			best5Shares.put(actualDiff, nonce)
		} else if (minDiff < actualDiff) {
			best5Shares.remove(minDiff)
			best5Shares.put(actualDiff, nonce)
		}

		val step = 4294967295 / threadCount
		for (i in 1..threadCount) {
			if (nonce <= i * step) {
				sharesPerThread.merge("" + i, 1, { oldValue, one -> oldValue + one })
				break
			}
		}
	}

	fun getAverageDifficulty(): Long {
		return totalDifficulty / jobsCount
	}

	fun sortedBest5Shares(): MutableMap<Long, Long> {
		return best5Shares.toSortedMap(Comparator.reverseOrder())
	}

	override fun toString(): String {
		return "StatisticData(date=$date, hour=$hour, jobsCount=$jobsCount, submitShares=$submitShares, averageDiff: ${getAverageDifficulty()}," +
				" longestJobSec=$longestJobSec, maxSharesForJob=$maxSharesForJob, sharesPerThread=$sharesPerThread, best5Shares=${sortedBest5Shares()}"
	}


	fun csvHeaders(): String {
		return "date; hour; jobsCount; submitShares; averageDiff; longestJobSec; maxSharesForJob; sharesPerThread; best5Shares"
	}

	fun csvData(): String {
		return "$date; $hour; $jobsCount; $submitShares; ${getAverageDifficulty()}; $longestJobSec; $maxSharesForJob; $sharesPerThread; ${sortedBest5Shares()}"
	}
}