/*
 * Copyright 2004 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp;

import com.altiplaconsulting.arbextractor.AltiplaJsMessage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.javascript.rhino.JSDocInfo;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Extracts messages and message comments from JS code.
 * <p/>
 * <p> Uses a special prefix (e.g. {@code MSG_}) to determine which variables
 * are messages. Here are the recognized formats:
 * <p/>
 * <code>
 * var MSG_FOO = "foo";
 * var MSG_FOO_HELP = "this message is used for foo";
 * </code>
 * <p/>
 * <code>
 * var MSG_BAR = function(a, b) {
 * return a + " bar " + b;
 * }
 * var MSG_BAR_HELP = "the bar message";
 * </code>
 * <p/>
 * <p>This class enforces the policy that message variable names must be unique
 * across all JS files.
 */
public class AltiplaJsMessageExtractor {

    private final JsMessage.Style style;
    private final JsMessage.IdGenerator idGenerator;
    private final CompilerOptions options;

    public AltiplaJsMessageExtractor(
            JsMessage.IdGenerator idGenerator,
            JsMessage.Style style,
            CompilerOptions options) {
        this.idGenerator = idGenerator;
        this.style = style;
        this.options = options;
    }

    /**
     * Visitor that collects messages.
     */
    private class ExtractMessagesVisitor extends JsMessageVisitor {
        // We use List here as we want to preserve insertion-order for found
        // messages.
        // Take into account that messages with the same id could be present in the
        // result list. Message could have the same id only in case if they are
        // unnamed and have the same text but located in different source files.
        private final List<AltiplaJsMessage> messages = Lists.newLinkedList();

        private ExtractMessagesVisitor(AbstractCompiler compiler) {
            super(compiler, true, style, idGenerator);
        }

        @Override
        void processJsMessage(JsMessage message,
                              JsMessageDefinition definition) {
            JSDocInfo docInfo = definition.getMessageNode().getParent().getParent().getJSDocInfo();
            String blockDescription = docInfo.getBlockDescription();

            String id = null;
            if (blockDescription != null && blockDescription.startsWith("SOY MESSAGE ID: ")) {
                id = blockDescription.substring("SOY MESSAGE ID: ".length()).trim();
            }

            if (!message.isExternal()) {
                messages.add(new AltiplaJsMessage(id, message));
            }
        }

        /**
         * Returns extracted messages.
         *
         * @return collection of JsMessage objects that was found in js sources.
         */
        public Collection<AltiplaJsMessage> getMessages() {
            return messages;
        }
    }

    /**
     * Extracts JS messages from JavaScript code.
     */
    public Collection<AltiplaJsMessage> extractMessages(SourceFile... inputs)
            throws IOException {
        return extractMessages(ImmutableList.copyOf(inputs));
    }


    /**
     * Extracts JS messages from JavaScript code.
     *
     * @param inputs the JavaScript source code inputs
     * @return the extracted messages collection
     * @throws RuntimeException if there are problems parsing the JS code or the
     *                          JS messages, or if two messages have the same key
     */
    public <T extends SourceFile> Collection<AltiplaJsMessage> extractMessages(Iterable<T> inputs) {
        Compiler compiler = new Compiler();
        compiler.init(
                ImmutableList.<SourceFile>of(),
                Lists.newArrayList(inputs),
                options);
        compiler.parseInputs();

        ExtractMessagesVisitor extractCompilerPass =
                new ExtractMessagesVisitor(compiler);
        if (compiler.getErrors().length == 0) {
            extractCompilerPass.process(null, compiler.getRoot());
        }

        JSError[] errors = compiler.getErrors();
        // Check for errors.
        if (errors.length > 0) {
            StringBuilder msg = new StringBuilder("JSCompiler errors\n");
            MessageFormatter formatter = new LightweightMessageFormatter(compiler);
            for (JSError e : errors) {
                msg.append(formatter.formatError(e));
            }
            throw new RuntimeException(msg.toString());
        }

        return extractCompilerPass.getMessages();
    }
}
