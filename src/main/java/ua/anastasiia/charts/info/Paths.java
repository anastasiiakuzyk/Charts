package ua.anastasiia.charts.info;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class Paths {

    public static String[] getFilesPaths(String mainDirectory) {
        File[] directories = new File(mainDirectory).listFiles(File::isDirectory);
        assert directories != null;
//        System.out.println(Arrays.toString(directories));
        Arrays.sort(directories);
        List<String> regionsList = new ArrayList<>();
        for (File region : directories) {
            regionsList.addAll(Arrays.asList(ArrayUtils.addAll(getMonths(mainDirectory, region.getName()))));
        }
        return regionsList.toArray(new String[0]);
    }

    public static String getFilePath(String mainDirectory, String region, String fileName) {
        return mainDirectory + "//" + region + "/" + fileName;
    }

    public static String getMainDirectoryFromPath(String path) {
        return path.substring(0, path.indexOf("//"));
    }

    public static String getRegionFromPath(String path) {
        String str = "//";
        return path.substring(path.lastIndexOf(str) + str.length(), path.lastIndexOf("/"));
    }

    public static String getFileNameFromPath(String path) {
        String str = "/";
        return path.substring(path.lastIndexOf(str) + str.length());
    }

    private static String[] getMonths(String mainDirectory, String region) {
        String pathname = mainDirectory + "//" + region;
        File folder = new File(pathname);
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        Arrays.sort(listOfFiles, new Comparator<>() {
            @Override
            public int compare(File o1, File o2) {
                int n2 = extractNumber(o1.getName());
                int n1 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i;
                try {
                    int s = name.lastIndexOf('/') + 5;
                    int e = name.lastIndexOf('.');
                    String number = name.substring(s, e);
                    i = Integer.parseInt(number);
                } catch (Exception e) {
                    i = 0;
                }
                return i;
            }
        });
        String[] filePaths = new String[listOfFiles.length];
        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = getFilePath(mainDirectory, region, listOfFiles[i].getName());
        }
        return filePaths;
    }

}
