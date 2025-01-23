package io.github.residentadvisor.strip_unicode_null_transform;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;

import java.util.Map;

public class StripUnicodeNullTransform<R extends ConnectRecord<R>> implements Transformation<R> {

    @Override
    public R apply(R record) {
        if (record.value() == null || !(record.value() instanceof Struct)) {
            return record;
        }

        Struct struct = (Struct) record.value();

        for (Field field : struct.schema().fields()) {
            Object value = struct.get(field);
            if (field.name().equals("after")) {
                Struct after = (Struct) value;
                Struct newAfter = replaceNullBytes(after);
                struct.put(field, newAfter);
            }
        }

        return record.newRecord(
            record.topic(),
            record.kafkaPartition(),
            record.keySchema(),
            record.key(),
            record.valueSchema(),
            struct,
            record.timestamp());
    }
    
    private Struct replaceNullBytes(Struct struct) {
        Struct newStruct = new Struct(struct.schema());
        
        for (Field field : struct.schema().fields()) {
            Object value = struct.get(field);
            if (value instanceof String) {
                String strValue = (String) value;
                newStruct.put(field, strValue.replace("\u0000", ""));
            } else {
                newStruct.put(field, value);
            }
        }
        
        return newStruct;
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }

}
