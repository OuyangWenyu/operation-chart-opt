package com.wenyu.service.excelutil.fromdbway2;

import java.io.IOException;

import com.wenyu.service.excelutil.ExcelTool;


public class ExcelTest3 {
	public static final int startYear = 1953;// 起始年份
	public static final int startPeriod = 6;// 从6月开始
	public static final int N = 10;// 方案数
	public static final int[][] DAYS = { { 30, 31, 31, 30, 31, 30, 31, 31, 28, 31, 30, 31 },
			{ 30, 31, 31, 30, 31, 30, 31, 31, 29, 31, 30, 31 } };

	public static void main(String[] args) throws IOException {
		
		
		String packagePath = "result";// 结果表格存放路径
		String [][] name = new String[2][3];
		String[] a = {"长系列", "典型年"};
		String[] aa = {"长系列 - 副本", "典型年 - 副本"};
		String[] b = {"POA兼顾保证出力-", "POA年发电量最大-", "如美单库-"};
		for (int i = 0; i < name.length; i++) {
			for (int j = 0; j < name[i].length; j++) {
				name[i][j] = packagePath + "/" + aa[i] + "/" + b[j] + a[i];
				
			}
//			System.out.println(Arrays.toString(name[i]));
		}
		String[][][] finalName = new String[2][3][];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				if (i == 0 && j == 0)
					finalName[i][j] = new String[10];
				else
					finalName[i][j] = new String[6];
				for (int k = 0; k < finalName[i][j].length; k++) {
					finalName[i][j][k] = name[i][j] + "/" + "梯级运行过程" + k + ".xlsx";
				}
//				System.out.println(Arrays.toString(finalName[i][j]));
//				System.exit(0);
			}
		}
		
		// 流量取整数，水位水头发电量取两位小数，出力取一位小数
		String[] tables = { "入库流量", "出库流量", "发电流量", "弃水流量", "水位", "水头", "发电量", "出力" };
		String[] tables2 = { "入库流量", "出库流量", "发电流量", "弃水流量", "月初水位", "净水头", "发电量(万kW·h)", "出力(万kW)" };
		String[] stations= { "如美", "邦多", "古学", "白塔", "古水", "乌弄龙", "里底", "托巴", "黄登",
				"大华桥", "苗尾", "功果桥" };
		String[] schemes = {"无如美", "2865-2785", "2875-2795", "2885-2805", "2895-2815", "2905-2825", 
				"2895-2805", "2895-2815(死水位)", "2895-2825", "2895-2835"};
		String[] heads = {"" , "年份", "6月" , "7月", "8月", "9月", "10月", "11月", "12月", "1月","2月", "3月","4月", "5月"};
		String[] heads2 = {"", "" , "天数", "6月" , "7月", "8月", "9月", "10月", "11月", "12月", "1月","2月", "3月","4月", "5月"};
		String[] headsForYear = {"10%", "25%", "50%", "75%", "90%"};
