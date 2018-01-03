package org.juz.xmrig

import java.io.File


fun main(args: Array<String>) {

	val userHome = System.getProperty("user.home")
	val folder = userHome + "/me/xmrig/build/"
	val fileName = "xmrig.log"
	val outDataFileName = "xmr-shares-data.csv"

	val logFile = File(folder + fileName)
	val outDataFile = File(folder + outDataFileName)
	if (!outDataFile.exists()) {
		outDataFile.createNewFile()
		for (i in 1..76) {
			outDataFile.appendText("b$i; ")
		}
		outDataFile.appendText("nonce; actualDiff; diff\n")
	}

	var blob: String? = null

	logFile.forEachLine {

		val dataLine = it.substring(22)

		if (dataLine.startsWith("new job blob:")) {
			blob = dataLine.substring(14)
		}

		if (dataLine.startsWith("sending job result")) {
			val nonce = extractLong(dataLine, "nonceInt: ")
			val diff = extractLong(dataLine, "diff: ")
			val actualDiff = extractLong(dataLine, "actualDiff: ")

			for (i in 0..75) {
				val hex = blob?.substring(i * 2, (i + 1) * 2)
				val num = Integer.parseInt(hex, 16)
				outDataFile.appendText("$num; ")
			}

			outDataFile.appendText("$nonce; $actualDiff; $diff\n")
		}
	}

	println("===== DONE =====")
}

 