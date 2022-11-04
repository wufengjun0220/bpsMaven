package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dcfs.esb.ftp.client.FtpClientConfig;
import com.dcfs.esb.ftp.client.FtpGet;
import com.dcfs.esb.ftp.server.error.FtpException;
import com.mingtech.framework.common.util.FTPUtil;

public class TestFtp extends TestCase {
	private final static Log log = LogFactory.getLog(TestFtp.class);
	public String filePath ="c:\\temp\\";

	public void testHKB() {
		String config = "config/FtpClientConfig.properties";
		FtpClientConfig.loadConf(config);
		// 上传文件
		FtpGet ftpput = null;
		try {
			ftpput = new FtpGet("/mac.txt", "down/mac.txt", false);
			long l = 0;
			l = System.currentTimeMillis();
			ftpput.doGetFile(); //上传文件，返回文件路径+文件名
			System.out.println("上传花费时间为： "+(System.currentTimeMillis() - l ) + " ms");
		} catch (FtpException e) {
			if (log.isErrorEnabled()) {
				log.error("文件上传出错", e);
			}
		} finally {
			if (ftpput != null)
				ftpput.close();
		}
	}
	
	public void testFtpDownload(){
		FTPUtil ftp = new FTPUtil("10.28.110.187", "zrc", "zrcbank123", 21);
		//FTPUtil ftp = new FTPUtil("172.30.139.2", "zrc", "zrcbank123", 21);
		//boolean b = ftp.connectServer();
		//System.out.println(b);
		String fileName1 = "CDCS_ACCT_BAL_DATA-100035-20171204.tar"; 
		String fileName2 = "CDCS_STL_DETAIL_DATA-100035-20171204.tar";
		String fileName3 = "CDCS_STL_TOTAL_DATA-100035-20171204.tar";
//		boolean a = ftp.downloadFile("\\", fileName1, filePath+fileName1);
//		boolean b = ftp.downloadFile("\\", fileName2,filePath+fileName2);
//		boolean c = ftp.downloadFile("\\", fileName3, filePath+fileName3);
//		System.out.println(a + "===" +b + "===" + c);
		
//		this.unGzipFile("c:\\temp\\DRAFTLIST-100035-20171204-1000000000.dat.gz");
		
		
//		readFileByLines("c:\\temp\\CDCS_STL_DETAIL_DATA-100035-20171204.dat");
//		readFileByLines("c:\\temp\\CDCS_ACCT_BAL_DATA-100035-20171204.dat");
//		parseFile("CDCS_STL_TOTAL_DATA-100035-20171204.tar",new PjsTotalData());
//		parseFile("CDCS_STL_DETAIL_DATA-100035-20171204.tar",new PjsDetailData());
//		parseFile("CDCS_ACCT_BAL_DATA-100035-20171204.tar",new PjsBalData());
//		parseFile("DRAFTLIST-100035-20171204.tar",new PjsBalData());
//		List gzList = new ArrayList();
//		this.extTarFileList(filePath + "DRAFTLIST-100035-20171204.tar", filePath,gzList);
//		String unGzipFile1 = this.unGzipFile(filePath+"DRAFTLIST-100035-20171204-1000000000.dat.gz");
//		System.out.println(unGzipFile1);
//		String unGzipFile2 = this.unGzipFile(filePath+"DRAFTLIST-100035-20171204-2000000000.dat.gz");
//		System.out.println(unGzipFile2);
//		String unGzipFile3 = this.unGzipFile(filePath+"DRAFTLIST-100035-20171204-3000000000.dat.gz");
//		System.out.println(unGzipFile3);
//		String unGzipFile4 = this.unGzipFile(filePath+"DRAFTLIST-100035-20171204-4000000000.dat.gz");
//		System.out.println(unGzipFile4);
	}
	
	
	
