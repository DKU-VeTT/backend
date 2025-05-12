package kr.ac.dankook.VettLLMChatServer.config.converter;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@WritingConverter
public class LocalDateTimeToDateKstConverter implements Converter<LocalDateTime, Date> {

    @Override
    @NonNull
    public Date convert(LocalDateTime source) {
        return Timestamp.valueOf(source.plusHours(9));
    }
}