/**
 * @name WeatherLoader
 * @version 1.0
 * @author Lazar Kirven
 * @copyright (c) 2010 Lazar Kirven
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

function WeatherLoader(map){

	this.currentRadarIndex = 0;
	this.currentRadarObj = undefined;
	this.loadedList = [];
	this.setMap(map);

    var div = this.div_= document.createElement('div');
    div.className = "weatherOverlay";
	
	this.nwsIndexTable = {
		ABC: 0,
		ABR: 1,
		ABX: 2,
		ACG: 3,
		AEC: 4,
		AHG: 5,
		AIH: 6,
		AKC: 7,
		AKQ: 8,
		AMA: 9,
		AMX: 10,
		APD: 11,
		APX: 12,
		ARX: 13,
		ATX: 14,
		BBX: 15,
		BGM: 16,
		BHX: 17,
		BIS: 18,
		BLX: 19,
		BMX: 20,
		BOX: 21,
		BRO: 22,
		BUF: 23,
		BYX: 24,
		CAE: 25,
		CBW: 26,
		CBX: 27,
		CCX: 28,
		CLE: 29,
		CLX: 30,
		CRP: 31,
		CXX: 32,
		CYS: 33,
		DAX: 34,
		DDC: 35,
		DFX: 36,
		DGX: 37,
		DIX: 38,
		DLH: 39,
		DMX: 40,
		DOX: 41,
		DTX: 42,
		DVN: 43,
		DYX: 44,
		EAX: 45,
		EMX: 46,
		ENX: 47,
		EOX: 48,
		EPZ: 49,
		ESX: 50,
		EVX: 51,
		EWX: 52,
		EYX: 53,
		FCX: 54,
		FDR: 55,
		FDX: 56,
		FFC: 57,
		FSD: 58,
		FSX: 59,
		FTG: 60,
		FWS: 61,
		GGW: 62,
		GJX: 63,
		GLD: 64,
		GRB: 65,
		GRK: 66,
		GRR: 67,
		GSP: 68,
		GWX: 69,
		GYX: 70,
		HDX: 71,
		HGX: 72,
		HKI: 73,
		HKM: 74,
		HMO: 75,
		HNX: 76,
		HPX: 77,
		HTX: 78,
		HWA: 79,
		ICT: 80,
		ICX: 81,
		ILN: 82,
		ILX: 83,
		IND: 84,
		INX: 85,
		IWA: 86,
		IWX: 87,
		JAX: 88,
		JGX: 89,
		JKL: 90,
		LBB: 91,
		LCH: 92,
		LIX: 93,
		LNX: 94,
		LOT: 95,
		LRX: 96,
		LSX: 97,
		LTX: 98,
		LVX: 99,
		LWX: 100,
		LZK: 101,
		MAF: 102,
		MAX: 103,
		MBX: 104,
		MHX: 105,
		MKX: 106,
		MLB: 107,
		MOB: 108,
		MPX: 109,
		MQT: 110,
		MRX: 111,
		MSX: 112,
		MTX: 113,
		MUX: 114,
		MVX: 115,
		MXX: 116,
		NKX: 117,
		NQA: 118,
		OAX: 119,
		OHX: 120,
		OKX: 121,
		OTX: 122,
		PAH: 123,
		PBZ: 124,
		PDT: 125,
		POE: 126,
		PUX: 127,
		RAX: 128,
		RGX: 129,
		RIW: 130,
		RLX: 131,
		RTX: 132,
		SFX: 133,
		SGF: 134,
		SHV: 135,
		SJT: 136,
		SOX: 137,
		SRX: 138,
		TBW: 139,
		TFX: 140,
		TLH: 141,
		TLX: 142,
		TWX: 143,
		TYX: 144,
		UDX: 145,
		UEX: 146,
		VAX: 147,
		VBX: 148,
		VNX: 149,
		VTX: 150,
		VWX: 151,
		YUX: 152
	};
	
	this.nwsRadarTable = [{
		name: "ABC",
		lat0: 64.835517,
		lat1: 56.735755,
		lng0: -157.448578,
		lng1: -166.284681
	}, {
		name: "ABR",
		lat0: 48.270508,
		lat1: 42.631241,
		lng0: -95.331912,
		lng1: -101.483839
	}, {
		name: "ABX",
		lat0: 37.565036,
		lat1: 32.726169,
		lng0: -104.179217,
		lng1: -109.457981
	}, {
		name: "ACG",
		lat0: 60.461814,
		lat1: 53.231040,
		lng0: -131.577368,
		lng1: -139.465485
	}, {
		name: "AEC",
		lat0: 69.093911,
		lat1: 59.913399,
		lng0: -160.277102,
		lng1: -170.292205
	}, {
		name: "AHG",
		lat0: 64.760112,
		lat1: 56.677196,
		lng0: -146.932788,
		lng1: -155.750514
	}, {
		name: "AIH",
		lat0: 63.344746,
		lat1: 55.565113,
		lng0: -142.050487,
		lng1: -150.537358
	}, {
		name: "AKC",
		lat0: 62.475546,
		lat1: 54.870627,
		lng0: -152.471947,
		lng1: -160.768223
	}, {
		name: "AKQ",
		lat0: 39.455939,
		lat1: 34.503058,
		lng0: -74.301929,
		lng1: -79.705072
	}, {
		name: "AMA",
		lat0: 37.650504,
		lat1: 32.806693,
		lng0: -99.062517,
		lng1: -104.346675
	}, {
		name: "AMX",
		lat0: 27.801004,
		lat1: 23.413018,
		lng0: -78.015566,
		lng1: -82.802459
	}, {
		name: "APD",
		lat0: 69.707350,
		lat1: 60.347639,
		lng0: -142.385187,
		lng1: -152.595781
	}, {
		name: "APX",
		lat0: 47.694534,
		lat1: 42.109314,
		lng0: -81.668440,
		lng1: -87.761408
	}, {
		name: "ARX",
		lat0: 46.559503,
		lat1: 41.076532,
		lng0: -88.195306,
		lng1: -94.176729
	}, {
		name: "ATX",
		lat0: 51.156440,
		lat1: 45.222770,
		lng0: -119.252062,
		lng1: -125.725156
	}, {
		name: "BBX",
		lat0: 42.051648,
		lat1: 36.925031,
		lng0: -118.807004,
		lng1: -124.399678
	}, {
		name: "BGM",
		lat0: 44.866266,
		lat1: 39.526025,
		lng0: -73.067287,
		lng1: -78.893004
	}, {
		name: "BHX",
		lat0: 43.095574,
		lat1: 37.892968,
		lng0: -121.448486,
		lng1: -127.124056
	}, {
		name: "BIS",
		lat0: 49.653369,
		lat1: 43.878131,
		lng0: -97.604622,
		lng1: -103.904882
	}, {
		name: "BLX",
		lat0: 48.688543,
		lat1: 43.009131,
		lng0: -105.502978,
		lng1: -111.698701
	}, {
		name: "BMX",
		lat0: 35.531168,
		lat1: 30.804239,
		lng0: -84.187375,
		lng1: -89.344024
	}, {
		name: "BOX",
		lat0: 44.611009,
		lat1: 39.291322,
		lng0: -68.231517,
		lng1: -74.034812
	}, {
		name: "BRO",
		lat0: 28.111632,
		lat1: 23.712370,
		lng0: -95.015402,
		lng1: -99.814597
	}, {
		name: "BUF",
		lat0: 45.646381,
		lat1: 40.241795,
		lng0: -75.784130,
		lng1: -81.680043
	}, {
		name: "BYX",
		lat0: 26.768932,
		lat1: 22.417156,
		lng0: -79.325351,
		lng1: -84.072743
	}, {
		name: "CAE",
		lat0: 36.329461,
		lat1: 31.559869,
		lng0: -78.513072,
		lng1: -83.716263
	}, {
		name: "CBW",
		lat0: 48.883011,
		lat1: 43.184631,
		lng0: -64.693612,
		lng1: -70.910026
	}, {
		name: "CBX",
		lat0: 46.212431,
		lat1: 40.759657,
		lng0: -113.254804,
		lng1: -119.203284
	}, {
		name: "CCX",
		lat0: 43.536138,
		lat1: 38.300343,
		lng0: -75.143350,
		lng1: -80.855126
	}, {
		name: "CLE",
		lat0: 44.045723,
		lat1: 38.770683,
		lng0: -78.977911,
		lng1: -84.732499
	}, {
		name: "CLX",
		lat0: 35.000443,
		lat1: 30.301010,
		lng0: -78.474401,
		lng1: -83.601054
	}, {
		name: "CRP",
		lat0: 30.016141,
		lat1: 25.543729,
		lng0: -95.067438,
		lng1: -99.946433
	}, {
		name: "CXX",
		lat0: 47.279556,
		lat1: 41.732361,
		lng0: -70.135214,
		lng1: -76.186700
	}, {
		name: "CYS",
		lat0: 43.774231,
		lat1: 38.520218,
		lng0: -101.935398,
		lng1: -107.667049
	}, {
		name: "DAX",
		lat0: 41.024068,
		lat1: 35.968740,
		lng0: -118.914954,
		lng1: -124.429858
	}, {
		name: "DDC",
		lat0: 40.258649,
		lat1: 35.254255,
		lng0: -97.233783,
		lng1: -102.693123
	}, {
		name: "DFX",
		lat0: 31.536875,
		lat1: 27.000879,
		lng0: -97.801696,
		lng1: -102.750055
	}, {
		name: "DGX",
		lat0: 34.615703,
		lat1: 29.935786,
		lng0: -87.427065,
		lng1: -92.532428
	}, {
		name: "DIX",
		lat0: 42.522523,
		lat1: 37.362092,
		lng0: -71.591531,
		lng1: -77.221092
	}, {
		name: "DLH",
		lat0: 49.722897,
		lat1: 43.940594,
		lng0: -89.050759,
		lng1: -95.358726
	}, {
		name: "DMX",
		lat0: 44.376700,
		lat1: 39.075660,
		lng0: -90.826704,
		lng1: -96.609657
	}, {
		name: "DOX",
		lat0: 41.360530,
		lat1: 36.282237,
		lng0: -72.665408,
		lng1: -78.205364
	}, {
		name: "DTX",
		lat0: 45.386552,
		lat1: 40.003663,
		lng0: -80.530985,
		lng1: -86.403228
	}, {
		name: "DVN",
		lat0: 44.252819,
		lat1: 38.961559,
		lng0: -87.690049,
		lng1: -93.462333
	}, {
		name: "DYX",
		lat0: 34.880381,
		lat1: 30.187082,
		lng0: -96.689750,
		lng1: -101.809712
	}, {
		name: "EAX",
		lat0: 41.343968,
		lat1: 36.266804,
		lng0: -91.490022,
		lng1: -97.028746
	}, {
		name: "EMX",
		lat0: 34.219871,
		lat1: 29.559654,
		lng0: -108.083824,
		lng1: -113.167697
	}, {
		name: "ENX",
		lat0: 45.267637,
		lat1: 39.894591,
		lng0: -71.128366,
		lng1: -76.989871
	}, {
		name: "EOX",
		lat0: 33.775034,
		lat1: 29.136530,
		lng0: -82.924690,
		lng1: -87.984876
	}, {
		name: "EPZ",
		lat0: 34.198338,
		lat1: 29.539189,
		lng0: -104.152408,
		lng1: -109.235117
	}, {
		name: "ESX",
		lat0: 38.132591,
		lat1: 33.260551,
		lng0: -112.229093,
		lng1: -117.544046
	}, {
		name: "EVX",
		lat0: 32.857391,
		lat1: 28.262253,
		lng0: -83.410381,
		lng1: -88.423259
	}, {
		name: "EWX",
		lat0: 31.977516,
		lat1: 27.422203,
		lng0: -95.539142,
		lng1: -100.508575
	}, {
		name: "EYX",
		lat0: 37.511496,
		lat1: 32.675711,
		lng0: -114.917901,
		lng1: -120.193302
	}, {
		name: "FCX",
		lat0: 39.497238,
		lat1: 34.541748,
		lng0: -77.566502,
		lng1: -82.972492
	}, {
		name: "FDR",
		lat0: 36.754119,
		lat1: 31.961166,
		lng0: -96.357302,
		lng1: -101.585979
	}, {
		name: "FDX",
		lat0: 37.034954,
		lat1: 32.226299,
		lng0: -101.001724,
		lng1: -106.247529
	}, {
		name: "FFC",
		lat0: 35.727322,
		lat1: 30.990063,
		lng0: -81.977736,
		lng1: -87.145655
	}, {
		name: "FSD",
		lat0: 46.313810,
		lat1: 40.852262,
		lng0: -93.745005,
		lng1: -99.703057
	}, {
		name: "FSX",
		lat0: 36.972198,
		lat1: 32.167068,
		lng0: -108.571650,
		lng1: -113.813610
	}, {
		name: "FTG",
		lat0: 42.355492,
		lat1: 37.207146,
		lng0: -101.732129,
		lng1: -107.348507
	}, {
		name: "FWS",
		lat0: 34.916298,
		lat1: 30.221169,
		lng0: -94.737753,
		lng1: -99.859713
	}, {
		name: "GGW",
		lat0: 51.168074,
		lat1: 45.233138,
		lng0: -103.381367,
		lng1: -109.855843
	}, {
		name: "GJX",
		lat0: 41.604976,
		lat1: 36.509761,
		lng0: -105.429157,
		lng1: -110.987574
	}, {
		name: "GLD",
		lat0: 41.919996,
		lat1: 36.802702,
		lng0: -98.904093,
		lng1: -104.486596
	}, {
		name: "GRB",
		lat0: 47.265939,
		lat1: 41.719980,
		lng0: -85.080889,
		lng1: -91.131027
	}, {
		name: "GRK",
		lat0: 33.019139,
		lat1: 28.416493,
		lng0: -94.868285,
		lng1: -99.889353
	}, {
		name: "GRR",
		lat0: 45.588975,
		lat1: 40.189210,
		lng0: -82.594763,
		lng1: -88.485416
	}, {
		name: "GSP",
		lat0: 37.290167,
		lat1: 32.467062,
		lng0: -79.584832,
		lng1: -84.846401
	}, {
		name: "GWX",
		lat0: 36.276013,
		lat1: 31.509319,
		lng0: -85.724655,
		lng1: -90.924683
	}, {
		name: "GYX",
		lat0: 46.630615,
		lat1: 41.141403,
		lng0: -67.257898,
		lng1: -73.246129
	}, {
		name: "HDX",
		lat0: 35.432593,
		lat1: 30.710822,
		lng0: -103.542198,
		lng1: -108.693221
	}, {
		name: "HGX",
		lat0: 31.740299,
		lat1: 27.195438,
		lng0: -92.595856,
		lng1: -97.553886
	}, {
		name: "HKI",
		lat0: 24.025431,
		lat1: 19.760813,
		lng0: -157.224961,
		lng1: -161.877271
	}, {
		name: "HKM",
		lat0: 22.232377,
		lat1: 18.017960,
		lng0: -153.478402,
		lng1: -158.075947
	}, {
		name: "HMO",
		lat0: 23.253328,
		lat1: 19.010958,
		lng0: -154.865122,
		lng1: -159.493162
	}, {
		name: "HNX",
		lat0: 38.764553,
		lat1: 33.854518,
		lng0: -116.948333,
		lng1: -122.304733
	}, {
		name: "HPX",
		lat0: 39.200971,
		lat1: 34.264051,
		lng0: -84.587650,
		lng1: -89.973381
	}, {
		name: "HTX",
		lat0: 37.339576,
		lat1: 32.513649,
		lng0: -83.447288,
		lng1: -88.711937
	}, {
		name: "HWA",
		lat0: 21.187943,
		lat1: 17.000443,
		lng0: -153.284108,
		lng1: -157.852290
	}, {
		name: "ICT",
		lat0: 40.148053,
		lat1: 35.150860,
		lng0: -94.712717,
		lng1: -100.164199
	}, {
		name: "ICX",
		lat0: 40.082936,
		lat1: 35.089985,
		lng0: -110.133034,
		lng1: -115.579889
	}, {
		name: "ILN",
		lat0: 41.975966,
		lat1: 36.854719,
		lng0: -81.023936,
		lng1: -86.610750
	}, {
		name: "ILX",
		lat0: 42.734240,
		lat1: 37.558352,
		lng0: -86.509081,
		lng1: -92.155504
	}, {
		name: "IND",
		lat0: 42.274591,
		lat1: 37.132059,
		lng0: -83.470306,
		lng1: -89.080341
	}, {
		name: "INX",
		lat0: 38.621204,
		lat1: 33.719883,
		lng0: -92.886099,
		lng1: -98.232995
	}, {
		name: "IWA",
		lat0: 35.651321,
		lat1: 30.918076,
		lng0: -109.082925,
		lng1: -114.246466
	}, {
		name: "IWX",
		lat0: 43.989540,
		lat1: 38.718879,
		lng0: -82.820300,
		lng1: -88.570111
	}, {
		name: "JAX",
		lat0: 32.775502,
		lat1: 28.184148,
		lng0: -79.193454,
		lng1: -84.202205
	}, {
		name: "JGX",
		lat0: 35.020961,
		lat1: 30.320491,
		lng0: -80.782832,
		lng1: -85.910617
	}, {
		name: "JKL",
		lat0: 40.082936,
		lat1: 35.089985,
		lng0: -80.585037,
		lng1: -86.031892
	}, {
		name: "LBB",
		lat0: 36.026280,
		lat1: 31.273076,
		lng0: -99.217025,
		lng1: -104.402339
	}, {
		name: "LCH",
		lat0: 32.408141,
		lat1: 27.833541,
		lng0: -90.716608,
		lng1: -95.707081
	}, {
		name: "LIX",
		lat0: 32.625061,
		lat1: 28.040604,
		lng0: -87.320216,
		lng1: -92.321442
	}, {
		name: "LNX",
		lat0: 44.613091,
		lat1: 39.293237,
		lng0: -97.669421,
		lng1: -103.472899
	}, {
		name: "LOT",
		lat0: 44.244492,
		lat1: 38.953889,
		lng0: -85.194406,
		lng1: -90.965973
	}, {
		name: "LRX",
		lat0: 43.345942,
		lat1: 38.124568,
		lng0: -113.949233,
		lng1: -119.645278
	}, {
		name: "LSX",
		lat0: 41.229030,
		lat1: 36.159755,
		lng0: -87.913331,
		lng1: -93.443449
	}, {
		name: "LTX",
		lat0: 36.370578,
		lat1: 31.598743,
		lng0: -75.821844,
		lng1: -81.027482
	}, {
		name: "LVX",
		lat0: 40.479904,
		lat1: 35.460968,
		lng0: -83.201836,
		lng1: -88.677039
	}, {
		name: "LWX",
		lat0: 41.514852,
		lat1: 36.425893,
		lng0: -74.697575,
		lng1: -80.249166
	}, {
		name: "LZK",
		lat0: 37.241796,
		lat1: 32.421437,
		lng0: -89.628333,
		lng1: -94.886905
	}, {
		name: "MAF",
		lat0: 34.270107,
		lat1: 29.607417,
		lng0: -99.641479,
		lng1: -104.728049
	}, {
		name: "MAX",
		lat0: 44.741225,
		lat1: 39.411086,
		lng0: -119.803809,
		lng1: -125.618506
	}, {
		name: "MBX",
		lat0: 51.365927,
		lat1: 45.409246,
		lng0: -97.609485,
		lng1: -104.107682
	}, {
		name: "MHX",
		lat0: 37.180046,
		lat1: 32.363198,
		lng0: -74.244248,
		lng1: -79.498993
	}, {
		name: "MKX",
		lat0: 45.666209,
		lat1: 40.259959,
		lng0: -85.597224,
		lng1: -91.494951
	}, {
		name: "MLB",
		lat0: 30.351949,
		lat1: 25.865896,
		lng0: -78.202983,
		lng1: -83.096859
	}, {
		name: "MOB",
		lat0: 32.975119,
		lat1: 28.374518,
		lng0: -85.726397,
		lng1: -90.745234
	}, {
		name: "MPX",
		lat0: 47.633729,
		lat1: 42.054124,
		lng0: -90.516509,
		lng1: -96.603351
	}, {
		name: "MQT",
		lat0: 49.400619,
		lat1: 43.650924,
		lng0: -84.406572,
		lng1: -90.678967
	}, {
		name: "MRX",
		lat0: 38.613983,
		lat1: 33.713104,
		lng0: -80.724338,
		lng1: -86.070752
	}, {
		name: "MSX",
		lat0: 49.937900,
		lat1: 44.133548,
		lng0: -110.813714,
		lng1: -117.145734
	}, {
		name: "MTX",
		lat0: 43.889678,
		lat1: 38.626754,
		lng0: -109.571528,
		lng1: -115.312900
	}, {
		name: "MUX",
		lat0: 39.632511,
		lat1: 34.668461,
		lng0: -119.184827,
		lng1: -124.600154
	}, {
		name: "MVX",
		lat0: 50.451645,
		lat1: 44.593704,
		lng0: -94.124431,
		lng1: -100.514912
	}, {
		name: "MXX",
		lat0: 34.879358,
		lat1: 30.186106,
		lng0: -83.225778,
		lng1: -88.345690
	}, {
		name: "NKX",
		lat0: 35.271403,
		lat1: 30.558024,
		lng0: -114.465782,
		lng1: -119.607649
	}, {
		name: "NQA",
		lat0: 37.765846,
		lat1: 32.915337,
		lng0: -87.222859,
		lng1: -92.514324
	}, {
		name: "OAX",
		lat0: 43.948970,
		lat1: 38.681452,
		lng0: -93.488018,
		lng1: -99.234400
	}, {
		name: "OHX",
		lat0: 38.695453,
		lat1: 33.789630,
		lng0: -83.882640,
		lng1: -89.234447
	}, {
		name: "OKX",
		lat0: 43.476892,
		lat1: 38.245599,
		lng0: -70.005810,
		lng1: -75.712675
	}, {
		name: "OTX",
		lat0: 50.613197,
		lat1: 44.738121,
		lng0: -114.416071,
		lng1: -120.825246
	}, {
		name: "PAH",
		lat0: 39.542673,
		lat1: 34.584314,
		lng0: -86.062936,
		lng1: -91.472055
	}, {
		name: "PBZ",
		lat0: 43.128805,
		lat1: 37.923727,
		lng0: -77.374137,
		lng1: -83.052404
	}, {
		name: "PDT",
		lat0: 48.517290,
		lat1: 42.854418,
		lng0: -115.758010,
		lng1: -121.935689
	}, {
		name: "POE",
		lat0: 33.462557,
		lat1: 28.839038,
		lng0: -90.449875,
		lng1: -95.493715
	}, {
		name: "PUX",
		lat0: 40.980598,
		lat1: 35.928215,
		lng0: -101.420561,
		lng1: -106.932252
	}, {
		name: "RAX",
		lat0: 38.095503,
		lat1: 33.225645,
		lng0: -75.829285,
		lng1: -81.141857
	}, {
		name: "RGX",
		lat0: 42.322306,
		lat1: 37.176341,
		lng0: -116.649430,
		lng1: -122.263211
	}, {
		name: "RIW",
		lat0: 45.768515,
		lat1: 40.353644,
		lng0: -105.518509,
		lng1: -111.425641
	}, {
		name: "RLX",
		lat0: 40.827451,
		lat1: 35.785383,
		lng0: -78.968197,
		lng1: -84.468635
	}, {
		name: "RTX",
		lat0: 48.542499,
		lat1: 42.877201,
		lng0: -119.868684,
		lng1: -126.049009
	}, {
		name: "SFX",
		lat0: 45.810279,
		lat1: 40.391868,
		lng0: -109.724575,
		lng1: -115.635569
	}, {
		name: "SGF",
		lat0: 39.715140,
		lat1: 34.745826,
		lng0: -90.684949,
		lng1: -96.106019
	}, {
		name: "SHV",
		lat0: 34.791121,
		lat1: 30.102354,
		lng0: -91.279232,
		lng1: -96.394250
	}, {
		name: "SJT",
		lat0: 33.683842,
		lat1: 29.049733,
		lng0: -97.960088,
		lng1: -103.015480
	}, {
		name: "SOX",
		lat0: 36.194813,
		lat1: 31.432530,
		lng0: -115.034063,
		lng1: -120.229281
	}, {
		name: "SRX",
		lat0: 37.710231,
		lat1: 32.862956,
		lng0: -91.713625,
		lng1: -97.001561
	}, {
		name: "TBW",
		lat0: 29.935526,
		lat1: 25.466348,
		lng0: -79.960204,
		lng1: -84.835671
	}, {
		name: "TFX",
		lat0: 50.379867,
		lat1: 44.529494,
		lng0: -108.187572,
		lng1: -114.569796
	}, {
		name: "TLH",
		lat0: 32.687486,
		lat1: 28.100175,
		lng0: -81.822663,
		lng1: -86.827002
	}, {
		name: "TLX",
		lat0: 37.753488,
		lat1: 32.903695,
		lng0: -94.628250,
		lng1: -99.918932
	}, {
		name: "TWX",
		lat0: 41.537639,
		lat1: 36.447109,
		lng0: -93.450722,
		lng1: -99.004027
	}, {
		name: "TYX",
		lat0: 46.489440,
		lat1: 41.012603,
		lng0: -72.687656,
		lng1: -78.662387
	}, {
		name: "UDX",
		lat0: 46.875430,
		lat1: 41.364550,
		lng0: -99.818058,
		lng1: -105.829927
	}, {
		name: "UEX",
		lat0: 42.910719,
		lat1: 37.721845,
		lng0: -95.606990,
		lng1: -101.267579
	}, {
		name: "VAX",
		lat0: 33.191151,
		lat1: 28.580464,
		lng0: -80.482887,
		lng1: -85.512728
	}, {
		name: "VBX",
		lat0: 37.243859,
		lat1: 32.423379,
		lng0: -117.762269,
		lng1: -123.020974
	}, {
		name: "VNX",
		lat0: 39.205103,
		lat1: 34.267923,
		lng0: -95.430503,
		lng1: -100.816517
	}, {
		name: "VTX",
		lat0: 36.805550,
		lat1: 32.009728,
		lng0: -116.558738,
		lng1: -121.790544
	}, {
		name: "VWX",
		lat0: 40.774690,
		lat1: 35.736146,
		lng0: -84.971122,
		lng1: -90.467715
	}, {
		name: "YUX",
		lat0: 34.836262,
		lat1: 30.145207,
		lng0: -112.092976,
		lng1: -117.210491
	}];
};


WeatherLoader.prototype = new google.maps.OverlayView;

WeatherLoader.prototype.onAdd = function() {

};

WeatherLoader.prototype.onRemove = function() {
	this.unload();
};

WeatherLoader.prototype.draw = function() {
	/*
    var projection = this.getProjection();
    var position = projection.fromLatLngToDivPixel(this.getMap().getCenter());

    var div = this.div_;
    div.style.left = position.x + 'px';
    div.style.top = position.y + 'px';
    div.style.display = 'block';
    */
	this.unload();
	this.load();
};

