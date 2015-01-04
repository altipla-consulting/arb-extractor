package com.altiplaconsulting.arbextractor;

import com.google.javascript.jscomp.*;

import java.io.IOException;
import java.util.Collection;

class ExtractMessages {

    public static void main(String[] args) throws IOException {
        CompilerOptions options = new CompilerOptions();
        options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);

        GoogleJsMessageIdGenerator idGenerator = new GoogleJsMessageIdGenerator(args[0]);
        JsMessageExtractor extractor = new JsMessageExtractor(idGenerator, JsMessage.Style.CLOSURE, options);
        Collection<JsMessage> messages = extractor.extractMessages(SourceFile.fromFile(args[1]));

        System.out.println("{");
        System.out.println("\"@@locale\": \"es\",");

        for (JsMessage message : messages) {
            System.out.println("\"" + message.getKey() + "\": \"" + message.toString() + "\",");
            System.out.println("\"@" + message.getKey() + "\": {");
            System.out.println("\"type\": \"text\",");
            System.out.println("\"context\": \"" + message.getSourceName() + "\",");
            System.out.println("\"description\": \"" + message.getDesc() + "\"");
            System.out.println("},");
        }

        // Last to avoid the trailing comma in the JSON
        System.out.println("\"@@context\": \"arb-extractor messages\"");

        System.out.println("}");
    }

}
