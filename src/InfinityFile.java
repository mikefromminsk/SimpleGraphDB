import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;



public class InfinityFile {

	private String Path;
	private long Size;
	
	private TreeMap<String,String> treeCache;
	
	private String[] Names;
	
	private long maxSizeCache = 16000;
	private long maxFragmentCache = 4000;
	
	private boolean flagClear = false;
	
	private ArrayList<RandomAccessFile> ListThreads; 
	
	public InfinityFile(String Path, long Size) throws Throwable{
		
		if(!new File(Path).isDirectory())
			Files.createDirectory(Paths.get(Path));
		
		if(new File(Path,".settings").isFile())
			throw new Throwable("Данный репозиторий уже существует");
		
		this.Path = Path;
		this.Size = Size;
		
		treeCache = new TreeMap<String,String>();
		treeCache.put("Size", "0");
		
		Names = new String[0];
		ListThreads = new ArrayList<RandomAccessFile>();
		
		File file = new File(Path, Names.length + ".dat");
		file.createNewFile();
		AddDataNames(String.valueOf(Names.length));
		for(int i=0;i<Names.length;i++)
			ListThreads.add(new RandomAccessFile(new File(Path, Names[i]+ ".dat"), "rw"));
		
		SaveSettings();
	}
	
	public InfinityFile(String Path) throws Throwable{
		this.Names = new String[0];
		
		this.Path = Path;
		if(!new File(Path,".settings").isFile())
			throw new Throwable("Репозиторий не найден");
		FileInputStream fis = new FileInputStream(new File(Path,".settings"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

		String line = reader.readLine();
		Size = Integer.parseInt(line);
		while(line!=null){
			line = reader.readLine();
			if(line!=null)
				AddDataNames(line);
		}
		reader.close();
		
		treeCache = new TreeMap<String,String>();
		ListThreads = new ArrayList<RandomAccessFile>();
		for(int i=0;i<Names.length;i++)
			ListThreads.add(new RandomAccessFile(new File(Path, Names[i]+ ".dat"), "rw"));
		
		OpenCacheSettings();
	}
		
	private void AddDataNames(String Name) throws Throwable {
		if(this.Names == null)
			this.Names = new String[0];
		
		String[] Names = new String[this.Names.length+1];
		for(int i=0;i<this.Names.length;i++)
			Names[i] = this.Names[i];
		Names[this.Names.length] = Name;
		this.Names = Names;
		
		SaveSettings();
	}
	
	private void SaveSettings() throws Throwable{
				if(!new File(Path,".settings").isFile())
					new File(Path,".settings").createNewFile();
				
				String Data = toString();
				
				FileOutputStream fos = new FileOutputStream(new File(Path,".settings"));
			    fos.write(Data.getBytes());
			    fos.close();
			    
			    SaveCacheSettings();
	}
	
	public void Add(String Data) throws Throwable{
            while(Data.length()!=0){
            	RandomAccessFile writer = ListThreads.get(Names.length-1);
            	
                writer.seek(GetFullSize()%Size);
            	
            	int s = (int)(Size-writer.length());
            	if(s>Data.length()) s = Data.length();
            	
            	String buffer = Data.substring(0, s);
            	writer.writeBytes(buffer);
            	Data = Data.substring(s);
            	if(writer.length()>=Size){
            		File file = new File(Path, Names.length + ".dat");
    				file.createNewFile();
    				AddDataNames(String.valueOf(Names.length));
    				ListThreads.add(new RandomAccessFile
    						(new File(Path, Names[Names.length-1]+ ".dat"), "rw"));
            	}
            	
            	if(treeCache.containsKey("Size"))
            		treeCache.put("Size", String.valueOf(Long.parseLong(treeCache.get("Size"))+s));
            	for(int i=s-1;i>=0;i--)
            		treeCache.put(String.valueOf(GetFullSize()-i), new String(new char[] {buffer.charAt(i)}));
            	
            	testCacheSize();
            }		
	}
	
	public void Edit(long Start,String Data) throws Throwable{
        	if(GetFullSize() < Start + Data.length())
        		throw new Throwable("Выход за предел данных"); 
			for(int i=0;i<Data.length();i++){
				RandomAccessFile writer = ListThreads.get((int) ((Start+i)/Size));
				writer.seek((Start+i)%Size);
				writer.writeByte(Data.charAt(i));
				
				treeCache.put(String.valueOf(Start+i), new String(new char[] {Data.charAt(i)}));
			}
	}
	
	public String Read(long Start,long Size) throws Throwable{
		if(GetFullSize() < Start + Size) 
				throw new Throwable("Выход за предел данных");
		String buffer = "";
		for(int i=0;i<Size;i++){
				RandomAccessFile reader = ListThreads.get((int) ((Start+i)/this.Size));
				reader.seek((Start+i)%this.Size);
				buffer += new String(new byte[]{reader.readByte()}); 
		}
		return buffer;
	}
	
	public String toString(){
		String Data = String.valueOf(Size) + System.lineSeparator();
		for(int i=0;i<Names.length;i++)
			Data+=Names[i]+System.lineSeparator();
		
		return Data;
	}
	
	public long GetSize(){return Size;}
	
	public long GetFullSize() throws Throwable{
		if(Names.length == 0)
			return 0;
		//if(treeCache.containsKey("Size")) 
			//return Long.parseLong(treeCache.get("Size"));
    	RandomAccessFile writer = ListThreads.get(Names.length-1);
    	long lengthFile = Size * (Names.length-1) + writer.length();
    	
    	treeCache.put("Size", String.valueOf(lengthFile));
    	return lengthFile;
	}
	
	private void ChangeCacheSize() throws Throwable {
    	RandomAccessFile writer = ListThreads.get(Names.length-1);
    	long lengthFile = Size * (Names.length-1) + writer.length();
    	
    	treeCache.put("Size", String.valueOf(lengthFile));
	}
	
	private void testCacheSize() throws Throwable {
		if((GetFullSize() == maxSizeCache || Names.length == maxFragmentCache)&&!flagClear) {
			cacheClear();
			flagClear=true;
		}
	}
	
	public void cacheClear() throws Throwable {
		FileOutputStream fos = new FileOutputStream(new File(Path,"cache.dat"));
		for(Map.Entry e : treeCache.entrySet()) {				
			String data = e.getKey() + " " + e.getValue() + System.lineSeparator();
			fos.write(data.getBytes());
		}
		fos.close();
		treeCache.clear();
	}
	
	private void SaveCacheSettings() throws Throwable {
		FileOutputStream fos = new FileOutputStream(new File(Path,"cache.settings"));
		fos.write((maxSizeCache + System.lineSeparator()).getBytes());
		fos.write((maxFragmentCache + System.lineSeparator()).getBytes());
		fos.close();
	}
	
	private void OpenCacheSettings() throws Throwable {
		FileInputStream fis = new FileInputStream(new File(Path,"cache.settings"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		maxSizeCache = Long.valueOf(reader.readLine());
		maxFragmentCache = Long.valueOf(reader.readLine());
	}

	public long getMaxSizeCache() {
		return maxSizeCache;
	}

	public void setMaxSizeCache(long maxSizeCache) throws Throwable {
		this.maxSizeCache = maxSizeCache;
		SaveCacheSettings();
		if(maxSizeCache>this.maxSizeCache) flagClear = true;
	}

	public long getMaxFragmentCache() {
		return maxFragmentCache;
	}

	public void setMaxFragmentCache(long maxFragmentCache) throws Throwable {
		this.maxFragmentCache = maxFragmentCache;
		SaveCacheSettings();
		if(maxFragmentCache>this.maxFragmentCache) flagClear = true;
	}
	
	public void Close() throws Throwable{
		for(int i=0;i<ListThreads.size();i++)
			ListThreads.get(i).close();
	}
	
	public void finalize() throws Throwable {
		Close();
	}
}
