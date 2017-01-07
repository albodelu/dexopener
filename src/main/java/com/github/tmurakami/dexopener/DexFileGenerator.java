package com.github.tmurakami.dexopener;

import com.github.tmurakami.dexopener.repackaged.org.ow2.asmdex.ApplicationReader;

import java.io.File;
import java.util.Collection;

import dalvik.system.DexFile;

interface DexFileGenerator {
    DexFile generateDexFile(ApplicationReader ar, File cacheDir, Collection<String> classesToVisit);
}