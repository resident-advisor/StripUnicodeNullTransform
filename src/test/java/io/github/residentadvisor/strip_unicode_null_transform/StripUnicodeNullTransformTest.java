package io.github.residentadvisor.strip_unicode_null_transform;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

public class StripUnicodeNullTransformTest {
        @Test
        public void testTransformWithAllFields() {
                try (StripUnicodeNullTransform<SinkRecord> transform = new StripUnicodeNullTransform<>()) {
                        // Arrange
                        transform.configure(new HashMap<String, String>());

                        Schema messageSchema = SchemaBuilder.struct()
                                .field("field1", Schema.STRING_SCHEMA)
                                .field("field2", Schema.STRING_SCHEMA)
                                .field("field3", Schema.STRING_SCHEMA)
                                .field("field4", Schema.STRING_SCHEMA)
                                .build();

                        Schema schema = SchemaBuilder.struct()
                                .field("before", messageSchema)
                                .field("after", messageSchema)
                                .field("source", Schema.STRING_SCHEMA)
                                .field("transaction", Schema.STRING_SCHEMA)
                                .build();

                        Struct inputValue = new Struct(schema)
                                .put("before", new Struct(messageSchema)
                                        .put("field1", "foo\u0000bar")
                                        .put("field2", "baz")
                                        .put("field3", "qux\\u0000quux")
                                        .put("field4", "ab\u0000xy"))
                                .put("after", new Struct(messageSchema)
                                        .put("field1", "foo\u0000bar")
                                        .put("field2", "baz")
                                        .put("field3", "qux\\u0000quux")
                                        .put("field4", "ab\u0000xy"))
                                .put("source", "test")
                                .put("transaction", "test");

                        Struct expectedValue = new Struct(schema)
                                .put("before", new Struct(messageSchema)
                                        .put("field1", "foo\u0000bar")
                                        .put("field2", "baz")
                                        .put("field3", "qux\\u0000quux")
                                        .put("field4", "ab\u0000xy"))
                                .put("after", new Struct(messageSchema)
                                        .put("field1", "foobar")
                                        .put("field2", "baz")
                                        .put("field3", "quxquux")
                                        .put("field4", "abxy"))
                                .put("source", "test")
                                .put("transaction", "test");

                        // Act
                        SinkRecord output = transform.apply(new SinkRecord("", 0, null, null, schema, inputValue, 0));
                        
                        // Assert
                        assertEquals(expectedValue.toString(), output.value().toString());
                }
        }
        @Test
        public void testTransformWithNullValue() {
                try (StripUnicodeNullTransform<SinkRecord> transform = new StripUnicodeNullTransform<>()) {
                        // Arrange
                        transform.configure(new HashMap<String, String>());

                        Schema schema = SchemaBuilder.struct()
                                .field("before", Schema.STRING_SCHEMA)
                                .field("after", Schema.STRING_SCHEMA)
                                .field("source", Schema.STRING_SCHEMA)
                                .field("transaction", Schema.STRING_SCHEMA)
                                .build();

                        // Act
                        SinkRecord output = transform.apply(new SinkRecord("", 0, null, null, schema, null, 0));

                        // Assert
                        assertNull(output.value());
                }
        }

        @Test
        public void testTransformWithNonStructValue() {
                try (StripUnicodeNullTransform<SinkRecord> transform = new StripUnicodeNullTransform<>()) {
                        // Arrange
                        transform.configure(new HashMap<String, String>());

                        Schema schema = Schema.STRING_SCHEMA;
                        String inputValue = "foobar";

                        String expectedValue = "foobar";

                        // Act
                        SinkRecord output = transform.apply(new SinkRecord("", 0, null, null, schema, inputValue, 0));

                        // Assert
                        assertEquals(expectedValue, output.value());
                }
        }
}