	public void parseFile(String fileName,Object obj){
		List gzList = new ArrayList();
		List datList = new ArrayList();
		boolean b = this.extTarFileList(filePath + fileName, filePath,gzList);
		if (b) {
			for (Object object : gzList) {
				String gzFile = (String) object;
				System.out.println(gzFile);
				String unGzipFile = this.unGzipFile(filePath+gzFile);
				System.out.println(unGzipFile);
				datList.add(unGzipFile);
			}
		}
//		for (Object object : datList) {
//			String dataFile = (String) object;
//			readFileByLines( dataFile,obj);
//		}
	}
	
	
	/**
	 * 读取文件  
	 * @param fileName
	 */
	private static void readFileByLines(String fileName,Object obj){
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			while((tempString = reader.readLine()) != null){
				
				insertData(tempString,obj);
				line++;
			}
			reader.close();
		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	private static void insertData(String line,Object obj){
		char a = 27;
		String[] split = line.split(String.valueOf(a));
		/*if (obj instanceof PjsBalData) {
			PjsBalData pjs = new PjsBalData();
			pjs.setBranchno(split[0]);
			pjs.setPjszjzhzh(split[1]);
			pjs.setCqye(new BigDecimal(split[2]));
			pjs.setJffse(new BigDecimal(split[3]));
			pjs.setJffsbs(split[4]);
			pjs.setDffse(new BigDecimal(split[5]));
			pjs.setDffsbs(split[6]);
			pjs.setMqye(new BigDecimal(split[7]));
		}
		if (obj instanceof PjsDetailData) {
			PjsDetailData pjs = new PjsDetailData();
			pjs.setJgdbh(split[0]);
			pjs.setJslx(split[1]);
			pjs.setJsfs(split[2]);
			pjs.setQslx(split[3]);
			pjs.setJsje(new BigDecimal(split[4]));
			pjs.setSfkfx(split[5]);
			pjs.setJsjg(split[6]);
		}
		if (obj instanceof PjsTotalData) {
			PjsTotalData pjs = new PjsTotalData();
			pjs.setBranchno(split[0]);
			pjs.setYwlx(split[1]);
			pjs.setSfkfx(split[2]);
			pjs.setJsfs(split[3]);
			pjs.setQslx(split[4]);
			pjs.setJsjg(split[5]);
			if (split[6].equals("0") || split[6].equals("")) {
				pjs.setPmze(new BigDecimal( 0 ));
			}else{
				pjs.setPmze(new BigDecimal( split[6] ));
			}
			pjs.setPjzs(split[7]);
			pjs.setJsbs(split[8]);
			if (split.length == 10) {
				pjs.setJsje(new BigDecimal(split[9]));
			}
			
		}*/
			
	}
	
	
	
	/**
	 * 解压tar包
	 * 
	 * @param filename
	 *            tar文件
	 * @param directory
	 *            解压目录
	 * @return
	 */
	private static boolean extTarFileList(String filename, String directory,List list) {/*
		boolean flag = false;
		OutputStream out = null;
		TarInputStream in = null;
		FileOutputStream fileOut = null;
		try {
			in = new TarInputStream(new FileInputStream(new File(filename)));
			TarEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}
				//System.out.println(entry.getName());
				list.add(entry.getName());
				File outfile = new File(directory + entry.getName());
				new File(outfile.getParent()).mkdirs();
				out = new BufferedOutputStream(new  FileOutputStream(outfile));
				int x = 0;
				byte[] b = new byte[2048];
				while ((x = in.read(b)) != -1) {
					out.write(b,0,x);
				}
				
				out.close();
			}
			in.close();

			flag = true;
		} catch (IOException ioe) {
			iologger.error(e.getMessage(),e);
			flag = false;
		}
		return flag;
	*/
		return false;
	}
	
	public static String unGzipFile(String sourcedir) {
        String ouputfile = "";
        try {  
            //建立gzip压缩文件输入流 
            FileInputStream fin = new FileInputStream(sourcedir);   
            //建立gzip解压工作流
            GZIPInputStream gzin = new GZIPInputStream(fin);   
            
            //建立解压文件输出流  
            ouputfile = sourcedir.substring(0,sourcedir.lastIndexOf('.'));
            FileOutputStream fout = new FileOutputStream(ouputfile);   
            
            int num;
            byte[] buf=new byte[512];

            while ((num = gzin.read(buf,0,buf.length)) != -1)
            {   
                fout.write(buf,0,num);
            }
            gzin.close();   
            fout.close();   
            fin.close();   
        } catch (Exception ex){  
            System.err.println("--" + ex.toString());  
        }  
        return ouputfile;
    }
	
	
    
    /** 
     * 构建目录 
     * @param outputDir 
     * @param subDir 
     */  
    public static void createDirectory(String outputDir,String subDir){     
        File file = new File(outputDir);  
        if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空  
            file = new File(outputDir + "/" + subDir);  
        }  
        if(!file.exists()){  
              if(!file.getParentFile().exists())
                  file.getParentFile().mkdirs();
            file.mkdirs();  
        }  
    }
	
}
