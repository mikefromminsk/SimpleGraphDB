import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Test;


public class Tests{
	
	
	@Test
	public void DiskManager(){
		String path = System.getenv("public") + "\\testDiskManager";
		String[] arrSecPath = new String[]{"first","secondary"};
		for(String elPath:arrSecPath)
			if(!new File(path+elPath).isDirectory())
				try {
					Files.createDirectory(Paths.get(path+elPath));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		int size = 200;
		
		String[] arrPath = new String[]{"\\.meta","\\.nodes","\\.links","\\.tree"};
		
		for(int j=0;j<arrSecPath.length;j++)
			for(int i=0;i<arrPath.length;i++) {
				if(new File(path+arrSecPath[j]+arrPath[i]).isDirectory())
					deleteAllFilesFolder(path+arrSecPath[j]+arrPath[i]);}
		
		try{
			DiskManager ds= new DiskManager(path+arrSecPath[0],
					path+arrSecPath[1],size);
			
			String[] arr = new String[]{"aaa","asdas","wewqew","652kd","optdmaqksmxndj"};
			
			for(int i=0;i<arr.length;i++) {
				ds.Add(arr[i]);
				Thread.sleep(100);
				assertEquals("Read element \"" + arr[i] + "\"",
						arr[i],
						entrance(arr[i],ds.Read(new CRC16(arr[i]).getHash()))); }
		}catch(Throwable e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void Infinity(){
		String path = System.getenv("public") + "\\testInfinity";
		
		if(new File(path).isDirectory())
			deleteAllFilesFolder(path);
		
		try{
			InfinityFile file = new InfinityFile(path,200);
			
			String testStr = "0123456789";
			file.Add(testStr);
			assertEquals("Read string \"" + testStr + "\"",testStr,file.Read(0, testStr.length()));
			
			testStr = "9876543210";
			file.Edit(0, testStr);
			assertEquals("Read edition string \"" + testStr + "\"",testStr,file.Read(0, testStr.length()));	
		}catch(Throwable e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void Nodes(){
		String path = System.getenv("public") + "\\testNodes";
		if(!new File(path).isDirectory())
			try {
				Files.createDirectory(Paths.get(path));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		
		if(new File(path+"\\.nodes").isDirectory())
			deleteAllFilesFolder(path+"\\.nodes");
		
		try{
			new InfinityFile(path+"\\.nodes",200);
			Nodes nodes = new Nodes(path,MetaNode.DATA);
			
			String[] arr = {"aaa","1","wewqew","652kd","optdmaqksmxndj"};
			ArrayList<NodesData> al = new ArrayList<NodesData>();
			for(int i=0;i<arr.length;i++)
				al.add(nodes.Add(arr[i]));
			
			for(int i=0;i<arr.length;i++){
				assertEquals("Read node \"" + arr[i] + "\"",
						arr[i],nodes.Read(al.get(i)).split(" ")[0]);
				}
		}catch(Throwable e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void Meta(){
		String path = System.getenv("public") + "\\testMeta";
		if(!new File(path).isDirectory())
			try {
				Files.createDirectory(Paths.get(path));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		
		if(new File(path+"\\.meta").isDirectory())
			deleteAllFilesFolder(path+"\\.meta");
		
		try{
			new InfinityFile(path+"\\.meta",200);
			MetaNodes fileMeta = new MetaNodes(path);
			
			MetaNode[] arr = new MetaNode[]
			                    {new MetaNode("1111",MetaNode.DATA,20,200),
			                     new MetaNode("1112",MetaNode.LINKS,221,20),
			                     new MetaNode("1113",MetaNode.DATA,320,210),
			                     new MetaNode("1114",MetaNode.LINKS,340,100)};
			
			for(int i=0;i<arr.length;i++)
				fileMeta.Add(arr[i]);
			
			for(int i=0;i<arr.length;i++)
				assertEquals("Read metanode \"" + arr[i].Id + "\"",
						arr[i].toString(),new MetaNode(fileMeta.Read(21*i)).toString());
		}catch(Throwable e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void Tree(){
		String path = System.getenv("public") + "\\testTree";
		if(!new File(path).isDirectory())
			try {
				Files.createDirectory(Paths.get(path));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		int size = 200;
		
		String[] arrPath = new String[]{"\\.meta","\\.nodes","\\.links","\\.tree"};
		
		for(int i=0;i<arrPath.length;i++)
			if(new File(path+arrPath[i]).isDirectory())
				deleteAllFilesFolder(path+arrPath[i]);
		
		try{
			for(int i=0;i<arrPath.length;i++)
				new InfinityFile(path+arrPath[i],size);
			Tree tree = new Tree(path);
			
			String[] arr = new String[]{"aqewr","wqe221","asdas1","231"};
			for(int i=0;i<arr.length;i++)
				tree.Add(arr[i]);
			
			for(int i=0;i<arr.length;i++)
				assertEquals("Read element \"" + arr[i] + "\"",
						arr[i],
						entrance(arr[i],tree.Read(new CRC16(arr[i]).getHash())));
		}catch(Throwable e){
			fail(e.getMessage());
		}
	}
	
	@Test
	public void WriteBufferAndActionThread(){
		String path = System.getenv("public") + "\\testWriteBufferThread";
		if(!new File(path).isDirectory())
			try {
				Files.createDirectory(Paths.get(path));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		int size = 200;
		
		String[] arrPath = new String[]{"\\.meta","\\.nodes","\\.links","\\.tree"};
		
		for(int i=0;i<arrPath.length;i++)
			if(new File(path+arrPath[i]).isDirectory())
				deleteAllFilesFolder(path+arrPath[i]);
		
		try{
			for(int i=0;i<arrPath.length;i++)
				new InfinityFile(path+arrPath[i],size);
			Tree tree = new Tree(path);
			
			String[] arr = new String[]{"aqewr","wqe221","asdas1","231"};
			ActionThread at = new ActionThread();
			WriteBufferThread wb = new WriteBufferThread(at);
			for(int i=0;i<arr.length;i++)
				wb.AddTask(new Act(tree,Act.Write,arr[i]));
			
			Thread.sleep(1000);
			
			for(int i=0;i<arr.length;i++) {
				at.AddAct(new Act(tree,Act.Read,new CRC16(arr[i]).getHash()));
				assertEquals("Read element \"" + arr[i] + "\"",
						arr[i],
						entrance(arr[i],at.getBufferData()));
			}
		}catch(Throwable e){
			fail(e.getMessage());
		}
	}
	
	private static String entrance(String el,String[] arr){
		for(int i=0;i<arr.length;i++)
			if(arr[i].length() >= el.length() && arr[i].substring(0, el.length()).equals(el))
				return el;
		return "";
	}
	
	private static void deleteAllFilesFolder(String path) {
        for (File myFile : new File(path).listFiles())
               if (myFile.isFile()) myFile.delete();
   }
	
}