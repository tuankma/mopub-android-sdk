package com.mopub.mobileads.util;

import android.app.Activity;
import android.view.View;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static com.mopub.mobileads.util.Reflection.MethodBuilder;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;


@RunWith(RobolectricTestRunner.class)
public class ReflectionTest {
    private Activity activity;
    private View view;
    private String string;
    private MethodBuilder methodBuilder;

    @Before
    public void setup(){
        activity = new Activity();
        view = new View(activity);
        string = "goat";
    }

    @Test
    public void execute_withCorrectVoidMethodThatHasNoParameters_shouldPass() throws Exception {
        methodBuilder = new MethodBuilder(activity, "finish");

        methodBuilder.execute();

        // pass
    }

    @Test
    public void execute_withCorrectNonVoidMethodThatHasNoParameters_shouldPass() throws Exception {
        methodBuilder = new MethodBuilder(string, "length");

        int result = (Integer)methodBuilder.execute();

        assertThat(result).isEqualTo(4);
    }

    @Test
    public void execute_withCorrectVoidMethodThatHasParameters_shouldPass() throws Exception {
        methodBuilder = new MethodBuilder(view, "buildDrawingCache");
        methodBuilder.addParam(boolean.class, true);

        methodBuilder.execute();

        // pass
    }

    @Test
    public void execute_withCorrectNonVoidMethodThatHasParameters_shouldPass() throws Exception {
        methodBuilder = new MethodBuilder(string, "charAt");
        methodBuilder.addParam(int.class, 2);

        Object result = methodBuilder.execute();

        assertThat(result).isEqualTo('a');
    }

    @Test
    public void execute_withNoSuchMethod_shouldThrowException() throws Exception {
        methodBuilder = new MethodBuilder(activity, "noSuchMethod");

        try {
            methodBuilder.execute();
            fail("Should fail because method did not exist");
        } catch (Exception e) {
            // pass
        }
    }

    @Test
    public void execute_withCorrectVoidMethodThatHasParameters_withMissingParameters_shouldThrowException() throws Exception {
        methodBuilder = new MethodBuilder(activity, "finishActivity");
        // forget to add int requestCode parameter

        try {
            methodBuilder.execute();
            fail("Should fail because we did not supply all the parameters");
        } catch (Exception e) {
            // pass
        }
    }

    @Test
    public void execute_withExistingMethodButIncorrectParameterTypes_shouldThrowException() throws Exception {
        methodBuilder = new MethodBuilder(string, "concat");
        methodBuilder.addParam(Object.class, "other");

        try {
            methodBuilder.execute();
            fail("Should fail because there is no string.concat(Object) method");
        } catch (Exception e) {
            // pass
        }
    }

    @Test
    public void execute_withExistingMethodButSubclassedParameter_shouldPass() throws Exception {
        methodBuilder = new MethodBuilder(string, "equals");
        methodBuilder.addParam(Object.class, "cheese");

        boolean result = (Boolean) methodBuilder.execute();

        assertThat(result).isFalse();
    }

    @Test
    public void execute_withCorrectMethodThatHasParameters_withIncorrectOrderingOfParameters_shouldThrowException() throws Exception {
        methodBuilder = new MethodBuilder(string, "indexOf");
        methodBuilder.addParam(int.class, 2);
        methodBuilder.addParam(String.class, "g");

        try {
            methodBuilder.execute();
            fail("Should fail because we expected string.indexOf(String, int) instead of string.indexOf(int, String)");
        } catch (Exception e) {
            // pass
        }
    }

    @Test
    public void execute_withNullInstanceOnInstanceMethod_shouldThrowException() throws Exception {
        methodBuilder = new MethodBuilder(null, "length");

        try {
            methodBuilder.execute();
            fail("Should fail because we are giving a null instance");
        } catch (Exception e) {
            // pass
        }
    }
}
