package github.jadetang.index;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午4:18
 */
public class HashIndex implements QueueIndex {

    private ConcurrentHashMap<Integer, Integer> map;

    public HashIndex() {
        map = new ConcurrentHashMap<>();
    }

    public int ceil(int messageOffset) {
        throw new UnsupportedOperationException("HashIndex do not support this method.");
    }

    public int find(int messageOffset) {
        return map.getOrDefault(messageOffset, -1);
    }

    public void addEntry(int messageOffset, int fileOffset) {
        map.putIfAbsent(messageOffset, fileOffset);
    }
}
