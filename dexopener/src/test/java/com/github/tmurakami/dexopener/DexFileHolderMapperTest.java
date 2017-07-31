package com.github.tmurakami.dexopener;

import com.github.tmurakami.dexopener.repackaged.org.jf.dexlib2.Opcodes;
import com.github.tmurakami.dexopener.repackaged.org.jf.dexlib2.iface.ClassDef;
import com.github.tmurakami.dexopener.repackaged.org.jf.dexlib2.iface.DexFile;
import com.github.tmurakami.dexopener.repackaged.org.jf.dexlib2.immutable.ImmutableClassDef;
import com.github.tmurakami.dexopener.repackaged.org.jf.dexlib2.immutable.ImmutableDexFile;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SuppressWarnings("deprecation")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class DexFileHolderMapperTest {

    @InjectMocks
    private DexFileHolderMapper testTarget;

    @Mock
    private ClassNameFilter classNameFilter;
    @Mock
    private Executor executor;
    @Mock
    private DexFileTaskFactory dexFileTaskFactory;
    @Mock
    private FutureTask<dalvik.system.DexFile> task;
    @Mock
    private dalvik.system.DexFile dexFile;

    @Captor
    private ArgumentCaptor<DexFile> dexFileCaptor;

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    @Test
    public void map_should_collect_DexFileHolders() throws Exception {
        Set<ClassDef> classes = new HashSet<>();
        int classCount = 151;
        for (int i = 0; i < classCount; i++) {
            String className = "foo.Bar" + i;
            classes.add(new ImmutableClassDef(TypeUtils.getInternalName(className),
                                              0,
                                              null,
                                              null,
                                              null,
                                              null,
                                              null,
                                              null));
        }
        byte[] bytecode = DexPoolUtils.toBytecode(new ImmutableDexFile(Opcodes.getDefault(), classes));
        given(classNameFilter.accept(anyString())).willReturn(true);
        given(dexFileTaskFactory.newDexFileTask(dexFileCaptor.capture())).willReturn(task);
        given(task.get()).willReturn(dexFile);
        Map<String, DexFileHolder> holderMap = new HashMap<>();
        testTarget.map(bytecode, holderMap);
        assertEquals(classCount, holderMap.size());
        List<DexFile> dexFileList = dexFileCaptor.getAllValues();
        assertEquals(2, dexFileList.size());
        assertEquals(150, dexFileList.get(0).getClasses().size());
        assertEquals(1, dexFileList.get(1).getClasses().size());
        HashSet<DexFileHolder> holders = new HashSet<>(holderMap.values());
        assertEquals(2, holders.size());
        for (DexFileHolder holder : holders) {
            assertEquals(dexFile, holder.get());
        }
        then(task).should(times(2)).run();
        then(executor).should(times(2)).execute(task);
    }

}
