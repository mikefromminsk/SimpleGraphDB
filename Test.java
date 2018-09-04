import java.util.Scanner;

public interface Test {
	public boolean StartTest();
}

class InfinityTest implements Test{
	
	public boolean StartTest() {
		try {
			System.out.print("¬ведите путь к репозиторию:");
			String Path = new Scanner(System.in).nextLine();
			
			System.out.print("¬ведите максимальный размер части:");
			int Size = new Scanner(System.in).nextInt();
			
			InfinityFile file = new InfinityFile(Path,Size);
			
			boolean result = true;
			
			file.Add("TestOne");
			if(!file.Read(0, "TestOne".length()).equals("TestOne"))
				result = false;
			
			file.Edit(0, "TestTwo");
			if(!file.Read(0, "TestTwo".length()).equals("TestTwo"))
				result = false;
			
			file.Add("TestThree");
			if(!file.Read(0, "TestTwoTestThree".length()).equals("TestTwoTestThree"))
				result = false;
			
			file.Edit("TestTwo".length(), "TestFour");
			if(!file.Read(0, "TestTwoTestFour".length()).equals("TestTwoTestFour"))
				result = false;
			
			Main.ClearDir(Path);
			
			return result;
		}catch(Throwable e) {
			System.out.println(e.getMessage());
			return false;
		}	
	}
	
}

class NodesTest implements Test{
	
	public boolean StartTest() {
		try {
			System.out.print("¬ведите путь к репозиторию:");
			String Path = new Scanner(System.in).nextLine();
			
			System.out.print("¬ведите максимальный размер части:");
			int Size = new Scanner(System.in).nextInt();
			
			Nodes nodes = new Nodes(new InfinityFile(Path,Size));
			
			boolean result = true;
			
			nodes.Add(1, MetaNode.DATA, new String[] {"TestOne"});
			if(!nodes.Read(1)[0].equals("TestOne"))
				result = false;
			
			nodes.Edit(1, new String[] {"TestOne","TestTwo"});
			if(!nodes.Read(1)[0].equals("TestOne") || !nodes.Read(1)[1].equals("TestTwo"))
				result = false;
			
			nodes.Add(2, MetaNode.LINKS, new String[] {"1"});
			if(!nodes.Read(2)[0].equals("1"))
				result = false;
			
			Main.ClearDir(Path);
			
			return result;
		}catch(Throwable e) {
			System.out.println(e.getMessage());
			return false;
		}	
	}
	
}

class TreeTest implements Test{
	
	public boolean StartTest() {
		try {
			System.out.print("¬ведите путь к репозиторию:");
			String Path = new Scanner(System.in).nextLine();
			
			System.out.print("¬ведите максимальный размер части:");
			int Size = new Scanner(System.in).nextInt();
			
			Tree tree = new Tree(new InfinityFile(Path,Size));
			
			boolean result = true;
			
			tree.Add(new String[] {"1"});
			if(!tree.ReadOnId(1)[0].equals("1"))
				result = false;
			
			tree.Add(new String[] { "2" });
			if(!tree.ReadOnId(2)[0].equals("2"))
				result = false;
			
			for(int i=3;i<=5;i++)
				tree.Add(new String[] {String.valueOf(i)});
			
			for(int i=1;i<=5;i++)
				if(!tree.ReadOnId(i%4)[i/4].equals(String.valueOf(i)))
					result = false;
			
			Main.ClearDir(Path);
			
			return result;
		}catch(Throwable e) {
			System.out.println(e.getMessage());
			return false;
		}	
	}
	
}