WeatherLoader.prototype.lineRectangleIntersect = function (x0, y0, x1, y1) {
	var top_intersection = 0;
    var bottom_intersection = 0;
    var toptrianglepoint = 0;
    var bottomtrianglepoint = 0;
    var m = 0;
    var c = 0;
    var startX = 0;
    var endX = 0;

    // Calculate m and c for the equation for the line (y = mx+c)
    m = (y1-y0)/(x1-x0);
  	c = y0 -(m*x0);

    if (x0 < x1) {
    	startX = x0;
    	endX = x1;
    }
    else {
    	startX = x1;
    	endX = x0;
    }
    var x = 0;
    var y = 0;
    var hit = false;
    var intersect = false;
    for (x=startX; x<endX; x++) {
    	y = (m*x) + c;
    	if (y > 0 && y < map.height && x > 0 && x < map.width) {
        	intersect = true;
        	break;
        }
	}
    return intersect;
};

WeatherLoader.prototype.inViewport = function(code) {
	
		// bounds of map viewport
		var bounds = map.getBounds();
		var p0 = bounds.getNorthEast();
		var p1 = bounds.getSouthWest();
		var p2 = new google.maps.LatLng(p0.lat(), p1.lng());
		var p3 = new google.maps.LatLng(p1.lat(), p0.lng());
		var polygon = [ p0, p1, p2, p3 ];
	    var numPoints = polygon.length;
	
		// bounds of radar site	
		var index = this.nwsIndexTable[ code ];
		var radarObj = this.nwsRadarTable[index];
		var r0 = new google.maps.LatLng(radarObj.lat0, radarObj.lng0);
		var r1 = new google.maps.LatLng(radarObj.lat1, radarObj.lng1);
		var r2 = new google.maps.LatLng(radarObj.lat0, radarObj.lng1);
		var r3 = new google.maps.LatLng(radarObj.lat1, radarObj.lng0);
		var radarPts = [ r0, r1, r2, r3 ];
	
        var inPoly = false;
       	var i = 0;
        var j = numPoints-1;
		var vertex1 = undefined;
		var vertex2 = undefined;
		
		for (k=0; k<radarPts.length; k++) {
			pt = radarPts[k];
			
			//
			// process polygon
			//
			for (i=0; i<polygon.length; i++) {
				vertex1 = polygon[i];
				vertex2 = polygon[j];
				
				if (vertex1.lat() < pt.lng() && vertex2.lng() >= pt.lng() || vertex2.lng() < pt.lng() && vertex1.lng() >= pt.lng()) {
					if (vertex1.lat() + (pt.lng() - vertex1.lng()) / (vertex2.lng() - vertex1.lng()) * (vertex2.lat() - vertex1.lat()) < pt.lat()) {
						inPoly = !inPoly;
					}
				}
				j = i;
			}
			j = numPoints-1;
			
			if (inPoly) {
				break;
			}
		}
        return inPoly;
};

