package org.juz.xmrig


fun main(args: Array<String>) {

//	val hexs = arrayListOf<String>("bf", "ef", "d0", "05", "4a", "c7", "fb", "a7", "7e")
//	val numbers = arrayListOf<Int>(191, 239, 208, 5, 74, 199, 251, 167, 126, 231, 19)
	//191-239-208-5-74-199-251-167-126-231-19-242-40-68-43-81-232
	//bf ef d0 05 4a c7 fb a7 7e 

	val hexs = arrayListOf<String>("90","93","ef","d0","05","b5","10","89","15","29")
	val numbers = arrayListOf<Int>(144, 147, 239, 208, 5, 181, 16, 137, 21, 41)
//90 93 ef d0 05 b5 10 89 15 29
//144; 147; 239; 208; 5; 181; 16; 137; 21
	
	
	var i = 0
	for(h in hexs) {
		val dec = Integer.parseInt(h, 16)
		val num = numbers[i]
		println("$h $num = $dec")
		if (dec != num) {
			println("ERR")
		}
		i++
	}
	
}