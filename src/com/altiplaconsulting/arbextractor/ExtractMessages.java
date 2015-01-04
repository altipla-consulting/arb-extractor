package com.altiplaconsulting.arbextractor;

import com.google.javascript.jscomp.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

class ExtractMessages {

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, "UTF-8");

        CompilerOptions options = new CompilerOptions();
        options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);

        GoogleJsMessageIdGenerator idGenerator = new GoogleJsMessageIdGenerator(args[0]);
        JsMessageExtractor extractor = new JsMessageExtractor(idGenerator, JsMessage.Style.CLOSURE, options);
        Collection<JsMessage> messages = extractor.extractMessages(SourceFile.fromFile(args[1]));

        out.println("{");
        out.println("\t\"@@locale\": \"es\",");
        out.println();

        for (JsMessage message : messages) {
            out.println("\t\"" + message.getKey() + "\": \"" + message.toString() + "\",");
            out.println("\t\"@" + message.getKey() + "\": {");
            out.println("\t\t\"type\": \"text\",");
            out.println("\t\t\"context\": \"" + message.getSourceName() + "\",");
            out.println("\t\t\"description\": \"" + message.getDesc() + "\"");
            out.println("\t},");
            out.println();
        }

        // Last to avoid the trailing comma in the JSON
        out.println("\t\"@@context\": \"arb-extractor messages\"");

        out.println("}");
    }

}