//		for (int i = 0; i < heads2.length; i++) {
//			if (i == 0)
//				heads2[i] = "";
//			else if (i == 1) 
//				heads2[i] = "年份";
//			else
//				heads2[i] = "" + (i - 1);
//		}
		int number = 1;
		// 长系列还是典型年2
		for (int i = 0; i < 1; i++) {
			// 那种算法5
			for (int j = 0; j < 1; j++) {
				// 那种方案，定位到每一个excel表格finalName[i][j].length
				for (int k = 0; k < finalName[i][j].length; k++) {
//					if ( k > 0 &&k < 5) {
//						continue;
//					}
					// sheet(流量，出力),电站，53年的数据
					double [][][] temp = new double[tables.length][][];
					System.out.println("**********第" + number++ + "个表格**********");
					System.out.println("读取表格" + finalName[i][j][k]);
					for (int m = 0; m < tables.length; m++)
						temp[m] = ExcelTool.readDoubleFrom07Excel(finalName[i][j][k], tables[m]);
					// 长系列
					if (i == 0) {
						// sheet(流量出力)， 电站，每一年的数据,8
						Object[][][] object = new Object[tables2.length][660][14];
						// 数据
						for (int x = 0 ; x < object.length; x++) {
							int index = 0;
							// 电站
							for (int y = 0; y < 12; y++) {
								if (index % 55 == 0) {
									heads[0] = stations[index / 55];
									for (int q = 0; q < heads.length; q++) {
										object[x][index][q] = heads[q];
									}
									index++;
								}
								Object[][] oo = getArray(temp[x][y]);
								for (int p = 0; p < 53; p++) {
									object[x][index++] = oo[p];
								}
								index++;
							}
						}
						System.out.println("写入表格...");
						for (int x = 0; x < object.length; x++) {
//							// 去除班达电站
//							if (x == 0)
//								continue;
//							if (j == 4 && x != 1)
//								continue;
							ExcelTool.reWrite07Excel(name[i][j] + "/" + "梯级运行过程" + schemes[k] + ".xlsx", tables2[x], object[x]);
						}
						System.out.println("写入完成!!!");
					} 
					// 典型年
					else {
						// sheet(流量出力)， 电站，每一年的数据
						Object[][][] object = new Object[tables2.length][396][16 * 5];
						// 每个项目
						for (int x = 0 ; x < object.length; x++) {
							// 每年，一共5年
							int index = 0;
							int dayNum = 0;
							for (int year = 0; year < 5; year++) {
								double[] tempArray = new double[year > 1 ? 365 :366];
								if (index % 396 == 0)
									heads2[0] = headsForYear[index / 396];
								// 每组数据的表头
								for (int y = 0; y < 12; y++) {
									if (index % 33 == 0) {
										heads2[1] = stations[(index % 396 / 33)];
										for (int q = 0 + 16 * year; q < heads2.length + 16 * year; q++) {
											object[x][index % 396][q] = heads2[q - 16 * year];
										}
										index++;
									}
									// 每年一个项目，一个电站
									System.arraycopy(temp[x][y], dayNum, tempArray, 0,tempArray.length);
									Object[][] oo = getArray2(tempArray, year);
									for (int p = 0; p < 31; p++) {
										for (int q = 0 + 16 * year; q < 16 * (year + 1) - 1; q++) {
											object[x][index % 396][q] = oo[p][q - 16 * year];
										}
										index++;
									}
									// 空行
									index++;
								}
								dayNum += tempArray.length;
								
							}
						}
						System.out.println("写入表格...");
						for (int x = 0; x < object.length; x++) {
//							// 去除班达电站
//							if (x == 0)
//								continue;
//							if (j == 4 && x != 1)
//								continue;
							ExcelTool.reWrite07Excel(name[i][j] + "/" + "梯级运行过程" + schemes[k] + ".xlsx", tables2[x], object[x]);
						}
						System.out.println("写入完成!!!");
					}
				}
			}
			System.out.println("GG simida!");
		}
		
//		double temp[][] = ExcelTool.readDoubleFrom07Excel(finalName[0][0][0], "入库流量");
//		for (int i = 0; i < temp.length; i++) {
//			System.out.println(Arrays.toString(temp[i]));
//		}
//		String[] fileNames = new String[N];
//		for (int i = 0; i < N; i++) {
//			fileNames[i] = "梯级运行过程POA" + i + "";
//		}
//		
//		String[] filePaths = new String[N];
//		for (int i = 0; i < N; i++) {
//			filePaths[i] = packagePath + fileNames[i] + ".xlsx";// 表格扩展名
//		}
//		
//		String[] fileTableNames = { "入库流量", "出库流量", "发电流量", "弃水流量", "水位", "水头", "发电量", "出力" };
//		int fileTables = fileTableNames.length;// 每张表的表格总数，0入库流量，1出库流量，2发电流量，3弃水流量，4水位，5水头，6发电量，7出力
//		String[] stationNames = { "4.班达", "5.如美", "6.邦多", "7.古学", "8.白塔", "9.古水", "10.乌弄龙", "11.里底", "12.托巴", "13.黄登",
//				"14.大华桥", "15.苗尾", "16.功果桥" };
	}
	
	public static Object[][] getArray(double[] array) {
		Object[][] object = new Object[53][14];
		for (int i = 0; i < object.length; i++) {
			for (int j = 0; j < object[i].length; j++) {
				if (j == 0)
					object[i][j] = null;
				else if (j == 1)
					object[i][j] = 1953 + i;
				else
					object[i][j] = (Object) array[i * 12 + j - 2];
			}
		}
		return object;
	}
	
	public static Object[][] getArray2(double[] array, int year) {
		int flag = year > 1 ? 0 : 1;
		Object[][] object = new Object[31][15];
		Object[][] temp = new Object[12][31];
		int index = 0;
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < DAYS[flag][i]; j++) {
				temp[i][j] = array[index++];
			}
		}
		Object[][] transposedArray = transpose(temp);
		for (int i = 0; i < object.length; i++) {
			object[i][2] = i + 1;
			for (int j = 3; j < object[i].length; j++) {
				object[i][j] = transposedArray[i][j - 3];		
			}
		}
		return object;
	}
	
	public static Object[][] transpose(Object[][] source) {
		Object[][] result = new Object[source[0].length][source.length];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = source[j][i];
			}
		}
		return result;
	}
	
}
