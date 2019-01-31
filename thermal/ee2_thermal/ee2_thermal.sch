EESchema Schematic File Version 4
EELAYER 26 0
EELAYER END
$Descr A4 11693 8268
encoding utf-8
Sheet 1 1
Title ""
Date ""
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L power:GND #PWR?
U 1 1 5C3FA567
P 4200 3700
F 0 "#PWR?" H 4200 3450 50  0001 C CNN
F 1 "GND" H 4200 3550 50  0000 C CNN
F 2 "" H 4200 3700 50  0001 C CNN
F 3 "" H 4200 3700 50  0001 C CNN
	1    4200 3700
	1    0    0    -1  
$EndComp
$Comp
L pspice:ISOURCE Iqopt
U 1 1 5C3FA5F0
P 2700 2700
F 0 "Iqopt" H 2700 2520 50  0000 C CNN
F 1 "ISOURCE" H 2710 2870 50  0000 C CNN
F 2 "" H 2700 2700 50  0001 C CNN
F 3 "" H 2700 2700 50  0001 C CNN
	1    2700 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:CAP Coptical
U 1 1 5C3FA79E
P 3350 2700
F 0 "Coptical" V 3450 2850 50  0000 C CNN
F 1 "CAP" V 3450 2550 50  0000 C CNN
F 2 "" H 3350 2700 50  0001 C CNN
F 3 "" H 3350 2700 50  0001 C CNN
	1    3350 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:R Roptamb
U 1 1 5C3FA982
P 3800 2700
F 0 "Roptamb" V 3880 2700 50  0000 C CNN
F 1 "R" V 3800 2700 50  0000 C CNN
F 2 "" H 3800 2700 50  0001 C CNN
F 3 "" H 3800 2700 50  0001 C CNN
	1    3800 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:R Rhinge
U 1 1 5C3FAAEA
P 4450 2000
F 0 "Rhinge" V 4530 2000 50  0000 C CNN
F 1 "v.high" V 4450 2000 50  0000 C CNN
F 2 "" H 4450 2000 50  0001 C CNN
F 3 "" H 4450 2000 50  0001 C CNN
	1    4450 2000
	0    1    1    0   
$EndComp
$Comp
L pspice:R Rheatpipe
U 1 1 5C3FAB15
P 6850 2000
F 0 "Rheatpipe" V 6930 2000 50  0000 C CNN
F 1 "low" V 6850 2000 50  0000 C CNN
F 2 "" H 6850 2000 50  0001 C CNN
F 3 "" H 6850 2000 50  0001 C CNN
	1    6850 2000
	0    1    1    0   
$EndComp
$Comp
L pspice:ISOURCE Iqmf
U 1 1 5C3FB7C1
P 5100 2700
F 0 "Iqmf" H 5100 2520 50  0000 C CNN
F 1 "ISOURCE" H 5110 2870 50  0000 C CNN
F 2 "" H 5100 2700 50  0001 C CNN
F 3 "" H 5100 2700 50  0001 C CNN
	1    5100 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:CAP Cmainf
U 1 1 5C3FB7C7
P 5750 2700
F 0 "Cmainf" V 5850 2850 50  0000 C CNN
F 1 "CAP" V 5850 2550 50  0000 C CNN
F 2 "" H 5750 2700 50  0001 C CNN
F 3 "" H 5750 2700 50  0001 C CNN
	1    5750 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:R Rmfamb
U 1 1 5C3FB7D3
P 6200 2700
F 0 "Rmfamb" V 6280 2700 50  0000 C CNN
F 1 "R" V 6200 2700 50  0000 C CNN
F 2 "" H 6200 2700 50  0001 C CNN
F 3 "" H 6200 2700 50  0001 C CNN
	1    6200 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:ISOURCE Iqmr
U 1 1 5C3FBC3D
P 7500 2700
F 0 "Iqmr" H 7500 2520 50  0000 C CNN
F 1 "ISOURCE" H 7510 2870 50  0000 C CNN
F 2 "" H 7500 2700 50  0001 C CNN
F 3 "" H 7500 2700 50  0001 C CNN
	1    7500 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:CAP Cmainr
