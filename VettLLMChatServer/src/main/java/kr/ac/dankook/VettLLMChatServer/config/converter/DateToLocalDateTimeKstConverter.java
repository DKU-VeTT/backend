package kr.ac.dankook.VettLLMChatServer.config.converter;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@ReadingConverter

public class DateToLocalDateTimeKstConverter implements Converter<Date, LocalDateTime> {

    @Override
    @NonNull
    public LocalDateTime convert(Date source) {
        return source.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .minusHours(9);
    }
}
