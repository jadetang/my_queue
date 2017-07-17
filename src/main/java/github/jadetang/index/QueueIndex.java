package github.jadetang.index;

/**
 * A index mapping message offset to physical file offset
 *
 * @author sanguan.tangsicheng on 2017/7/16 下午3:49
 */
public interface QueueIndex {

    /**
     * return the largest message offset which is equal or less than the give message offset
     * if no such index exist, return -1;
     */
    int ceil(int messageOffset);


    /**
     * return the physical file offset corresponding the mesageOffset
     * if not such index exist, return -1;
     */
    int find(int messageOffset);


    /**
     * add a new index entry
     */
    void addEntry(int messageOffset, int fileOffset);

}
