package star.team.chat.dto.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import star.team.chat.dto.GeneralMessageDTO;
import star.team.chat.dto.UpdateReadTimeMessageDTO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GeneralMessageDTO.class, name = "general"),
        @JsonSubTypes.Type(value = UpdateReadTimeMessageDTO.class, name = "readTimeUpdate")
})
public interface ChatMessage {
    Long teamId();
}