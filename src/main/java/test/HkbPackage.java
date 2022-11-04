package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class HkbPackage {
	
	/*
	 * java路径
	 */
	private static String addFilePath = "E:/workspace/workspace-HKB-2/bps/";
	private static String Path="E:/workspace/workspace-HKB-2/bps/src/test/HkbPackageJavaPatch.txt";
	private static String folderPath="E:/backup/hkbPack/java";

	
	/*
	 * class路径
	 */	
//	private static String addFilePath = "E:/workspace/workspace-SXB-SC/spmMicSrv/WebRoot/WEB-INF/classes/";
//	private static String Path="E:/backup/code/spmClass.txt";
//	private static String folderPath="E:/backup/code/All/des/spmMicSrv/WEB-INF/classes/";
	

	
	private static BufferedReader reader=null;
	private static int fileCounts=0;
	public static void init(){
		try {
			reader=new BufferedReader(new FileReader(Path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		init();
		List<String> list=queryDD();
		run(list);
		File file=new File(folderPath);
		countFile(file);
		System.out.println("确认后文件总数为："+fileCounts);
	}
	
	public static void run(List<String> list){
		String line;
		String newPath=null;
		String addPath;
		int fileCount=0;
		deleteDir(new File(folderPath));
		for(int i=0;i<list.size();i++){
			line=list.get(i);
			if(line.contains("--")) continue;
			if(!line.contains(".")) continue;
			newPath=folderPath+line.substring(0,line.lastIndexOf("/"));
			File newFile= new File(newPath);
			newFile.delete();
			if(!newFile.exists()){
				newFile.mkdirs();
			}
			newPath=folderPath+line;
			addPath=addFilePath+line;
			copyFile(addPath,newPath);
			fileCount++;
		}
		System.out.println("文件总数："+fileCount);
	}
	public static void run2(){
		String line;
		String newPath;
		String addPath;
		int fileCount=0;
		try {
			while((line=reader.readLine())!=null){
				newPath=folderPath+line.substring(0,line.lastIndexOf("/"));
				File newFile=new File(newPath);
				if(!newFile.exists()){
					newFile.mkdirs();
				}
				newPath=folderPath+line;
				(new File(newPath)).createNewFile();
				addPath=addFilePath+line;
				copyFile(addPath,newPath);
				fileCount++;
			}
			System.out.println("总文件数为："+fileCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void countFile(File file){
		File list[]=file.listFiles();
		for(int i=0;i<list.length;i++){
			if(list[i].isFile()){
				fileCounts++;
			}else{
				countFile(list[i]);
			}
		}
	}
	public static void copyFile(String oldPath,String newPath){
		System.out.println("起始路径："+oldPath);
		//System.out.println("复制文件："+newPath);
		List<Object> list=new ArrayList<Object>();
		try {
			int bytesum=0;
			int byteread=0;
			File oldFile=new File(oldPath);
			if(oldFile.exists()==false){
				System.out.println("不存在文件："+oldFile);
			}
			if(oldFile.exists()){
				if(list.contains(newPath)){
					list.add(newPath);
				}else{
			//		System.out.println("重复文件："+newPath);
				}
				InputStream inStream=new FileInputStream(oldPath);
				FileOutputStream fs=new FileOutputStream(newPath);
				byte[] buffer=new byte[1444];
				int length;
				while((byteread=inStream.read(buffer))!=-1){
					bytesum+=byteread;
					fs.write(buffer,0,byteread);
				}
				
				inStream.close();
			}
		}  catch (Exception e) {
			System.out.println("复制文件出错");
			e.printStackTrace();
		}
	}
	private static boolean deleteDir(File dir){
		if(dir.isDirectory()){
			String[] children=dir.list();
			for(String str:children){
				File deleteFile=new File(dir,str);
				boolean success=deleteDir(deleteFile);
			}
		}
		return dir.delete();
	}
	public static List<String> queryDD(){
		List<String> list=new ArrayList<String>();
		String line;
		String newPath=null;
		List<String> clist=new ArrayList<String>();
		try {
			while((line=reader.readLine())!=null){
				if(line.contains("--"))	continue;
				if(!line.contains(".")) continue;
				if(list.contains(line)){
					clist.add(line);
				}else{
					list.add(line);
				}
			}
			System.out.println("文件总数为"+list.size());
		} catch (Exception e) {
			System.out.println("出错文件为"+newPath);
			e.printStackTrace();
		}
		if(clist.size()>0){
			for(String str:clist)
				System.out.println("重复文件为："+str);
		}
		return list;
	}
	
}
