package copy;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Path;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Hello world!
 *
 */
public class App 
{

    private static String source = "";
    private static String target = "";
    private static Path sourcePath = null;
    private static Path targetPath = null;
    private static File sourceFile = null;
    private static File targetFile = null;
    private static List<Path> sourceFiles = null;

    private static List<String> copyFileTypes = Stream.of(
            "copyByStandardCopyOption",
            "copyByFileInputStream",
            "copyByFileChannel"
        ).collect(Collectors.toList());
    private static List<String> copyDirectoryTypes = Stream.of(
            "copyByFileUtils",
            "copyBySpringUtilsRecursively"
        ).collect(Collectors.toList());

    private static final Logger logger = LoggerFactory.getLogger(App.class);


    public static void main( String[] args ) throws Exception {
        logger.info("------Start benchmark application------");
        if (args.length < 2) {
            logger.info("Please add the source and target parameters");
            System.exit(1);
        }
        source = args[0];
        target = args[1];

        if (!doesExists()) System.exit(1);

        logger.info("Source and Target parameters are checked");

        logger.info("Source size: " + getFolderSize(sourcePath) + "MB");

        runBenchmark();
    }

    public static void runBenchmark() throws Exception {
        copyFunctions func = new copyFunctions();
        String bestFileType = "";
        String bestDirType = "";
        long bestFileTime = -1;
        long bestDirTime = -1;
        for (String type : copyDirectoryTypes) {
            long totalTime = callCopyDirTest(type, func);

            if (bestDirTime == -1) {
                bestDirTime = totalTime;
                bestDirType = type;
            } else if (bestDirTime > totalTime) {
                bestDirTime = totalTime;
                bestDirType = type;
            }
        }

        for (String type : copyFileTypes) {
            long totalTime = callCopyFileTest(type, func);
            if (bestFileTime == -1) {
                bestFileTime = totalTime;
                bestFileType = type;
            } else if (bestFileTime > totalTime) {
                bestFileTime = totalTime;
                bestFileType = type;
            }
        }

        logger.info("------");
        logger.info("The best directory copy was the " + bestDirType + " and the run time was in Seconds: " + TimeUnit.SECONDS.convert(bestDirTime, TimeUnit.NANOSECONDS) + " / in Milliseconds: " + TimeUnit.MILLISECONDS.convert(bestDirTime, TimeUnit.NANOSECONDS));
        logger.info("The best file copy was the " + bestFileType + " and the run time was in Seconds: " + TimeUnit.SECONDS.convert(bestFileTime, TimeUnit.NANOSECONDS) + " / in Milliseconds: " + TimeUnit.MILLISECONDS.convert(bestFileTime, TimeUnit.NANOSECONDS));
    }

    public static long callCopyDirTest(String type, copyFunctions func) throws Exception {
        logger.info("------");
        logger.info("Run " + type + " benchmark test");
        long startTime = System.nanoTime();

        func.getClass().getMethod(type).invoke(func);

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        if (getFolderSize(sourcePath) != getFolderSize(targetPath)) logger.error("Copy not done properly");
        logger.info("Done with the " + type + " benchmark test. Run time in Seconds: " + TimeUnit.SECONDS.convert(totalTime, TimeUnit.NANOSECONDS) + " / in Milliseconds: " + TimeUnit.MILLISECONDS.convert(totalTime, TimeUnit.NANOSECONDS));

        deleteTarget();
        return totalTime;
    }

    public static long callCopyFileTest(String type, copyFunctions func) throws Exception {
        logger.info("------");
        logger.info("Run " + type + " benchmark test");
        long startTime = System.nanoTime();

        func.getClass().getMethod(type).invoke(func);

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        if (getFolderSize(sourcePath) != getFolderSize(targetPath)) logger.error("Copy not done properly");
        logger.info("Done with the " + type + " benchmark test. Run time in Seconds: " + TimeUnit.SECONDS.convert(totalTime, TimeUnit.NANOSECONDS) + " / in Milliseconds: " + TimeUnit.MILLISECONDS.convert(totalTime, TimeUnit.NANOSECONDS));

        deleteTarget();
        return totalTime;
    }

    public static void deleteTarget() throws IOException {
        logger.info("Deleting target folder");
        FileUtils.cleanDirectory(targetFile);
    }

    public static boolean doesExists() throws IOException {
        boolean exists = true;
        if (source.equals("")) {
            logger.info("Source parameter missing");
            exists = false;
        }
        if (target.equals("")) {
            logger.info("Target parameter missing");
            exists = false;
        }
        if (exists) {
            setFileVariables();

            if (Files.notExists(sourcePath)) {
                logger.info("Source folder doesn't exist");
                exists = false;
            }
            if (Files.notExists(targetPath)) {
                logger.info("Target folder doesn't exist");
                exists = false;
            }
        }
        return exists;
    }

    public static long getFolderSize(Path directory) throws IOException {
        long size = Files.walk(directory)
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
        return size / 1024 / 1024;
    }

    public static void setFileVariables() throws IOException {
        sourcePath = Paths.get(source);
        targetPath = Paths.get(target);
        sourceFile = new File(source);
        targetFile = new File(target);

        sourceFiles = Files.walk(sourcePath).collect(Collectors.toList());
    }

    public static String getSource() {
        return source;
    }

    public static void setSource(String source) {
        App.source = source;
    }

    public static String getTarget() {
        return target;
    }

    public static void setTarget(String target) {
        App.target = target;
    }

    public static Path getSourcePath() {
        return sourcePath;
    }

    public static void setSourcePath(Path sourcePath) {
        App.sourcePath = sourcePath;
    }

    public static Path getTargetPath() {
        return targetPath;
    }

    public static void setTargetPath(Path targetPath) {
        App.targetPath = targetPath;
    }

    public static File getSourceFile() {
        return sourceFile;
    }

    public static void setSourceFile(File sourceFile) {
        App.sourceFile = sourceFile;
    }

    public static File getTargetFile() {
        return targetFile;
    }

    public static void setTargetFile(File targetFile) {
        App.targetFile = targetFile;
    }

    public static List<Path> getSourceFiles() {
        return sourceFiles;
    }

    public static void setSourceFiles(List<Path> sourceFiles) {
        App.sourceFiles = sourceFiles;
    }
}
