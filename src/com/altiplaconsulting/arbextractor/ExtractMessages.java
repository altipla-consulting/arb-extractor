package com.altiplaconsulting.arbextractor;

import com.google.javascript.jscomp.*;

import java.io.IOException;
import java.util.Collection;

public class ExtractMessages {

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

//{
//        "@@context": "messages",
//        "MSG_88B45QOLWVVO": "English",
//        "@MSG_88B45QOLWVVO": {
//        "type": "text",
//        "context": "example.soy.js",
//        "description": "Traducible al inglés"
//        }
//        }
//    public static void main(String[] args) throws IOException {
//        JsMessageExtractor extractor = new JsMessageExtractor(
//                new GoogleJsMessageIdGenerator(args[0]), JsMessage.Style.CLOSURE);
//        Collection<JsMessage> messages = extractor.extractMessages(
//                SourceFile.fromFile(args[1]));
//        for (JsMessage message : messages) {
//            System.out.println("desc: " + message.getDesc());
//            System.out.println("id: " + message.getId());
//            System.out.println("key: " + message.getKey());
//            System.out.println("source name: " + message.getSourceName());
//            System.out.println("Parts:");
//            for (CharSequence cs : message.parts()) {
//                System.out.println("...." + cs.toString());
//            }
//        }
//    }
