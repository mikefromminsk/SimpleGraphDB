import java.util.ArrayList;

public class Tree {
	
	Nodes nodes;
	Nodes links;
	MetaNodes metaNodes;
	InfinityFile FileI;
	
	public Tree(String path) throws Throwable{
		nodes = new Nodes(path,MetaNode.DATA);
		links = new Nodes(path,MetaNode.LINKS);
		metaNodes = new MetaNodes(path);
		
		FileI = new InfinityFile(path + "\\.tree");
	}
	
	public void Add(String data) throws Throwable{
		if(data.length()==0)
			throw new Throwable("Пустая строка");
		
		String hash = new CRC16(data).getHash();
		
		long pos = 0;
		boolean flagAv = true;
		for(int i=0;i<4;i++){
			int num = Integer.parseInt(new String(new char[]{hash.charAt(i)}), 16);
			
			if(FileI.GetFullSize()<pos+num*8){
				for(int j=0;j<16;j++)
					FileI.Add(to8Chars(0));
			}
			
			long BPos = Long.parseLong(FileI.Read(pos+num*8, 8));
			if((BPos==0 && i<3)||(BPos==0 && !flagAv)){
				long Size = FileI.GetFullSize();
				for(int j=0;j<16;j++)
					FileI.Add(to8Chars(0));
				if(i<3)
					FileI.Edit(pos+num*8,to8Chars(Size));
				else{
					FileI.Edit(pos+num*8,to8Chars(metaNodes.GetFullSize()));  }
				if(i<3) flagAv=false;
			}
			
			
			pos = Long.parseLong(FileI.Read(pos+num*8, 8));

		}
		
		if(!flagAv){
			NodesData n = nodes.Add(data);
			NodesData l = links.Add(to8Chars(metaNodes.GetFullSize()+21));
			metaNodes.Add(new MetaNode(hash, MetaNode.LINKS, l.Start, l.Size));
			
			metaNodes.Add(new MetaNode(hash, MetaNode.DATA, n.Start, n.Size));
		}else{
			String[] arr = Read(hash);
			for(int i=0;i<arr.length;i++)
				if(arr[i].equals(data))
					return;
			
			NodesData n = nodes.Add(data);
			String p = to8Chars(metaNodes.Add(new MetaNode(hash,MetaNode.DATA,n.Start,n.Size)));
			MetaNode d = new MetaNode(metaNodes.Read(pos));

			
			if(d.Size>=(links.Read(new NodesData(d.Start,d.Size)) + p).length())
				links.Edit(d.Start, links.Read(new NodesData(d.Start,d.Size)) + p);
			else{
				NodesData newData = links.Add(links.Read(new NodesData(d.Start,d.Size)) + p);
				metaNodes.Edit(pos, new MetaNode(d.Id,d.Type,newData.Start,newData.Size));
			}
		}		
	}
	
	public void AddSame(String data) throws Throwable{
		if(data.length()==0)
			throw new Throwable("Пустая строка");
		
		String hash = new CRC16(data).getHash();
		
		long pos = 0;
		boolean flagAv = true;
		for(int i=0;i<4;i++){
			int num = Integer.parseInt(new String(new char[]{hash.charAt(i)}), 16);
			
			if(FileI.GetFullSize()<pos+num*8){
				for(int j=0;j<16;j++)
					FileI.Add(to8Chars(0));
			}
			
			long BPos = Long.parseLong(FileI.Read(pos+num*8, 8));
			if((BPos==0 && i<3)||(BPos==0 && !flagAv)){
				long Size = FileI.GetFullSize();
				for(int j=0;j<16;j++)
					FileI.Add(to8Chars(0));
				if(i<3)
					FileI.Edit(pos+num*8,to8Chars(Size));
				else{
					FileI.Edit(pos+num*8,to8Chars(metaNodes.GetFullSize()));  }
				if(i<3) flagAv=false;
			}
			
			
			pos = Long.parseLong(FileI.Read(pos+num*8, 8));

		}
		
		if(!flagAv){
			NodesData n = nodes.Add(data);
			NodesData l = links.Add(to8Chars(metaNodes.GetFullSize()+21));
			metaNodes.Add(new MetaNode(hash, MetaNode.LINKS, l.Start, l.Size));
			
			metaNodes.Add(new MetaNode(hash, MetaNode.DATA, n.Start, n.Size));
		}else{		
			NodesData n = nodes.Add(data);
			String p = to8Chars(metaNodes.Add(new MetaNode(hash,MetaNode.DATA,n.Start,n.Size)));
			MetaNode d = new MetaNode(metaNodes.Read(pos));

			
			if(d.Size>=(links.Read(new NodesData(d.Start,d.Size)) + p).length())
				links.Edit(d.Start, links.Read(new NodesData(d.Start,d.Size)) + p);
			else{
				NodesData newData = links.Add(links.Read(new NodesData(d.Start,d.Size)) + p);
				metaNodes.Edit(pos, new MetaNode(d.Id,d.Type,newData.Start,newData.Size));
			}
		}		
	}
	
	public String[] Read(String hash) throws Throwable{
		
		ArrayList<String> list = new ArrayList<String>();
		
		long pos = 0;
		for(int i=0;i<4;i++){
			int num = Integer.parseInt(new String(new char[]{hash.charAt(i)}), 16);
			pos = Integer.parseInt(FileI.Read(pos+num*8, 8));
		}
		
		
		MetaNode metaDataLinks = new MetaNode(metaNodes.Read(pos));
		String[] L = links.Read(new NodesData(metaDataLinks.Start,metaDataLinks.Size)).split("(?<=\\G.{8})");
		
		
		for(int i=0;i<L.length;i++){
			if(!L[i].equals("        ")){
				MetaNode metaDataLink = new MetaNode(metaNodes.Read(Long.valueOf(L[i])));
				list.add(nodes.Read(new NodesData(metaDataLink.Start,metaDataLink.Size)));
			}
		}
		
		return list.toArray(new String[list.size()]);
	}
	
	
	private String to8Chars(long i){
		String str = String.valueOf(i);
		while(str.length()<8)
			str= "0" + str;
		return str;
	}
	
}

class TreeNode{
	
	private String Id;
	private int Type;
	private String[] Data;
	
	public TreeNode(String Id,int Type, String[] Data) {
		this.Id = Id;
		this.Type = Type;
		this.Data = Data;
	}
	
	public String getId() {
		return Id;
	}
	
	public void setId(String id) {
		Id = id;
	}

	public String getType() {
		if(Type==MetaNode.DATA)
			return "Массив строк";
		else
			return "Массив ссылок";
	}

	public void setType(int type) {
		Type = type;
	}

	public String[] getData() {
		return Data;
	}

	public void setData(String[] data) {
		Data = data;
	}
	
	public String getDataString() {
		String buffer = "";
		for(int i=0;i<Data.length-1;i++)
			buffer+=Data[i] + "|";
		if(Data.length>0)
			buffer += Data[Data.length-1];
		return buffer;
	}
	
	public String toString() {
		String buffer = String.valueOf(Id) + " ";
		buffer += getType() + " ";
		buffer += getDataString();
		
		return buffer;
	}
	
}
