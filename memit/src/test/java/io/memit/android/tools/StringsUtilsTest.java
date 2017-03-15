package io.memit.android.tools;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by peter on 3/14/17.
 */

// @RunWith((JUnitParamsRunner.class))
public class StringsUtilsTest {


    @Test
    public void testJoin_Multiple(){
        List<String> list = Arrays.asList("1","2","3");

        String str = StringsUtils.join(list, ",");

        assertEquals("1,2,3", str);
    }

    @Test
    public void testJoin_Single(){
        List<String> list = Arrays.asList("1");

        String str = StringsUtils.join(list, ",");

        assertEquals("1", str);
    }


    @Test
    public void testtoCondition_Single(){
        List<String> list = Arrays.asList("1");

        String str = StringsUtils.joinConditions(list, "_id", "OR");

        assertEquals("_id=1", str);
    }


    @Test
    public void testtoCondition_Multiple(){
        List<String> list = Arrays.asList("1","2","3");

        String str = StringsUtils.joinConditions(list, "_id", "OR");

        assertEquals("_id=1 OR _id=2 OR _id=3", str);
    }

}
