import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;



public class InfinityFile {

	private String Path;
	private long Size;
	
	private String[] Names;
	
	public InfinityFile(String Path, long Size) throws Throwable{
		
		if(!new File(Path).isDirectory())
			Files.createDirectory(Paths.get(Path));
		
		if(new File(Path,".settings").isFile())
			throw new Throwable("Данный репозиторий уже существует");
		
		this.Path = Path;
		this.Size = Size;
		
		Names = new String[0];
		
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
	
	private void SaveSettings() throws IOException{
				if(!new File(Path,".settings").isFile())
					new File(Path,".settings").createNewFile();
				
				String Data = toString();
				
				FileOutputStream fos = new FileOutputStream(new File(Path,".settings"));
			    fos.write(Data.getBytes());
			    fos.close();
				
		
	}
	
	public void Add(String Data) throws Throwable{
			if(!new File(Path,Names.length + ".dat").isFile() && 
					!new File(Path,Names.length-1 + ".dat").isFile()){
				File file = new File(Path, Names.length + ".dat");
				file.createNewFile();
				AddDataNames(String.valueOf(Names.length));
			}
            while(Data.length()!=0){
            	File file = new File(Path, Names.length-1 + ".dat");
            	RandomAccessFile writer = new RandomAccessFile(file, "rw");
            	writer.seek(writer.length());
            	int s = (int)(Size-writer.length());
            	if(s>Data.length()) s = Data.length();
            	writer.writeBytes(Data.substring(0, s));
            	Data = Data.substring(s);
            	if(writer.length()>=Size){
            		file = new File(Path, Names.length + ".dat");
    				file.createNewFile();
    				AddDataNames(String.valueOf(Names.length));
            	}
            	writer.close();
            }		
	}
	
	public void Edit(long Start,String Data) throws Throwable{
        	if(GetFullSize() < Start + Data.length())
        		throw new Throwable("Выход за предел данных"); 
			for(int i=0;i<Data.length();i++){
				File file = new File(Path, (Start+i)/Size + ".dat");
				RandomAccessFile writer = new RandomAccessFile(file, "rw");
				writer.seek((Start+i)%Size);
				writer.writeByte(Data.charAt(i));
				writer.close();
			}
	}
	
	public String Read(long Start,long Size) throws Throwable{
		if(GetFullSize() < Start + Size)
        	throw new Throwable("Выход за предел данных");
		String buffer = "";
		for(int i=0;i<Size;i++){
			File file = new File(Path, (Start+i)/this.Size + ".dat");
			RandomAccessFile reader = new RandomAccessFile(file, "rw");
			reader.seek((Start+i)%this.Size);
			buffer += new String(new byte[]{reader.readByte()}); 
			reader.close();
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
		File file = new File(Path, Names.length-1 + ".dat");
    	RandomAccessFile writer = new RandomAccessFile(file, "rw");
    	long lengthFile = Size * (Names.length-1) + writer.length();
    	writer.close();
    	return lengthFile;
	}
}
