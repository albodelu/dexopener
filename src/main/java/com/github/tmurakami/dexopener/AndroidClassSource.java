package com.github.tmurakami.dexopener;

import android.support.annotation.NonNull;

import com.github.tmurakami.classinjector.ClassFile;
import com.github.tmurakami.classinjector.ClassSource;
import com.github.tmurakami.classinjector.ClassSources;
import com.github.tmurakami.dexopener.repackaged.org.ow2.asmdex.ApplicationReader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.github.tmurakami.dexopener.repackaged.org.ow2.asmdex.Opcodes.ASM4;

final class AndroidClassSource implements ClassSource {

    private final String sourceDir;
    private final ClassNameFilter classNameFilter;
    private final DexClassSourceFactory dexClassSourceFactory;
    private ClassSource delegate;

    AndroidClassSource(String sourceDir,
                       ClassNameFilter classNameFilter,
                       DexClassSourceFactory dexClassSourceFactory) {
        this.sourceDir = sourceDir;
        this.classNameFilter = classNameFilter;
        this.dexClassSourceFactory = dexClassSourceFactory;
    }

    @Override
    public ClassFile getClassFile(@NonNull String className) throws IOException {
        return classNameFilter.accept(className) ? getDelegate().getClassFile(className) : null;
    }

    private ClassSource getDelegate() throws IOException {
        ClassSource source = delegate;
        if (source == null) {
            source = delegate = newDelegate();
        }
        return source;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private ClassSource newDelegate() throws IOException {
        List<ClassSource> sources = new ArrayList<>();
        ClassNameReader r = new ClassNameReader(classNameFilter);
        ZipInputStream in = new ZipInputStream(new FileInputStream(sourceDir));
        try {
            for (ZipEntry e; (e = in.getNextEntry()) != null; ) {
                String name = e.getName();
                if (name.startsWith("classes") && name.endsWith(".dex")) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[16384];
                    for (int l; (l = in.read(buffer)) != -1; ) {
                        out.write(buffer, 0, l);
                    }
                    ApplicationReader ar = new ApplicationReader(ASM4, out.toByteArray());
                    sources.add(dexClassSourceFactory.newClassSource(ar, r.readClassNames(ar)));
                }
            }
        } finally {
            in.close();
        }
        return new ClassSources(sources);
    }

}