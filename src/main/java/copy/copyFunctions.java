package copy;

import org.apache.commons.io.FileUtils;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class copyFunctions {
    public static String target = App.getTarget();
    public static Path sourcePath = App.getSourcePath();
    public static Path targetPath = App.getTargetPath();
    public static File sourceFile = App.getSourceFile();
    public static File targetFile = App.getTargetFile();
    public static List<Path> sourceFiles = App.getSourceFiles();

    public static void copyByFileUtils() throws IOException {
        FileUtils.copyDirectory(sourceFile, targetFile);
    }

    public static void copyBySpringUtilsRecursively() throws IOException {
        FileSystemUtils.copyRecursively(sourceFile, targetFile);
    }

    public static void copyByStandardCopyOption() throws IOException {
        for (Path sourceFileName : sourceFiles)
            Files.copy(sourceFileName, Paths.get(target + sourceFileName.getFileName().toString()), REPLACE_EXISTING);
    }

    public static void copyByFileInputStream() throws IOException {
        for (Path sourceFileName : sourceFiles) {
            File file = sourceFileName.toFile();
            if (file.isFile()) {
                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(new File(target + sourceFileName.getFileName().toString()));

                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            } else if (file.isDirectory()) Files.copy(file.toPath(), Paths.get(target + sourceFileName.getFileName().toString()), REPLACE_EXISTING);
        }
    }

    public static void copyByFileChannel() throws IOException {
        for (Path sourceFileName : sourceFiles) {
            File file = sourceFileName.toFile();
            if (file.isFile()) {
                FileChannel sourceChannel = new FileInputStream(file).getChannel();
                FileChannel targetChannel = new FileOutputStream(new File(target + sourceFileName.getFileName().toString())).getChannel();
                targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

                sourceChannel.close();
                targetChannel.close();
            } else if (file.isDirectory()) Files.copy(file.toPath(), Paths.get(target + sourceFileName.getFileName().toString()), REPLACE_EXISTING);
        }
    }
}
