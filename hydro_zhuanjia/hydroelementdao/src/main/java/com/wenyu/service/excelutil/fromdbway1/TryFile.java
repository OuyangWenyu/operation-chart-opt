package com.wenyu.service.excelutil.fromdbway1;

import java.io.File;

public class TryFile {
	
	/**
	 * 如果文件夹不存在就新建文件夹
	 * @param folderName  文件夹名字
	 */
	public static void createFileFolder(String folderName)
	{
		File file =new File(folderName);    
		//如果文件夹不存在则创建    
		if  (!file .exists()  && !file .isDirectory())      
		{       
		    System.out.println("文件夹不存在，新建");  
		    file .mkdir();    
		} else   
		{  
		    System.out.println("目录存在");  
		}  
	}
}
