package com.github.stepancheg.protobuftojson.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Stepan Koltsov
 */
public class JsonUtilsTest {

    @Test
    public void quote() {
        Assert.assertEquals("\"\"", JsonUtils.quote(""));
        Assert.assertEquals("\"1\"", JsonUtils.quote("1"));
        Assert.assertEquals("\"1\\\"\"", JsonUtils.quote("1\""));
    }

}
