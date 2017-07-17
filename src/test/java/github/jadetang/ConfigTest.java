package github.jadetang;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author sanguan.tangsicheng on 2017/7/16 下午11:35
 */
public class ConfigTest {

    @Test
    public void testGetDir() {
        String dir = Config.dataDir();
        Assert.assertNotNull(dir);

    }

}