U 1 1 5C3FBC43
P 8150 2700
F 0 "Cmainr" V 8250 2850 50  0000 C CNN
F 1 "CAP" V 8250 2550 50  0000 C CNN
F 2 "" H 8150 2700 50  0001 C CNN
F 3 "" H 8150 2700 50  0001 C CNN
	1    8150 2700
	1    0    0    -1  
$EndComp
$Comp
L pspice:R Rmramb
U 1 1 5C3FBC4F
P 8600 2700
F 0 "Rmramb" V 8680 2700 50  0000 C CNN
F 1 "R" V 8600 2700 50  0000 C CNN
F 2 "" H 8600 2700 50  0001 C CNN
F 3 "" H 8600 2700 50  0001 C CNN
	1    8600 2700
	1    0    0    -1  
$EndComp
Wire Wire Line
	2700 2000 3350 2000
Wire Wire Line
	3800 2000 3800 2450
Wire Wire Line
	3350 2450 3350 2000
Connection ~ 3350 2000
Wire Wire Line
	2700 3400 3350 3400
Wire Wire Line
	3800 3400 3800 2950
Wire Wire Line
	3350 2950 3350 3400
Connection ~ 3350 3400
Wire Wire Line
	6200 2000 6200 2450
Wire Wire Line
	5750 2450 5750 2000
Connection ~ 5750 2000
Wire Wire Line
	6200 3400 6200 2950
Wire Wire Line
	5750 2950 5750 3400
Connection ~ 5750 3400
Wire Wire Line
	8600 2000 8600 2450
Wire Wire Line
	8150 2450 8150 2000
Connection ~ 8150 2000
Wire Wire Line
	8600 3400 8600 2950
Wire Wire Line
	8150 2950 8150 3400
Connection ~ 8150 3400
Connection ~ 3800 2000
Connection ~ 6200 2000
Connection ~ 3800 3400
Connection ~ 6200 3400
Wire Wire Line
	4200 3700 4200 3400
Connection ~ 4200 3400
Text Label 4200 3550 0    60   ~ 0
Tamb
Text Label 2800 2000 0    60   ~ 0
Toptpod
Text Label 5200 2000 0    60   ~ 0
Tmainfront
Text Label 7600 2000 0    60   ~ 0
Tmainrear
Text Notes 2700 1800 0    60   ~ 0
Optics Pod\n
Text Notes 5100 1800 0    60   ~ 0
Main Front
Text Notes 7500 1800 0    60   ~ 0
Main Rear
Wire Wire Line
	3350 2000 3800 2000
Wire Wire Line
	3350 3400 3800 3400
Wire Wire Line
	5750 2000 6200 2000
Wire Wire Line
	5750 3400 6200 3400
Wire Wire Line
	8150 2000 8600 2000
Wire Wire Line
	8150 3400 8600 3400
Wire Wire Line
	3800 2000 4200 2000
Wire Wire Line
	6200 2000 6600 2000
Wire Wire Line
	3800 3400 4200 3400
Wire Wire Line
	4700 2000 5100 2000
Wire Wire Line
	7100 2000 7500 2000
Wire Wire Line
	4200 3400 5100 3400
Wire Wire Line
	6200 3400 7500 3400
Wire Wire Line
	2700 2000 2700 2300
Wire Wire Line
	2700 3100 2700 3400
Wire Wire Line
	5100 3100 5100 3400
Connection ~ 5100 3400
Wire Wire Line
	5100 3400 5750 3400
Wire Wire Line
	5100 2300 5100 2000
Connection ~ 5100 2000
Wire Wire Line
	5100 2000 5750 2000
Wire Wire Line
	7500 3100 7500 3400
Connection ~ 7500 3400
Wire Wire Line
	7500 3400 8150 3400
Wire Wire Line
	7500 2300 7500 2000
Connection ~ 7500 2000
Wire Wire Line
	7500 2000 8150 2000
$EndSCHEMATC
