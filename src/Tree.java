import refactored.tree.CRC16;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Tree {

    public static final int NullNode = 1;

    private InfinityArray nodes;
    private InfinityArray links;
    private MetaNodes metaNodes;
    private InfinityFile FileI;

    RandomAccessFile SettingRootStream;

    public Tree(String path) throws Throwable {
        nodes = new InfinityArray(path, MetaNode.DATA);
        links = new InfinityArray(path, MetaNode.LINKS);
        metaNodes = new MetaNodes(path);

        FileI = new InfinityFile(path + "\\.tree");

        SettingRootStream = new RandomAccessFile(new File(path + "\\.tree", "root.settings"), "rw");
    }

    public void Add(String data) throws Throwable {
        if (data.length() == 0)
            throw new Throwable("Пустая строка");

        String hash = new CRC16(data).getHash();

        if (FileI.GetFullSize() == 0) {
            NodesData n = nodes.Add(data);
            NodesData l = links.Add(to8Chars(metaNodes.GetFullSize() + 21));

            metaNodes.Add(new MetaNode(hash, MetaNode.DATA, n.Start, n.Size));

            for (int j = 0; j < 16; j++)
                FileI.Add(to8Chars(NullNode));


            FileI.Edit(Long.parseLong(new String(new char[]{hash.charAt(2)}), 16) * 8,
                    to8Chars(metaNodes.Add(new MetaNode(hash, MetaNode.LINKS, l.Start, l.Size))));

            ChangeRootNode(GetHashLevel(hash, 3) + to8Chars(FileI.GetFullSize()));

            long pos = FileI.GetFullSize() + Integer.parseInt(new String(new char[]{hash.charAt(3)}), 16) * 8;

            for (int j = 0; j < 16; j++)
                FileI.Add(to8Chars(NullNode));

            FileI.Edit(pos, to8Chars(0));
        } else {
            int divergence = -1;
            String LastRootNode = ReadRootNode().substring(0, 4);
            for (int i = 0; i < LastRootNode.length(); i++) {
                if (LastRootNode.charAt(i) == '*') break;

                if (hash.charAt(i) != LastRootNode.charAt(i)) {
                    divergence = i;
                    break;
                }
            }
            if (divergence == -1) {

                long pos = Long.parseLong(ReadRootNode().substring(4, 12));

                boolean flagAv = true;

                for (int i = GetLevelNode(LastRootNode); i < 4; i++) {
                    int num = Integer.parseInt(new String(new char[]{hash.charAt(i)}), 16);

                    if (FileI.GetFullSize() < pos + num * 8) {
                        for (int j = 0; j < 16; j++)
                            FileI.Add(to8Chars(NullNode));
                    }

                    long BPos = Long.parseLong(FileI.Read(pos + num * 8, 8));
                    if ((BPos == NullNode && i < 3) || (BPos == NullNode && !flagAv)) {
                        long Size = FileI.GetFullSize();
                        for (int j = 0; j < 16; j++)
                            FileI.Add(to8Chars(NullNode));
                        if (i < 3)
                            FileI.Edit(pos + num * 8, to8Chars(Size));
                        else {
                            FileI.Edit(pos + num * 8, to8Chars(metaNodes.GetFullSize()));
                        }
                        if (i < 3) flagAv = false;
                    }


                    pos = Long.parseLong(FileI.Read(pos + num * 8, 8));
                }


                if (!flagAv) {
                    NodesData n = nodes.Add(data);
                    NodesData l = links.Add(to8Chars(metaNodes.GetFullSize() + 21));
                    metaNodes.Add(new MetaNode(hash, MetaNode.LINKS, l.Start, l.Size));

                    metaNodes.Add(new MetaNode(hash, MetaNode.DATA, n.Start, n.Size));
                } else {
                    String[] arr = Read(hash);
                    for (int i = 0; i < arr.length; i++)
                        if (arr[i].equals(data))
                            return;

                    NodesData n = nodes.Add(data);
                    String p = to8Chars(metaNodes.Add(new MetaNode(hash, MetaNode.DATA, n.Start, n.Size)));
                    MetaNode d = new MetaNode(metaNodes.Read(pos));


                    if (d.Size >= (links.Read(new NodesData(d.Start, d.Size)) + p).length())
                        links.Edit(d.Start, links.Read(new NodesData(d.Start, d.Size)) + p);
                    else {
                        NodesData newData = links.Add(links.Read(new NodesData(d.Start, d.Size)) + p);
                        links.AddGC(to8Chars(d.Start), d.Size);
                        metaNodes.Edit(pos, new MetaNode(d.Id, d.Type, newData.Start, newData.Size));
                    }
                }

            } else {
                long buf = 0;
                for (int i = GetLevelNode(LastRootNode) - 1; i >= divergence; i--) {
                    buf = FileI.GetFullSize() +
                            8 * Integer.parseInt(new String(new char[]{LastRootNode.charAt(i)}), 16);
                    for (int j = 0; j < 16; j++)
                        FileI.Add(to8Chars(NullNode));
                    FileI.Edit(buf, ReadRootNode().substring(4, 12));

                    ChangeRootNode(GetHashLevel(LastRootNode, divergence) +
                            to8Chars(FileI.GetFullSize() - 8 * 16));
                }
                Add(data);
            }
        }
    }

    public int GetLevelNode(String hash) {
        while (hash.length() != 0 && hash.charAt(hash.length() - 1) == '*') {
            hash = hash.substring(0, hash.length() - 1);
        }
        return hash.length();
    }

    public void ChangeRootNode(String data) throws Throwable {
        SettingRootStream.seek(0);
        SettingRootStream.write(data.getBytes());
    }

    public String ReadRootNode() throws Throwable {
        String buffer = "";
        for (int i = 0; i < 12; i++) {
            SettingRootStream.seek(i);
            buffer += new String(new byte[]{SettingRootStream.readByte()});
        }
        return buffer;
    }

    public String GetHashLevel(String Hash, int Level) {
        Hash = Hash.substring(0, Level);
        while (Hash.length() < 4)
            Hash += "*";
        return Hash;
    }

    public String[] Read(String hash) throws Throwable {
        if (FileI.GetFullSize() == 0)
            return new String[0];

        int divergence = -1;
        String LastRootNode = ReadRootNode().substring(0, 4);
        for (int i = 0; i < LastRootNode.length(); i++) {
            if (LastRootNode.charAt(i) == '*') break;

            if (hash.charAt(i) != LastRootNode.charAt(i)) {
                divergence = i;
                break;
            }
        }

        if (divergence != -1)
            return new String[0];

        ArrayList<String> list = new ArrayList<String>();

        long pos = Long.parseLong(ReadRootNode().substring(4, 12));
        for (int i = GetLevelNode(LastRootNode); i < 4; i++) {
            int num = Integer.parseInt(new String(new char[]{hash.charAt(i)}), 16);
            pos = Integer.parseInt(FileI.Read(pos + num * 8, 8));
        }


        MetaNode metaDataLinks = new MetaNode(metaNodes.Read(pos));
        String[] L = links.Read(new NodesData(metaDataLinks.Start, metaDataLinks.Size)).split("(?<=\\G.{8})");


        for (int i = 0; i < L.length; i++) {
            if (!L[i].equals("        ")) {
                MetaNode metaDataLink = new MetaNode(metaNodes.Read(Long.valueOf(L[i])));
                list.add(nodes.Read(new NodesData(metaDataLink.Start, metaDataLink.Size)));
            }
        }

        return list.toArray(new String[list.size()]);
    }

    public void Close() throws Throwable {
        nodes.Close();
        links.Close();
        metaNodes.Close();
        FileI.Close();
        SettingRootStream.close();
    }

    public void finalize() throws Throwable {
        Close();
    }

    private String to8Chars(long i) {
        String str = String.valueOf(i);
        while (str.length() < 8)
            str = "0" + str;
        return str;
    }

    public void ChangeCacheSetting(long maxSizeCache, long maxFragmentCache) throws Throwable {
        nodes.ChangeCacheSetting(maxSizeCache, maxFragmentCache);
        links.ChangeCacheSetting(maxSizeCache, maxFragmentCache);
        metaNodes.ChangeCacheSetting(maxSizeCache, maxFragmentCache);
        FileI.setMaxSizeCache(maxSizeCache);
        FileI.setMaxFragmentCache(maxFragmentCache);
    }

}

class TreeNode {

    private String Id;
    private int Type;
    private String[] Data;

    public TreeNode(String Id, int Type, String[] Data) {
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
        if (Type == MetaNode.DATA)
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
        for (int i = 0; i < Data.length - 1; i++)
            buffer += Data[i] + "|";
        if (Data.length > 0)
            buffer += Data[Data.length - 1];
        return buffer;
    }

    public String toString() {
        String buffer = String.valueOf(Id) + " ";
        buffer += getType() + " ";
        buffer += getDataString();

        return buffer;
    }

}
