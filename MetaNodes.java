import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


public class MetaNodes {	
	InfinityFile FileI;
	
	public MetaNodes(String path) throws Throwable{
		if(!new File(path+"\\.meta").isDirectory())
			Files.createDirectory(Paths.get(path+"\\.meta"));
		
		FileI = new InfinityFile(path+"\\.meta");
	}
	
	public MetaNodes(InfinityFile file){
		FileI = file;
	}
	
	public long Add(MetaNode node) throws Throwable{
		FileI.Add(node.toString());
		return FileI.GetFullSize()-21;
	}
	
	public void Edit(long pos, MetaNode node) throws Throwable{
		FileI.Edit(pos, node.toString());
	}
	
	public String Read(long pos) throws Throwable{
		return FileI.Read(pos, 21);
	}
	
	public long GetFullSize() throws Throwable{
		return FileI.GetFullSize();
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

class MetaNode{
	
	public static final int LINKS = 0;
	public static final int DATA = 1;
	
	public String Id;
	public int Type;
	public long Start;
	public long Size;
	
	public MetaNode(String Id, int Type, long Start, long Size){
		this.Id = Id;
		this.Type = Type;
		this.Start = Start;
		this.Size = Size;
	}
	
	public MetaNode(String val){
		Id = val.substring(0,4);
		Type = Integer.parseInt(val.substring(4,5));
		Start = Integer.parseInt(val.substring(5,13));
		Size = Integer.parseInt(val.substring(13,21));
	}
	
	public String toString(){
		return Id +
			Type + Main.to8byte(Start) +
			Main.to8byte(Size);
	}
	
}