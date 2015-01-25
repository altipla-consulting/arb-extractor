package com.altiplaconsulting.arbextractor;

import com.google.javascript.jscomp.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class ExtractMessages {

    private static List<JsMessage> extractedMessages = new ArrayList<JsMessage>();

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        CompilerOptions options = new CompilerOptions();
        options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);

        GoogleJsMessageIdGenerator idGenerator = new GoogleJsMessageIdGenerator(args[0]);
        JsMessageExtractor extractor = new JsMessageExtractor(idGenerator, JsMessage.Style.CLOSURE, options);

        PrintStream stdout = new PrintStream(System.out, true, "UTF-8");

        for (String filename : Arrays.copyOfRange(args, 2, args.length)) {
            Collection<JsMessage> messages = extractor.extractMessages(SourceFile.fromFile(filename));
            for (JsMessage message : messages) {
                boolean found = false;
                for (JsMessage extractedMessage : extractedMessages) {
                    if (extractedMessage.getId().equals(message.getId())) {
                        if (!extractedMessage.getDesc().equals(message.getDesc())) {
                            stdout.println("different message descriptions with the same id: <" +
                                    extractedMessage.getDesc() + "> != <" + message.getDesc() + ">\n\tin files " +
                                    extractedMessage.getSourceName() + " and " + message.getSourceName() + "\n\t" +
                                    "with values: <" + extractedMessage.toString() + "> and <" + message.toString() +
                                    ">");
                        }

                        found = true;
                    }
                }

                if (!found) {
                    extractedMessages.add(message);
                }
            }
        }

        File file = new File(args[1]);
        PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), true, "UTF-8");

        out.println("{");
        out.println("\t\"@@locale\": \"es\",");
        out.println();

        for (JsMessage message : extractedMessages) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence part : message.parts()) {
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

            out.println("\t\"" + message.getId() + "\": \"" + value + "\",");
            out.println("\t\"@" + message.getId() + "\": {");
            out.println("\t\t\"type\": \"text\",");
            out.println("\t\t\"x-file\": \"" + message.getSourceName() + "\",");
            out.println("\t\t\"x-key\": \"" + message.getKey() + "\",");
            out.println("\t\t\"description\": \"" + message.getDesc() + "\"");
            out.println("\t},");
            out.println();
        }

        // Last to avoid the trailing comma error in the generated JSON
        out.println("\t\"@@context\": \"arb-extractor messages\"");

        out.println("}");
    }

}
