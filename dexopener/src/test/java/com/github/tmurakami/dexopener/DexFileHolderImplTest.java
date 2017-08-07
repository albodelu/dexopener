package com.github.tmurakami.dexopener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class DexFileHolderImplTest {

    private DexFileHolderImpl testTarget;

    @Mock
    private FutureTask<dalvik.system.DexFile> task;
    @Mock
    private dalvik.system.DexFile dexFile;

    @Before
    public void setUp() throws Exception {
        testTarget = new DexFileHolderImpl();
        testTarget.setTask(task);
    }

    @Test
    public void get_should_return_the_dex_file() throws Exception {
        given(task.get()).willReturn(dexFile);
        assertSame(dexFile, testTarget.get());
        then(task).should().run();
    }

    @Test
    public void get_should_return_the_dex_file_even_if_the_thread_is_interrupted() throws Exception {
        given(task.get()).willThrow(new InterruptedException()).willReturn(dexFile);
        assertSame(dexFile, testTarget.get());
        assertTrue(Thread.interrupted());
        then(task).should().run();
    }

    @Test(expected = IOException.class)
    public void get_should_throw_IOException_if_an_io_error_occurs_during_task_execution() throws Exception {
        given(task.get()).willThrow(new ExecutionException(new IOException()));
        testTarget.get();
    }

    @Test(expected = RuntimeException.class)
    public void get_should_throw_IOException_if_a_runtime_error_occurs_during_task_execution() throws Exception {
        given(task.get()).willThrow(new ExecutionException(new RuntimeException()));
        testTarget.get();
    }

    @Test(expected = Error.class)
    public void get_should_throw_IOException_if_an_error_occurs_during_task_execution() throws Exception {
        given(task.get()).willThrow(new ExecutionException(new Error()));
        testTarget.get();
    }

    @Test(expected = UndeclaredThrowableException.class)
    public void get_should_throw_IOException_if_an_unexpected_error_occurs_during_task_execution() throws Exception {
        given(task.get()).willThrow(new ExecutionException(new Throwable()));
        testTarget.get();
    }

}