WeatherLoader.prototype.unload = function(){
	var i = 0;
	var radarImage = undefined;
	
	for (i=0; i<this.loadedList.length; i++) {
		radarImage = this.loadedList[i];	
		radarImage.setMap(null);
	}
	// clear out list
	while (this.loadedList.length > 0) {
		this.loadedList.pop();
	}
};

WeatherLoader.prototype.load = function () {
	var i = 0;
	var urlRequest = null;
	var nwsUrl = "http://radar.weather.gov/ridge/RadarImg";
	var imageType = "N0R";
	var radarCode = null;

	for (i=0; i<this.nwsRadarTable.length; i++) {	
		currentRadarObj = this.nwsRadarTable[i];
		radarCode = currentRadarObj.name;
		
		//
		// catch sites that are NOT on the map
		//
		if (this.inViewport(radarCode)) {
			//
			// process sites on the map as of now
			//
			var newUrl = nwsUrl + "/" + imageType + "/" + radarCode + "_" + imageType + "_0.gif";
			// var newUrl ="http://www.lib.utexas.edu/maps/historical/newark_nj_1922.jpg";
			console.log("load: lat0=" + currentRadarObj.lat0 + " lng0=" + currentRadarObj.lng0 + " lat1=" + currentRadarObj.lat1 + " lng1=" + currentRadarObj.lng1);
			var imageBounds = new google.maps.LatLngBounds(new google.maps.LatLng(currentRadarObj.lat1, currentRadarObj.lng1), 
				new google.maps.LatLng(currentRadarObj.lat0, currentRadarObj.lng0));
			var radarImage = new google.maps.GroundOverlay(newUrl, imageBounds);
			radarImage.setMap(map);
			this.loadedList.push(radarImage);
			console.log(radarCode + ": Adding radar image to map --> " + radarImage + " URL: " + newUrl);
		}
	}
};
