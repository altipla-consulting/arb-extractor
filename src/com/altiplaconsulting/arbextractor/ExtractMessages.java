package com.altiplaconsulting.arbextractor;

import com.google.javascript.jscomp.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ExtractMessages {

    private static ArrayList<AltiplaJsMessage> extractedMessages = new ArrayList<AltiplaJsMessage>();

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        CliOptions cliOptions = new CliOptions();
        CmdLineParser parser = new CmdLineParser(cliOptions);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

        CompilerOptions options = new CompilerOptions();
        options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
        options.setIdeMode(true);

        if (!cliOptions.getEntryPoint().isEmpty()) {
            DependencyOptions depOptions = new DependencyOptions();
            depOptions.setDependencyPruning(true);
            depOptions.setDependencySorting(true);
            depOptions.setMoocherDropping(true);

            List<String> entryPoints = new ArrayList<String>();
            entryPoints.add(cliOptions.getEntryPoint());
            depOptions.setEntryPoints(entryPoints);

            options.setDependencyOptions(depOptions);
        }

        GoogleJsMessageIdGenerator idGenerator = new GoogleJsMessageIdGenerator(cliOptions.getTranslationsProject());
        AltiplaJsMessageExtractor extractor = new AltiplaJsMessageExtractor(idGenerator, JsMessage.Style.CLOSURE, options);

        for (String filename : cliOptions.getFiles()) {
            Collection<AltiplaJsMessage> messages = extractor.extractMessages(SourceFile.fromFile(filename));
            extractedMessages.addAll(messages);
        }

        File file = new File(cliOptions.getOutputFile());
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), true, "UTF-8");

        out.println("{");
        out.println("\t\"@@locale\": \"es\",");
        out.println();

        for (AltiplaJsMessage message : extractedMessages) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence part : message.getMessage().parts()) {
                if (part instanceof JsMessage.PlaceholderReference) {
                    StringBuilder ph = new StringBuilder();
                    ph.append('{');

                    char[] name = ((JsMessage.PlaceholderReference) part).getName().toCharArray();
                    for (char c : name) {
                        if (Character.isUpperCase(c)) {
                            ph.append('_');
                        }

                        ph.append(Character.toUpperCase(c));
                    }

                    ph.append('}');
                    sb.append(ph.toString());
                } else {
                    sb.append(part.toString());
                }
            }
            String value = sb.toString();

            out.println("\t\"" + message.getMessage().getKey() + "\": \"" + value + "\",");
            out.println("\t\"@" + message.getMessage().getKey() + "\": {");
            out.println("\t\t\"type\": \"text\",");
            out.println("\t\t\"x-file\": \"" + message.getMessage().getSourceName() + "\",");
            out.println("\t\t\"x-id\": \"" + message.getMessage().getId() + "\",");
            if (message.getSoyId() != null) {
                out.println("\t\t\"x-soy-id\": \"" + message.getSoyId() + "\",");
            }
            out.println("\t\t\"x-original\": \"" + message.getMessage().toString() + "\",");
            out.println("\t\t\"description\": \"" + message.getMessage().getDesc() + "\"");
            out.println("\t},");
            out.println();
        }

        // Last to avoid the trailing comma error in the generated JSON
        out.println("\t\"@@context\": \"arb-extractor messages\"");

        out.println("}");
    }

}
