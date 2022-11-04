package test;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class MakePatch{
  //生成的war包名
  private static String jarRoot="bps.war";
  //编译目录
  private static File build=new File("WebRoot/WEB-INF/classes");
  private static File patchList=new File("patch.txt");
  private static File patch=new File("patch.zip");
  private static File svnpatch=new File("svn.zip");

  public static void main(String...args) throws IOException, ClassNotFoundException {
	  makeaddpath();
	  //makesvnpath();
  }
  
  
  private static void makeaddpath() throws IOException,ClassNotFoundException{
	  FileInputStream fis=new FileInputStream(patchList);
	    InputStreamReader isr=new InputStreamReader(fis);
	    BufferedReader br=new BufferedReader(isr);
	    String line;
	    Map<Class,Map<String,File>> classMap=refAll();
	    Map<String,File> map=new HashMap<String,File>();
	    while((line=br.readLine())!=null){
	      if(line.trim().length()==0) continue;
	      //去掉根路径cmis
	      String path=line.trim().replaceAll("^[^\\\\\\/]+[\\\\\\/]","");
	      File source=new File(path);
	      if(path.startsWith("WebRoot")){
	        path=path.replaceAll("^[^\\\\\\/]+[\\\\\\/]","");
	        map.put(jarRoot+"/"+path,source);
	      }else if(path.endsWith(".java")){
	        path=path.replaceAll("^[^\\\\\\/]+[\\\\\\/]","");
	        String className=path.replaceAll("\\.java$","");
	        //去掉导出编译后的多余路径
	        //className = className.replaceAll("/src", "");
	        
	        className=className.replaceAll("[\\\\\\/]",".");
	        //System.out.println(className);
	        Class clazz=Class.forName(className);
	        map.putAll(classMap.get(clazz));
	      }else{
	        path=path.replaceAll("^[^\\\\\\/]+[\\\\\\/]","");
	        //path = path.replaceAll("main/java/", "");
	        //path=path.replaceAll("main/config/","");
	        map.put(jarRoot+"/WEB-INF/classes/"+path,source);
	      }
	    }
	    byte[] buffer=new byte[1024*1024];
	    FileOutputStream fos=new FileOutputStream(patch);
	    JarOutputStream jar=new JarOutputStream(fos);
	    Iterator<Entry<String,File>> it=map.entrySet().iterator();
	    while(it.hasNext()){
	      Entry<String,File> entry=it.next();
	      String path=entry.getKey();
	      File file=entry.getValue();
	      JarEntry jarEntry=new JarEntry(path);
	      jar.putNextEntry(jarEntry);
	      FileInputStream is=new FileInputStream(file);
	      int len;
	      while((len=is.read(buffer))>0)
	        jar.write(buffer,0,len);
	      is.close();
	      jar.closeEntry();
	    }
	    jar.close();
	    System.out.println("本次增量包已打包结束,因在所有类的构造函数中存在静态块中单起线程，所以本进程并没结束");
  }

  /*
   * 定义所有的类 
   * map:{类对象={String对象=File对象}
   * EX:{class CancleJianZhiChongZheng.AddRscTaskInfoByBillNo={cmis.war/WEB-INF/classes/CancleJianZhiChongZheng/AddRscTaskInfoByBillNo.class=WebContent\WEB-INF\classes\CancleJianZhiChongZheng\AddRscTaskInfoByBillNo.class}, 
         class com.ecc.echain.ext.CusManagerUserSelectClass={cmis.war/WEB-INF/classes/com/ecc/echain/ext/CusManagerUserSelectClass.class=WebContent\WEB-INF\classes\com\ecc\echain\ext\CusManagerUserSelectClass.class}}
   */
  private static Map<Class,Map<String,File>> refAll() throws ClassNotFoundException{
    Map<Class,Map<String,File>> map=new HashMap<Class,Map<String,File>>();
    iteratorClass(build,"",map);
    return map;
  }

  /*
   * 考虑
   */
  private static void iteratorClass(File root,String prefix,Map<Class,Map<String,File>> map) throws ClassNotFoundException{
	  if(root.exists()){
      if(root.isDirectory()){
        File[] files=root.listFiles();
        for(int i=0;i<files.length;i++){
          File child=files[i];
          String name=child.getName();
          //如果有子目录
          if(child.isDirectory()) iteratorClass(child,prefix+name+".",map);
          else if(name.endsWith("class")){
            String className=prefix+child.getName().replaceAll("\\.class$","");
            try{
            	System.out.println(className);
              Class clazz=Class.forName(className);
              Map m2=map.get(clazz);
              if(m2==null){
                m2=new HashMap();
                map.put(clazz,m2);
              } else{
            	  System.out.println(m2);
              }
              String path=jarRoot+"/WEB-INF/classes/"+prefix.replaceAll("\\.","/")+name;
              //path：生成的路径  child：生成的class文件对象
              m2.put(path,child);
            }catch(Throwable e){
              // logger.error(e.getMessage(),e);
            }
          }
        }
      }
    }
  }

  private static Class getRoot(Class clazz){
    Class root=clazz.getEnclosingClass();
    if(root==null) return clazz;
    else return getRoot(root);
  }
  
  
  private static void makesvnpath()throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		FileInputStream fis=new FileInputStream(patchList);
		InputStreamReader isr=new InputStreamReader(fis);
		BufferedReader br=new BufferedReader(isr);
		String line;
		byte[] buffer=new byte[1024*1024];
		
		
		FileOutputStream fos=new FileOutputStream(svnpatch);
		JarOutputStream jar=new JarOutputStream(fos);
		
		while((line=br.readLine())!=null){
			line=line.trim().replaceAll("^[^\\\\\\/]+[\\\\\\/]","");
			File file = new File(line);
			JarEntry jarEntry=new JarEntry("svn/cmis/"+line);
		    jar.putNextEntry(jarEntry);
		    
		    FileInputStream is=new FileInputStream(file);
		    
			int len;
		    while((len=is.read(buffer))>0)
		    	jar.write(buffer,0,len);
		    is.close();
		    jar.closeEntry();
		}
		jar.close();
  }
}
