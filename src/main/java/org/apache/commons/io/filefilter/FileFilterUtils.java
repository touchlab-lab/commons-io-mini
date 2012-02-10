/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Useful utilities for working with file filters. It provides access to all
 * file filter implementations in this package so you don't have to import
 * every class you use.
 * 
 * @since Commons IO 1.0
 * @version $Id: FileFilterUtils.java 1005099 2010-10-06 16:13:01Z niallp $
 * 
 * @author Stephen Colebourne
 * @author Jeremias Maerki
 * @author Masato Tezuka
 * @author Rahul Akolkar
 */
public class FileFilterUtils {
    
    /**
     * FileFilterUtils is not normally instantiated.
     */
    public FileFilterUtils() {
    }

    //-----------------------------------------------------------------------

    /**
     * <p>
     * Applies an {@link IOFileFilter} to the provided {@link File} 
     * objects. The resulting array is a subset of the original file list that 
     * matches the provided filter.
     * </p>
     * 
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     * 
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the array of files to apply the filter to.
     * 
     * @return a subset of <code>files</code> that is accepted by the 
     *         file filter.
     * @throws IllegalArgumentException if the filter is <code>null</code> 
     *         or <code>files</code> contains a <code>null</code> value. 
     * 
     * @since Commons IO 2.0
     */
    public static File[] filter(IOFileFilter filter, File... files) {
        if (filter == null) {
            throw new IllegalArgumentException("file filter is null");
        }
        if (files == null) {
            return new File[0];
        }
        List<File> acceptedFiles = new ArrayList<File>();
        for (File file : files) {
            if (file == null) {
                throw new IllegalArgumentException("file array contains null");
            }
            if (filter.accept(file)) {
                acceptedFiles.add(file);
            }
        }
        return acceptedFiles.toArray(new File[acceptedFiles.size()]);
    }

    /**
     * <p>
     * Applies an {@link IOFileFilter} to the provided {@link File} 
     * objects. The resulting array is a subset of the original file list that 
     * matches the provided filter.
     * </p>
     * 
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     * 
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the array of files to apply the filter to.
     * 
     * @return a subset of <code>files</code> that is accepted by the 
     *         file filter.
     * @throws IllegalArgumentException if the filter is <code>null</code> 
     *         or <code>files</code> contains a <code>null</code> value. 
     * 
     * @since Commons IO 2.0
     */
    public static File[] filter(IOFileFilter filter, Iterable<File> files) {
        List<File> acceptedFiles = filterList(filter, files);
        return acceptedFiles.toArray(new File[acceptedFiles.size()]);
    }

    /**
     * <p>
     * Applies an {@link IOFileFilter} to the provided {@link File} 
     * objects. The resulting list is a subset of the original files that 
     * matches the provided filter.
     * </p>
     * 
     * <p>
     * The {@link List} returned by this method is not guaranteed to be thread safe.
     * </p>
     * 
     * <pre>
     * List&lt;File&gt; filesAndDirectories = ...
     * List&lt;File&gt; directories = FileFilterUtils.filterList(filesAndDirectories,
     *     FileFilterUtils.directoryFileFilter());
     * </pre>
     * @param filter the filter to apply to each files in the list.
     * @param files the collection of files to apply the filter to.
     * 
     * @return a subset of <code>files</code> that is accepted by the 
     *         file filter.
     * @throws IllegalArgumentException if the filter is <code>null</code> 
     *         or <code>files</code> contains a <code>null</code> value. 
     * @since Commons IO 2.0
     */
    public static List<File> filterList(IOFileFilter filter, Iterable<File> files) {
        return filter(filter, files, new ArrayList<File>());
    }

