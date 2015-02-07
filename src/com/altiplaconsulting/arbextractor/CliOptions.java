package com.altiplaconsulting.arbextractor;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.List;

public class CliOptions {

    @Option(name = "--translations_project", usage = "name of the translations project")
    private String translationsProject = "";

    @Option(name = "--output_file", usage = "output file in ARB format with all the messages")
    private String outputFile = "";

    @Option(name = "--closure_entry_point", usage = "entry point of the Closure application")
    private String entryPoint = "";

    @Option(name = "--soy_entry_point", usage = "entry point of the Soy extracted messages")
    private String soyEntryPoint = "";

    @Argument
    private List<String> files = new ArrayList<String>();

    public String getTranslationsProject() {
        return translationsProject;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public List<String> getFiles() {
        return files;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public String getSoyEntryPoint() {
        return soyEntryPoint;
    }
}
