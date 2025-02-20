package kr.ac.dankook.VettAuthServer.util;

import lombok.NonNull;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Set;

public class DecryptConverter implements ConditionalGenericConverter {

    @Override
    public boolean matches(
            @NonNull
            final TypeDescriptor sourceType,
            final TypeDescriptor targetType) {
        return targetType.hasAnnotation(DecryptId.class);
    }

    @NonNull
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(
                new ConvertiblePair(String.class, Long.class)
        );
    }

    @NonNull
    @Override
    public Object convert(
            @NonNull final Object source,
            @NonNull final TypeDescriptor sourceType,
            @NonNull final TypeDescriptor targetType) {
        try {
            return EncryptionUtil.decrypt((String)source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
