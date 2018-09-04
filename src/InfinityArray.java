import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;


public class InfinityArray {
	
	InfinityFile FileI;
	String path;
	
	public InfinityArray(String path,int meta) throws Throwable{
		if(meta == MetaNode.DATA)
			this.path = path+"\\.nodes";
		else
			this.path = path+"\\.links";
		
		FileI = new InfinityFile(this.path);
	}
	
	public InfinityArray(InfinityFile file,MetaNodes Meta){
		FileI = file;
	}
	
	public NodesData Add(String str) throws Throwable{
		String bStr = str;
		
		int Size = 1;
		while(Size<bStr.length())
			Size *= 2;
		while(Size > bStr.length())
			bStr += " ";
		
		long Start = FileI.GetFullSize();
		
		if(new File(path,Size + ".gc").isFile()) {
			long StartBuf = Start;
			
			RandomAccessFile reader = new RandomAccessFile(new File(path,Size + ".gc"), "rw");
			if(reader.length()>0) {
				byte[] buf = new byte[8];
				reader.read(buf);
				StartBuf = Long.parseLong(new String(buf));
				
				FileOutputStream fos = new FileOutputStream(new File(path,Size + ".buf"));
				for(long i=8;i<reader.length();i++) {
					reader.seek(8);
					fos.write(new byte[] {reader.readByte()});
				}
				fos.close();	
			}
			reader.close();
			new File(path,Size + ".gc").delete();
			new File(path,Size + ".buf").renameTo(new File(path,Size + ".gc"));
			
			if(StartBuf!=Start) {
				Start = StartBuf;
				FileI.Edit(Start, bStr);
				return new NodesData(Start,bStr.length());
			}
		}
		
		FileI.Add(bStr);
		
		return new NodesData(Start,bStr.length());
	}
	
	public long GetFullSize() throws Throwable{
		return FileI.GetFullSize();
	}
	
	public NodesData Edit(long pos, String str) throws Throwable{
		String bStr = str;
		
		int Size = 1;
		while(Size<bStr.length())
			Size *= 2;
		while(Size > bStr.length())
			bStr += " ";
		
		FileI.Edit(pos, bStr);
		
		return new NodesData(pos,bStr.length());
	}
	
	public String Read(NodesData data) throws Throwable{
		return FileI.Read(data.Start, data.Size);
	}
	
	public void AddGC(String Start,long Size) throws Throwable {
		FileOutputStream fos = new FileOutputStream(new File(path,Size + ".gc"));
		fos.write(Start.getBytes());
		fos.close();
	}
	
	public void Close() throws Throwable{
		FileI.Close();
	}
	
	public void finalize() throws Throwable {
		Close();
	}
	
	public void ChangeCacheSetting(long maxSizeCache,long maxFragmentCache) throws Throwable{
		FileI.setMaxSizeCache(maxSizeCache);
		FileI.setMaxFragmentCache(maxFragmentCache);
	}
}