    /**
     * <p>
     * Applies an {@link IOFileFilter} to the provided {@link File} 
     * objects. The resulting list is a subset of the original files that 
     * matches the provided filter.
     * </p>
     * 
     * <p>
     * The {@link List} returned by this method is not guaranteed to be thread safe.
     * </p>
     * 
     * <pre>
     * List&lt;File&gt; filesAndDirectories = ...
     * List&lt;File&gt; directories = FileFilterUtils.filterList(filesAndDirectories,
     *     FileFilterUtils.directoryFileFilter());
     * </pre>
     * @param filter the filter to apply to each files in the list.
     * @param files the collection of files to apply the filter to.
     * 
     * @return a subset of <code>files</code> that is accepted by the 
     *         file filter.
     * @throws IllegalArgumentException if the filter is <code>null</code> 
     *         or <code>files</code> contains a <code>null</code> value. 
     * @since Commons IO 2.0
     */
    public static List<File> filterList(IOFileFilter filter, File... files) {
        File[] acceptedFiles = filter(filter, files);
        return Arrays.asList(acceptedFiles);
    }

    /**
     * <p>
     * Applies an {@link IOFileFilter} to the provided {@link File} 
     * objects. The resulting set is a subset of the original file list that 
     * matches the provided filter.
     * </p>
     * 
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     * 
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the collection of files to apply the filter to.
     * 
     * @return a subset of <code>files</code> that is accepted by the 
     *         file filter.
     * @throws IllegalArgumentException if the filter is <code>null</code> 
     *         or <code>files</code> contains a <code>null</code> value. 
     * 
     * @since Commons IO 2.0
     */
    public static Set<File> filterSet(IOFileFilter filter, File... files) {
        File[] acceptedFiles = filter(filter, files);
        return new HashSet<File>(Arrays.asList(acceptedFiles));
    }

    /**
     * <p>
     * Applies an {@link IOFileFilter} to the provided {@link File} 
     * objects. The resulting set is a subset of the original file list that 
     * matches the provided filter.
     * </p>
     * 
     * <p>
     * The {@link Set} returned by this method is not guaranteed to be thread safe.
     * </p>
     * 
     * <pre>
     * Set&lt;File&gt; allFiles = ...
     * Set&lt;File&gt; javaFiles = FileFilterUtils.filterSet(allFiles,
     *     FileFilterUtils.suffixFileFilter(".java"));
     * </pre>
     * @param filter the filter to apply to the set of files.
     * @param files the collection of files to apply the filter to.
     * 
     * @return a subset of <code>files</code> that is accepted by the 
     *         file filter.
     * @throws IllegalArgumentException if the filter is <code>null</code> 
     *         or <code>files</code> contains a <code>null</code> value. 
     * 
     * @since Commons IO 2.0
     */
    public static Set<File> filterSet(IOFileFilter filter, Iterable<File> files) {
        return filter(filter, files, new HashSet<File>());
    }

    /**
     * <p>
     * Applies an {@link IOFileFilter} to the provided {@link File} 
     * objects and appends the accepted files to the other supplied collection. 
     * </p>
     * 
     * <pre>
     * List&lt;File&gt; files = ...
     * List&lt;File&gt; directories = FileFilterUtils.filterList(files,
     *     FileFilterUtils.sizeFileFilter(FileUtils.FIFTY_MB), 
     *         new ArrayList&lt;File&gt;());
     * </pre>
     * @param filter the filter to apply to the collection of files.
     * @param files the collection of files to apply the filter to.
     * @param acceptedFiles the list of files to add accepted files to.
     * 
     * @param <T> the type of the file collection.
     * @return a subset of <code>files</code> that is accepted by the 
     *         file filter.
     * @throws IllegalArgumentException if the filter is <code>null</code> 
     *         or <code>files</code> contains a <code>null</code> value. 
     */
    private static <T extends Collection<File>> T filter(IOFileFilter filter,
            Iterable<File> files, T acceptedFiles) {
        if (filter == null) {
            throw new IllegalArgumentException("file filter is null");
        }
        if (files != null) {
            for (File file : files) {
                if (file == null) {
                    throw new IllegalArgumentException("file collection contains null");
                }
                if (filter.accept(file)) {
                    acceptedFiles.add(file);
                }
            }
        }
        return acceptedFiles;
    }



}
