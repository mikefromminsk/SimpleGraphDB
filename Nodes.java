import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Nodes {
	
	InfinityFile FileI;
	
	public Nodes(String path,int meta) throws Throwable{
		if(meta == MetaNode.DATA)
			FileI = new InfinityFile(path+"\\.nodes");
		else
			FileI = new InfinityFile(path+"\\.links");
	}
	
	public Nodes(InfinityFile file,MetaNodes Meta){
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
}

class NodesData{
	
	public long Start;
	public long Size;
	
	public NodesData(long Start,long Size){
		this.Start = Start;
		this.Size = Size;
	}
}


