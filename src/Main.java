import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        while (true) {
            System.out.println("1.Создать репозиторий");
            System.out.println("2.Перейти в репозиторий");
            System.out.println("3.Выход");
            System.out.print("\nВведите пункт меню:");

            int num = new Scanner(System.in).nextInt();
            boolean flag = false;

            switch (num) {
                case 1:
                    CreateRep();
                    break;
                case 2:
                    ToRep();
                    break;
                case 3:
                    flag = true;
                    break;
            }

            if (flag) break;

        }

    }

    public static void CreateRep() {
        try {
            System.out.print("Введите путь №1 для создания репозитория:");
            String path1 = new Scanner(System.in).nextLine();
            System.out.print("Введите путь №2 для создания репозитория:");
            String path2 = new Scanner(System.in).nextLine();
            System.out.print("Введите максимальный размер части:");
            int size = new Scanner(System.in).nextInt();
            DiskManager ds = new DiskManager(path1, path2, size);
            System.out.println("Репозиторий №" + ds.GetNameManager() + " успешно создан");

            ToRep(ds.GetNameManager());
        } catch (Throwable e) {
            System.out.println("\n" + e.getMessage() + "\n");
        }
    }

    public static void ToRep(String name) {
        DiskManager ds;
        try {
            ds = new DiskManager(name);
        } catch (Throwable e1) {
            System.out.println("Ошибка менеджера дисков.");
            return;
        }

        while (true) {
            System.out.println("\n1.Добавить узел");
            System.out.println("2.Изменить настройки кэша");
            System.out.println("3.Вывести список строк");
            System.out.println("4.Добавить бесконечный файл");
            System.out.println("5.Добавить данные в бесконечный файл");
            System.out.println("6.Прочитать данные с бесконечного файла");
            System.out.println("7.Назад");
            System.out.print("\nВведите пункт меню:");

            int num = new Scanner(System.in).nextInt();
            boolean flag = false;

            try {

                switch (num) {
                    case 1:
                        System.out.print("Введите строку:");
                        String addStr = new Scanner(System.in).nextLine();
                        ds.Add(addStr);
                        break;
                    case 2:
                        System.out.print("Введите размер бесконечного файла:");
                        long maxSizeCache = new Scanner(System.in).nextLong();
                        System.out.print("Введите количество фрагментов:");
                        long maxFragmentCache = new Scanner(System.in).nextLong();
                        ds.ChangeCacheSetting(maxSizeCache, maxFragmentCache);
                        break;
                    case 3:
                        System.out.print("Введите идентификатор:");
                        String id = new Scanner(System.in).nextLine();
                        if (id.length() != 4) {
                            System.out.println("Строка несоответствует формату");
                        }
                        String[] data = ds.Read(id);
                        for (String el : data)
                            System.out.println(el);
                        break;
                    case 4:
                        System.out.print("Введите имя будущего бесконечного файла:");
                        String nameNewFile = new Scanner(System.in).nextLine();
                        System.out.print("Введите размер части будущего бесконечного файла:");
                        int sizeNewFile = new Scanner(System.in).nextInt();
                        ds.CreateInfinityFile(nameNewFile, sizeNewFile);
                        break;
                    case 5:
                        System.out.print("Введите имя бесконечного файла:");
                        String nameFile = new Scanner(System.in).nextLine();
                        System.out.print("Введите данные для записи:");
                        String dataAddFile = new Scanner(System.in).nextLine();
                        ds.AddInfinityData(nameFile, dataAddFile);
                        break;
                    case 6:
                        System.out.print("Введите имя бесконечного файла:");
                        String nameFileRead = new Scanner(System.in).nextLine();
                        System.out.print("Введите начало:");
                        long StartFileRead = new Scanner(System.in).nextLong();
                        System.out.print("Введите размер:");
                        long SizeFileRead = new Scanner(System.in).nextLong();
                        System.out.println(ds.ReadInfinity(nameFileRead, StartFileRead, SizeFileRead));
                        break;
                    case 7:
                        ds.Close();
                        flag = true;
                        break;
                }
            } catch (Throwable e) {
                System.out.println("\n" + e.getMessage() + "\n");
                e.printStackTrace();
            }

            if (flag) break;

            System.out.println("\n\n" + name + "\n");

        }
    }

    public static void ToRep() {
        System.out.print("Введите номер репозитория:");
        String name = new Scanner(System.in).nextLine();
        ToRep(name);
    }

    public static String to8byte(long val) {
        String buffer = String.valueOf(val);
        for (int i = buffer.length(); i < 8; i++)
            buffer = "0" + buffer;
        return buffer;
    }

    public static void ClearDir(String Path) {
        File index = new File(Path);
        String[] entries = index.list();
        for (String s : entries) {
            File currentFile = new File(index.getPath(), s);
            currentFile.delete();
        }
    }

}
